package com.xiao.xoj.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.vo.ProblemInfoVO;
import com.xiao.xoj.pojo.vo.ProblemVO;
import com.xiao.xoj.service.front.FrontProblemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/4/18 11:12
 * @description: TODO
 */
@RestController
@RequestMapping("/xoj/front-problem")
public class FrontProblemController {

    @Resource
    private FrontProblemService frontProblemService;

    /**
     * @des: 获取题目列表分页
     *
     * @param limit
     * @param currentPage
     * @param keyword
     * @param tagId
     * @param difficulty
     * @return
     */
    @GetMapping(value = "get-problem-list")
    public ResultData<Page<ProblemVO>> getProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                      @RequestParam(value = "keyword", required = false) String keyword,
                                                      @RequestParam(value = "tagId", required = false) List<Integer> tagId,
                                                      @RequestParam(value = "difficulty", required = false) Integer difficulty,
                                                      @RequestParam(value = "type", required = false) Integer type) {

        Page<ProblemVO> problems = frontProblemService.getProblemList(limit, currentPage, keyword, tagId, difficulty, type);
        return ResultData.success(problems);
    }


    /**
     * @des: 获取题目详细信息
     *
     * @param pid
     * @return
     */
    @GetMapping(value = "get-problem-detail")
    public ResultData<ProblemInfoVO> getProblemDetail(@RequestParam(value = "pid") Integer pid) {

        ProblemInfoVO problemInfoVO = frontProblemService.getProblemDetail(pid, null);
        return ResultData.success(problemInfoVO);
    }


    // todo 获取用户对于该题最近AC的代码

}
