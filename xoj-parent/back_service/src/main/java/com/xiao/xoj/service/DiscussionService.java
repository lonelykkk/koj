package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.discussion.Discussion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.entity.discussion.DiscussionReport;
import com.xiao.xoj.pojo.vo.DiscussionReportVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-24
 */
public interface DiscussionService extends IService<Discussion> {

    boolean deleteDiscussion(List<Integer> didList);

    boolean updateDiscussion(Discussion discussion);

    IPage<DiscussionReportVO> getReportList(Integer limit, Integer currentPage);

    boolean updateDiscussionReport(DiscussionReport discussionReport);
}
