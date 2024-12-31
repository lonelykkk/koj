package com.xiao.xoj.controller.front;

import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.resource.ResourceTag;
import com.xiao.xoj.pojo.vo.ResourceVO;
import com.xiao.xoj.service.front.FrontResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/11 10:45
 * @description: TODO
 */
@RestController
@RequestMapping("/xoj/front-resource")
public class FrontResourceController {

    @Autowired
    private FrontResourceService frontResourceService;


    /**
     * @des: 获取资源列表
     *
     * @param keyword
     * @param resourceTagId
     * @return
     */
    @GetMapping("get-resource-list")
    public ResultData<List<ResourceVO>> getResourceList(@RequestParam(value = "keyword", required = false) String keyword,
                                                        @RequestParam(value = "resourceTagId", required = false) Integer resourceTagId) {

        List<ResourceVO> list = frontResourceService.getResourceList(keyword, resourceTagId);
        return ResultData.success(list);
    }


    /**
     * @des: 获取资源分类
     *
     * @return
     */
    @GetMapping("get-resource-tag")
    public ResultData<List<ResourceTag>> getResourceTag() {
        List<ResourceTag> list = frontResourceService.getResourceTag();
        return ResultData.success(list);
    }
}
