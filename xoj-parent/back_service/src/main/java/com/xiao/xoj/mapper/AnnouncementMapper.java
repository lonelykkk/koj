package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.announce.Announcement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.AnnouncementVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    IPage<AnnouncementVO> getAnnouncementList(IPage<AnnouncementVO> page, @Param("isAdmin") Boolean isAdmin);

    IPage<AnnouncementVO> getContestAnnouncement(IPage<AnnouncementVO> page, @Param("cid") Integer cid);

}
