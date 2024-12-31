package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.JudgeVO;
import com.xiao.xoj.pojo.vo.ProblemCountVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-04-27
 */
public interface JudgeMapper extends BaseMapper<Judge> {

    Page<JudgeVO> getSubmissionList(Page<JudgeVO> page,
                                    @Param("userId") String userId,
                                    @Param("problemID") Integer problemID);

    IPage<JudgeVO> getContestSubmissionList(Page<JudgeVO> page,
                                            @Param("cid") Integer cid,
                                            @Param("userId") String userId);

    ProblemCountVO getProblemCount(@Param("pid") Integer pid);


    List<ProblemCountVO> getProblemListCount(@Param("pidList")List<Integer> pidList);

    ProblemCountVO getContestProblemCount(@Param("pid") Integer pid,
                                          @Param("cid") Integer cid,
                                          @Param("startTime") Date startTime);


    List<Judge> getProblemFinalCode(@Param("cid")Integer cid, @Param("userId")String userId);
}
