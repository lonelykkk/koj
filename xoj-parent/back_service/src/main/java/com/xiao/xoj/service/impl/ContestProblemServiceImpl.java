package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.pojo.entity.contest.ContestProblem;
import com.xiao.xoj.mapper.ContestProblemMapper;
import com.xiao.xoj.pojo.vo.ContestProblemVO;
import com.xiao.xoj.service.ContestProblemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
@Service
public class ContestProblemServiceImpl extends ServiceImpl<ContestProblemMapper, ContestProblem> implements ContestProblemService {

    @Override
    public List<ContestProblemVO> getContestProblem(Integer cid,
                                                    Date startTime,
                                                    Date endTime) {
        // todo 筛去 比赛管理员和超级管理员的提交

        List<ContestProblemVO> cpVOList = this.baseMapper.getContestProblemList(cid, startTime, endTime);
        return cpVOList;
    }

}
