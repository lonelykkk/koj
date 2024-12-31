package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.announce.Announcement;
import com.xiao.xoj.mapper.AnnouncementMapper;
import com.xiao.xoj.pojo.vo.AnnouncementVO;
import com.xiao.xoj.service.AnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Resource
    private AnnouncementMapper announcementMapper;

    @Override
    public IPage<AnnouncementVO> getAnnouncementList(Integer limit, Integer currentPage, Boolean isAdmin) {
        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;
        IPage<AnnouncementVO> page = new Page<>(currentPage, limit);
        return announcementMapper.getAnnouncementList(page, isAdmin);
    }


    @Override
    public boolean addAnnouncement(Announcement announcement) {
        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 参数校验
        if (StringUtils.isEmpty(announcement.getContent())) {
            throw new StatusFailException("通告内容不能为空！");
        }
        if (StringUtils.isEmpty(announcement.getTitle())) {
            throw new StatusFailException("通告标题不能为空！");
        }

        announcement.setUserId(accountProfile.getId());

        return this.save(announcement);
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 参数校验
        if (StringUtils.isEmpty(announcement.getContent())) {
            throw new StatusFailException("通告内容不能为空！");
        }
        if (StringUtils.isEmpty(announcement.getTitle())) {
            throw new StatusFailException("通告标题不能为空！");
        }

        announcement.setUserId(accountProfile.getId());

        return this.saveOrUpdate(announcement);
    }


    @Override
    public boolean deleteAnnouncement(Integer aid) {
        Announcement announcement = this.getById(aid);
        if (announcement == null) {
            throw new StatusFailException("删除失败！该通告不存在！");
        }

        return this.removeById(announcement.getId());
    }
}
