package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.announce.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.vo.AnnouncementVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
public interface AnnouncementService extends IService<Announcement> {

    IPage<AnnouncementVO> getAnnouncementList(Integer limit, Integer currentPage, Boolean isAdmin);

    boolean addAnnouncement(Announcement announcement);

    boolean updateAnnouncement(Announcement announcement);

    boolean deleteAnnouncement(Integer aid);
}
