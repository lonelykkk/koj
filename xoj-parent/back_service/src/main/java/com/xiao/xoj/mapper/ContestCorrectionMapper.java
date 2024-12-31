package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.ContestStudentInfoVO;
import com.xiao.xoj.pojo.vo.UserContestInfoVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-07-14
 */
public interface ContestCorrectionMapper extends BaseMapper<ContestCorrection> {

    IPage<ContestStudentInfoVO> getContestStudentInfo(IPage<ContestStudentInfoVO> page, @Param("keyword")String keyword, @Param("cid") Integer cid);

    IPage<UserContestInfoVO> getUserContestInfo(IPage<UserContestInfoVO> page, @Param("userId")String userId);
}
