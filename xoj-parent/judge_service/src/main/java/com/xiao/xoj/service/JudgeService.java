package com.xiao.xoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.dto.TestJudgeReq;
import com.xiao.xoj.pojo.dto.TestJudgeRes;
import com.xiao.xoj.pojo.entity.judge.Judge;

public interface JudgeService extends IService<Judge> {

    void judge(Judge judge);

    TestJudgeRes testJudge(TestJudgeReq testJudgeReq);

}
