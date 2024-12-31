package com.xiao.xoj.mapper;

import com.xiao.xoj.pojo.entity.contest.ContestProblem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.ContestProblemVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
public interface ContestProblemMapper extends BaseMapper<ContestProblem> {

    List<ContestProblemVO> getContestProblemList(@Param("cid") Integer cid,
                                             @Param("startTime") Date startTime,
                                             @Param("endTime") Date endTime);
}
