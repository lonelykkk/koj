package com.xiao.xoj.service.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.vo.ProblemInfoVO;
import com.xiao.xoj.pojo.vo.ProblemVO;

import java.util.List;

public interface FrontProblemService {
    Page<ProblemVO> getProblemList(Integer limit, Integer currentPage, String keyword, List<Integer> tagId, Integer difficulty, Integer type);

    ProblemInfoVO getProblemDetail(Integer pid, Integer cid);
}
