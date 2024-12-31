package com.xiao.xoj.manager.judge;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiao.xoj.common.exception.StatusSystemErrorException;
import com.xiao.xoj.pojo.dto.TestJudgeReq;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.service.JudgeService;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author 肖恩
 * @create 2023/4/27 19:58
 * @description: 分发评测任务，命令调用者
 */
@Component
@Slf4j(topic = "xoj")
public class JudgeDispatcher {

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private JudgeReceiver judgeReceiver;

    @Value("${judge-token}")
    private String judgeToken;

    public void sendTask(Integer judgeId, Integer pid, Boolean isContest) {

        // 将数据放入评测队列中（左进右出）评测任务有优先级：考核评测 > 普通评测 > 在线调试
        // 如果放入失败，需要修改该次评测的状态：STATUS_SUBMITTED_FAILED

        JSONObject task = new JSONObject();
        task.set("judgeId", judgeId);
        task.set("token", judgeToken);
        task.set("isContest", isContest);

        try {
            boolean isOk;
            if (isContest) {
                // 放入考核评测队列中
                isOk = redisUtils.llPush(Constants.Queue.CONTEST_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
            } else {
                // 放入普通评测队列中
                isOk = redisUtils.llPush(Constants.Queue.GENERAL_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
            }
            if (!isOk) {
                // 放入失败，修改评测状态
                judgeService.updateById(new Judge()
                                .setSubmitId(judgeId)
                                .setStatus(Constants.Judge.STATUS_SYSTEM_ERROR.getStatus())
                                .setErrorMessage("进入评测队列异常，请稍后在试")
                );
            }
            // 调用判题任务处理
            judgeReceiver.handleWaitingTask();
        } catch (Exception e) {
            log.error("调用redis将判题纳入判题等待队列异常--------------->{}", e.getMessage());
            judgeService.failToUseRedisPublishJudge(judgeId, pid, isContest);
        }
    }


    public void sendTestJudgeTask(TestJudgeReq testJudgeReq) {
        try {
            boolean isOk = redisUtils.llPush(Constants.Queue.TEST_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(testJudgeReq));
            if (!isOk) {
                throw new StatusSystemErrorException("系统错误：当前评测任务进入等待队列失败！");
            }
            judgeReceiver.handleWaitingTask();
        } catch (Exception e) {
            log.error("调用redis将判题纳入判题等待队列异常--------------->{}", e.getMessage());
            throw new StatusSystemErrorException("系统错误：当前评测任务进入等待队列失败！");
        }
    }

}
