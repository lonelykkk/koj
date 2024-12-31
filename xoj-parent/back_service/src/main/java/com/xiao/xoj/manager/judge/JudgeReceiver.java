package com.xiao.xoj.manager.judge;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.common.result.ResultStatus;
import com.xiao.xoj.pojo.dto.TestJudgeReq;
import com.xiao.xoj.pojo.dto.TestJudgeRes;
import com.xiao.xoj.pojo.dto.ToJudgeDTO;
import com.xiao.xoj.pojo.entity.contest.ContestRecord;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.service.ContestRecordService;
import com.xiao.xoj.service.JudgeService;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 肖恩
 * @create 2023/7/27 16:01
 * @description: 评测命令接收者，执行分发命令：向判题服务器发起请求，并对请求结果进行处理
 */
@Component
@Slf4j(topic = "xoj")
public class JudgeReceiver {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private ContestRecordService contestRecordService;

    @Value("${judge-token}")
    private String judgeToken;

    private static final String[] QUEUES = {
            Constants.Queue.CONTEST_JUDGE_WAITING.getName(),
            Constants.Queue.GENERAL_JUDGE_WAITING.getName(),
            Constants.Queue.TEST_JUDGE_WAITING.getName()
    };


    private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(15);

    // 自定义线程池
    private final static ThreadPoolExecutor judgeThreadPool =
            new ThreadPoolExecutor(4, 8, 5,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());

    private final static Map<String, ScheduledFuture> futureTaskMap = new ConcurrentHashMap<>(15);

    // 任务提交失败重试次数
    private final static Integer maxTryNum = 150;

    @Async("judgeTaskAsyncPool")
    public void handleWaitingTask() {
        log.info("handleWaitingTask current Thread: " + Thread.currentThread().getName());
        //  1.考核评测 > 2.普通评测 > 3.在线调试
        for (String queueName : QUEUES) {
            String taskStr = null;
            // 查看该任务队列中是否有任务，如果任务取出队头任务， 如果没有任务，判断下一个任务队列是否有任务
            long size = redisUtils.lGetListSize(queueName);
            if (size > 0) {
                taskStr = (String) redisUtils.lrPop(queueName);
            } else {
                continue;
            }

            // 对评测数据进行封装，进行分发
            if (taskStr != null) {
                // 在线评测任务
                if (Constants.Queue.TEST_JUDGE_WAITING.getName().equals(queueName)) {
                    TestJudgeReq testJudgeReq = JSONUtil.toBean(taskStr, TestJudgeReq.class);
                    testJudgeReq.setToken(judgeToken);
                    dispatch(Constants.TaskType.TEST_JUDGE, testJudgeReq);
                } else {
                    // 普通评测任务
                    JSONObject task = JSONUtil.parseObj(taskStr);
                    Integer judgeId = task.getInt("judgeId");
                    Judge judge = judgeService.getById(judgeId);
                    if (judge != null) {
                        // 调度评测时发现该评测任务被取消，则结束评测
                        if (Objects.equals(judge.getStatus(), Constants.Judge.STATUS_CANCELLED.getStatus())) {
                            if (judge.getCid() != 0) {
                                UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
                                // 取消评测，不罚时也不算得分
                                updateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
                                updateWrapper.eq("submit_id", judge.getSubmitId()); // submit_id一定只有一个
                                contestRecordService.update(null, updateWrapper);
                            }
                        } else {
                            String token = task.getStr("token");
                            // 调用判题服务
                            dispatch(Constants.TaskType.JUDGE, new ToJudgeDTO()
                                    .setJudge(judge)
                                    .setToken(token));
                        }
                    }
                }
            }
        }
    }

    public void dispatch(Constants.TaskType taskType, Object data) {
        switch (taskType) {
            case JUDGE:
                defaultJudge((ToJudgeDTO) data, taskType.getPath());
                break;
            case TEST_JUDGE:
                testJudge((TestJudgeReq) data, taskType.getPath());
                break;
            default:
                throw new IllegalArgumentException("判题机不支持此调用类型");
        }
    }

