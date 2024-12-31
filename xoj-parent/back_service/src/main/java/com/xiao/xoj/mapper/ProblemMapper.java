package com.xiao.xoj.mapper;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.ProblemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */

public interface ProblemMapper extends BaseMapper<Problem> {

    List<ProblemVO> getProblemList(Page<ProblemVO> page,
                                   @Param("keyword") String keyword,
                                   @Param("difficulty") Integer difficulty,
                                   @Param("tid") List<Integer> tagId,
                                   @Param("type") Integer type);

}
