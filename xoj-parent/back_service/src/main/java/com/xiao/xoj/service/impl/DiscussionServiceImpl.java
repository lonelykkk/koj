package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.mapper.DiscussionReportMapper;
import com.xiao.xoj.pojo.entity.discussion.Discussion;
import com.xiao.xoj.mapper.DiscussionMapper;
import com.xiao.xoj.pojo.entity.discussion.DiscussionReport;
import com.xiao.xoj.pojo.vo.DiscussionReportVO;
import com.xiao.xoj.service.DiscussionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-24
 */
@Service
public class DiscussionServiceImpl extends ServiceImpl<DiscussionMapper, Discussion> implements DiscussionService {

    @Resource
    private DiscussionReportMapper discussionReportMapper;

    @Override
    public boolean deleteDiscussion(List<Integer> didList) {
        if (didList == null || didList.size() == 0) {
            throw new StatusFailException("删除失败！");
        }
        return this.removeByIds(didList);
    }

    @Override
    public boolean updateDiscussion(Discussion discussion) {
        return this.updateById(discussion);
    }

    @Override
    public IPage<DiscussionReportVO> getReportList(Integer limit, Integer currentPage) {
        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;
        Page<DiscussionReportVO> page = new Page<>(currentPage, limit);
        return discussionReportMapper.getReportList(page);
    }

    @Override
    public boolean updateDiscussionReport(DiscussionReport discussionReport) {
        int res = discussionReportMapper.updateById(discussionReport);
        return res > 0 ? true : false;
    }
}
