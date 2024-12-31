package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.manager.judge.JudgeContext;
import com.xiao.xoj.mapper.JudgeMapper;
import com.xiao.xoj.mapper.ProblemMapper;
import com.xiao.xoj.pojo.dto.TestJudgeReq;
import com.xiao.xoj.pojo.dto.TestJudgeRes;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.service.JudgeService;
import com.xiao.xoj.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * @author 肖恩
 * @create 2023/5/3 16:46
 * @description: TODO
 */
@Service
public class JudgeServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeService {

    @Value("${spring.application.name}")
    private String name;

    @Resource
    private ProblemMapper problemMapper;

    @Resource
    private JudgeContext judgeContext;

    @Override
    public void judge(Judge judge) {
        // 标志该题进入编译阶段
        // 写入当前判题服务的名称
        UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
        judgeUpdateWrapper.set("status", Constants.Judge.STATUS_COMPILING.getStatus())
                .set("judger", name)
                .eq("submit_id", judge.getSubmitId())
                .ne("status", Constants.Judge.STATUS_CANCELLED.getStatus());
        boolean isUpdatedOk = this.update(judge, judgeUpdateWrapper);
        // 没更新成功，则可能表示该评测被取消 或者 judge记录被删除了，则结束评测
        if (!isUpdatedOk) {
            return;
        }

        // 进行判题操作
        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("id", judge.getPid());
        Problem problem = problemMapper.selectOne(problemQueryWrapper);

        Judge finalJudgeRes = judgeContext.judge(problem, judge);

        // 更新该次提交
        this.updateById(finalJudgeRes);

        // 更新其它表
        if (!Objects.equals(finalJudgeRes.getStatus(), Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus())) {
            // 更新其它表
            judgeContext.updateOtherTable(finalJudgeRes.getSubmitId(),
                    finalJudgeRes.getStatus(),
                    judge.getCid(),
                    judge.getUserId(),
                    judge.getPid(),
                    finalJudgeRes.getScore(),
                    finalJudgeRes.getRunTime());
        }
    }


    @Override
    public TestJudgeRes testJudge(TestJudgeReq testJudgeReq) {
        return judgeContext.testJudge(testJudgeReq);
    }
}
