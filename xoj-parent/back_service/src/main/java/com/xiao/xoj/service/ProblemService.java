package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.dto.ProblemDto;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
public interface ProblemService extends IService<Problem> {

    boolean addProblem(ProblemDto problemDto);

    boolean updateProblem(ProblemDto problemDto);

    boolean deleteProblem(Integer pid);

    IPage<Problem> getAllProblem(Integer limit, Integer currPage, String keyword, Integer auth, Integer cid);

    Problem getProblem(Integer pid);

}
