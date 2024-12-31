package com.xiao.xoj.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.about.StudioAwards;
import com.xiao.xoj.pojo.entity.about.StudioInfo;
import com.xiao.xoj.service.StudioAwardsService;
import com.xiao.xoj.service.StudioInfoService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
@RestController
@RequestMapping("/xoj/studio-info")
public class StudioInfoController {

    @Autowired
    private StudioInfoService studioInfoService;

    @Autowired
    private StudioAwardsService studioAwardsService;

    /**
     * @des: 获取成员信息列表
     *
     * @param limit
     * @param currentPage
     * @param keyword
     * @return
     */
    @GetMapping("get-member-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<IPage<StudioInfo>> getMemberList(@RequestParam(value = "limit", required = false) Integer limit,
                                                       @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                       @RequestParam(value = "keyword", required = false) String keyword) {

        IPage<StudioInfo> studioInfoIPage = studioInfoService.getMemberList(limit, currentPage, keyword);
        return ResultData.success(studioInfoIPage);
    }


    /**
     * @des: todo 批量添加，导入Excel表格中的数据
     *
     * @param file
     * @return
     */
    @PostMapping("upload-members-info")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> uploadMembersInfo(MultipartFile file) {
        boolean isOk = studioInfoService.uploadMembersInfo(file);

        if (!isOk) {
            return ResultData.fail("上传失败！");
        }
        return ResultData.success("上传成功！");
    }


    /**
     * @des: 添加成员信息
     *
     * @param studioInfo
     * @return
     */
    @PostMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> addStudioInfo(@RequestBody StudioInfo studioInfo) {
        boolean isOk = studioInfoService.addStudioInfo(studioInfo);
        if (!isOk) {
            return ResultData.fail("添加失败！");
        }
        return ResultData.success("添加成功！");
    }


    /**
     * @des: 修改成员信息
     *
     * @param studioInfo
     * @return
     */
    @PutMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> updateStudioInfo(@RequestBody StudioInfo studioInfo) {
        boolean isOk = studioInfoService.updateStudioInfo(studioInfo);
        if (!isOk) {
            return ResultData.fail("修改失败！");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des: 删除成员信息
     *
     * @param sid
     * @return
     */
    @DeleteMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> deleteStudioInfo(@RequestParam(value = "sid") Integer sid) {
        boolean isOk = studioInfoService.deleteStudioInfo(sid);
        if (!isOk) {
            return ResultData.fail("删除失败！");
        }
        return ResultData.success("删除成功！");
    }


    /**
     * @des: 获取奖项列表
     *
     * @param limit
     * @param currentPage
     * @param keyword
     * @return
     */
    @GetMapping("get-awards-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<IPage<StudioAwards>> getAwardsList(@RequestParam(value = "limit", required = false) Integer limit,
                                                         @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                         @RequestParam(value = "keyword", required = false) String keyword) {

        IPage<StudioAwards> data = studioAwardsService.getAwardsList(limit, currentPage, keyword);
        return ResultData.success(data);
    }


    /**
     * @des: 添加奖项信息
     *
     * @param studioAwards
     * @return
     */
    @PostMapping("add-awards")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> addAwards(@RequestBody StudioAwards studioAwards) {

        boolean isOk = studioAwardsService.addAwards(studioAwards);
        if (!isOk) {
            return ResultData.fail("添加失败！");
        }
        return ResultData.success("添加成功！");
    }


    /**
     * @des: 修改奖项信息
     *
     * @param studioAwards
     * @return
     */
    @PutMapping("update-awards")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> updateAwards(@RequestBody StudioAwards studioAwards) {

        boolean isOk = studioAwardsService.updateAwards(studioAwards);
        if (!isOk) {
            return ResultData.fail("修改失败！");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des: 删除奖项信息
     *
     * @param aid
     * @return
     */
    @DeleteMapping("delete-awards")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> deleteAwards(@RequestParam(value = "aid") Integer aid) {

        boolean isOk = studioAwardsService.deleteAwards(aid);
        if (!isOk) {
            return ResultData.fail("删除失败！");
        }
        return ResultData.success("删除成功！");
    }

}

