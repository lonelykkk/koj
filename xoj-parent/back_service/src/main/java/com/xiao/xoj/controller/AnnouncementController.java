package com.xiao.xoj.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.announce.Announcement;
import com.xiao.xoj.pojo.vo.AnnouncementVO;
import com.xiao.xoj.service.AnnouncementService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
@RestController
@RequestMapping("/xoj/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;


    /**
     * @des: 获取通告列表
     *
     * @param limit
     * @param currentPage
     * @return
     */
    @GetMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<IPage<AnnouncementVO>> getAnnouncementList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                 @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        IPage<AnnouncementVO> data = announcementService.getAnnouncementList(limit, currentPage, true);
        return ResultData.success(data);
    }


    /**
     * @des: 添加公告
     *
     * @param announcement
     * @return
     */
    @PostMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addAnnouncement(@RequestBody Announcement announcement) {
        boolean isOk = announcementService.addAnnouncement(announcement);
        if (!isOk) {
            return ResultData.fail("添加失败!");
        }
        return ResultData.success("添加成功！");
    }


    /**
     * @des: 修改公告
     *
     * @param announcement
     * @return
     */
    @PutMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateAnnouncement(@RequestBody Announcement announcement) {
        boolean isOk = announcementService.updateAnnouncement(announcement);
        if (!isOk) {
            return ResultData.fail("修改失败!");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des: 删除公告
     *
     * @param aid
     * @return
     */
    @DeleteMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteAnnouncement(@RequestParam(value = "aid") Integer aid) {
        boolean isOk = announcementService.deleteAnnouncement(aid);
        if (!isOk) {
            return ResultData.fail("删除失败!");
        }
        return ResultData.success("删除成功！");
    }

}

