package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.contest.Contest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.ContestRegisterCountVO;
import com.xiao.xoj.pojo.vo.ContestVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
public interface ContestMapper extends BaseMapper<Contest> {

    List<ContestVO> getContestList(IPage<ContestVO> page, @Param("keyword") String keyword);

    List<ContestRegisterCountVO> getContestRegisterCount(@Param("cidList") List<Integer> cidList);

    ContestVO getContestInfoById(@Param("cid") Integer cid);

    List<ContestVO> getRecentContest();
}
