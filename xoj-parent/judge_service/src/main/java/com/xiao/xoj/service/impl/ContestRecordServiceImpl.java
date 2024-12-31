package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.mapper.ContestRecordMapper;
import com.xiao.xoj.pojo.entity.contest.ContestRecord;
import com.xiao.xoj.service.ContestRecordService;
import com.xiao.xoj.utils.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
@Service
public class ContestRecordServiceImpl extends ServiceImpl<ContestRecordMapper, ContestRecord> implements ContestRecordService {

    @Resource
    private ContestRecordMapper contestRecordMapper;

    @Override
    public void updateContestRecord(Integer score, Integer status, Integer submitId, Integer runTime) {
        UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
        // 如果是AC
        if (status.intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
            updateWrapper.set("status", Constants.Contest.RECORD_AC.getCode());
            // 部分通过
        } else if (status.intValue() == Constants.Judge.STATUS_PARTIAL_ACCEPTED.getStatus()) {
            updateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
        } else {
            updateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
        }

        if (score != null) {
            updateWrapper.set("score", score);
        }

        updateWrapper.set("use_time", runTime);

        updateWrapper.eq("submit_id", submitId); // submit_id一定只有一个
        boolean result = contestRecordMapper.update(null, updateWrapper) > 0;
//        if (!result) {
//            tryAgainUpdate(updateWrapper);
//        }
    }
}
