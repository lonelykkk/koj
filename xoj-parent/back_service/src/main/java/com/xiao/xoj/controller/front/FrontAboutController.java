package com.xiao.xoj.controller.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.about.StudioAwards;
import com.xiao.xoj.pojo.entity.about.StudioInfo;
import com.xiao.xoj.service.front.FrontAboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/11 10:53
 * @description: 工作室成员信息及奖项控制器
 */
@RestController
@RequestMapping("/xoj/front-about")
public class FrontAboutController {

    @Autowired
    private FrontAboutService frontAboutService;


    /**
     * @des: 获取工作室成员信息
     *
     * @return
     */
    @GetMapping("get-member")
    public ResultData<List<StudioInfo>> getMemberList() {
        List<StudioInfo> data = frontAboutService.getMemberList();
        return ResultData.success(data);
    }


    /**
     * @des: 获取工作室奖项信息
     *
     * @param limit
     * @param currentPage
     * @return
     */
    @GetMapping("get-awards")
    public ResultData<IPage<StudioAwards>> getAwardsList(@RequestParam(value = "limit", required = false) Integer limit,
                                                         @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        IPage<StudioAwards> data = frontAboutService.getAwardsList(limit, currentPage);
        return ResultData.success(data);
    }


    // todo 获取工作室指导老师信息


}
