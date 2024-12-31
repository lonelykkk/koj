package com.xiao.xoj.service.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.discussion.Discussion;
import com.xiao.xoj.pojo.entity.discussion.DiscussionReport;
import com.xiao.xoj.pojo.vo.DiscussionVO;

public interface FrontDiscussionService {
    IPage<Discussion> getDiscussionList(Integer limit,
                                        Integer currentPage,
                                        Integer categoryId,
                                        Integer pid,
                                        Boolean onlyMine,
                                        String keyword,
                                        Boolean isAdmin);

    DiscussionVO getDiscussionDetail(Integer did);

    boolean addDiscussion(Discussion discussion);

    boolean deleteDiscussion(Integer did);

    boolean updateDiscussion(DiscussionVO discussionVO);

    boolean addDiscussionLike(Integer did, Boolean toLike);

    boolean addDiscussionReport(DiscussionReport discussionReport);
}
