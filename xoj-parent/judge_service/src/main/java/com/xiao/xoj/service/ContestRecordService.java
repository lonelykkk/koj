package com.xiao.xoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.entity.contest.ContestRecord;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
public interface ContestRecordService extends IService<ContestRecord> {

    void updateContestRecord(Integer score, Integer status, Integer submitId, Integer runTime);
}