    /**
     * @des: 普通评测
     *
     * @param data
     * @param path
     * @return
     */
    public void defaultJudge(ToJudgeDTO data, String path) {
        Integer submitId = data.getJudge().getSubmitId();
        AtomicInteger count = new AtomicInteger(0);
        String taskKey = UUID.randomUUID().toString() + submitId;

        System.out.println("===defaultJudge===");
        log.info("defaultJudge Thread: " + Thread.currentThread().getName());
        Runnable getResultTask = () -> {
            if (count.get() > maxTryNum) {
                checkResult(null, submitId);
                releaseTaskThread(taskKey);
                return;
            }
            count.incrementAndGet();
            String url = "http://127.0.0.1:9003/";
            data.setJudgeServerIp("127.0.0.1");
            data.setJudgeServerPort(9003);
            ResultData result = null;
            try {
                result = restTemplate.postForObject(url + path, data, ResultData.class);
            } catch (Exception e) {
                log.error("判题请求失败，请求地址: " + url, e);
            } finally {
                checkResult(result, submitId);
                releaseTaskThread(taskKey);
            }
        };

        ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(getResultTask, 0, 2, TimeUnit.SECONDS);// 2秒后执行
        futureTaskMap.put(taskKey, scheduledFuture);

//        Future<?> submit = judgeThreadPool.submit(getResultTask);
//        futureTaskMap.put(taskKey, submit);
    }


    /**
     * @des: 在线调试
     *
     * @param testJudgeReq
     * @param path
     * @return
     */
    public void testJudge(TestJudgeReq testJudgeReq, String path) {
        System.out.println("===testJudge===");
        AtomicInteger count = new AtomicInteger(0);
        String taskKey = testJudgeReq.getUniqueKey();

        Runnable getTestResultTask = () -> {
            if (count.get() > maxTryNum) {
                releaseTaskThread(taskKey);
                return;
            }
            count.incrementAndGet();
            String url = "http://127.0.0.1:9003/";
            try {
                JSONObject resultJson = restTemplate.postForObject(url + path, testJudgeReq, JSONObject.class);
                if (resultJson != null) {
                    if (resultJson.getInt("code").equals(ResultStatus.SUCCESS.getStatus())) {
                        System.out.println("testJudge success, result = " + JSONUtil.toJsonStr(resultJson));
                        // 将结果进行封装，并放入redis中
                        TestJudgeRes testJudgeRes = resultJson.getBean("data", TestJudgeRes.class);
                        testJudgeRes.setInput(testJudgeReq.getTestCaseInput());
                        testJudgeRes.setExpectedOutput(testJudgeReq.getExpectedOutput());
                        testJudgeRes.setProblemJudgeMode(testJudgeReq.getProblemJudgeMode());
                        redisUtils.set(testJudgeReq.getUniqueKey(), testJudgeRes, 60);
                    } else {
                        System.out.println("testJudge fail, result = " + JSONUtil.toJsonStr(resultJson));
                        // 将结果进行封装，并放入redis中
                        TestJudgeRes testJudgeRes = TestJudgeRes.builder()
                                .status(Constants.Judge.STATUS_SYSTEM_ERROR.getStatus())
                                .time(0L)
                                .memory(0L)
                                .stderr(resultJson.getStr("msg"))
                                .build();
                        redisUtils.set(testJudgeReq.getUniqueKey(), testJudgeRes, 60);
                    }
                }
            } catch (Exception e) { // 调用失败
                log.error("判题请求失败，请求地址: " + url, e);
                TestJudgeRes testJudgeRes = new TestJudgeRes()
                        .setStatus(Constants.Judge.STATUS_SYSTEM_ERROR.getStatus())
                        .setTime(0l)
                        .setMemory(0l)
                        .setStderr("评测提交失败，请再次提交！");
                redisUtils.set(testJudgeReq.getUniqueKey(), testJudgeRes, 60); // 将调用错误信息保存到redis中
            } finally {
                releaseTaskThread(taskKey);
            }
        };

        ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(getTestResultTask, 0, 1, TimeUnit.SECONDS);
        futureTaskMap.put(taskKey, scheduledFuture);
    }

    /**
     * @des: 检查评测请求响应结果
     *
     * @param result
     * @param submitId
     * @return
     */
    private void checkResult(ResultData<Void> result, Integer submitId) {
        Judge judge = new Judge();
        judge.setSubmitId(submitId);
        if (result == null) { // 调用失败
            judge.setStatus(Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus());
            judge.setErrorMessage("评测提交失败，请再次提交！");
            judgeService.updateById(judge);
        } else {
            // 如果是结果码不是20000 说明调用有错误
            if (result.getCode() != ResultStatus.SUCCESS.getStatus().intValue()) {
                // 判为系统错误
                judge.setStatus(Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
                judge.setErrorMessage(result.getMessage());
                judgeService.updateById(judge);
            }
        }
    }


    /**
     * @des: 移除任务，释放线程
     *
     * @param taskKey
     * @return
     */
    public void releaseTaskThread(String taskKey) {
        ScheduledFuture scheduledFuture = futureTaskMap.get(taskKey);
        if (scheduledFuture != null) {
            boolean cancel = scheduledFuture.cancel(true); // 如果任务没有执行，取消任务，如果任务正在执行，中断任务
            if (cancel) {
                futureTaskMap.remove(taskKey);
            }
        }
    }

}
