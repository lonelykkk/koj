package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.pojo.entity.discussion.DiscussionReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.DiscussionReportVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-24
 */
public interface DiscussionReportMapper extends BaseMapper<DiscussionReport> {

    IPage<DiscussionReportVO> getReportList(Page<DiscussionReportVO> page);
}
