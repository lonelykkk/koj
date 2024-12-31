package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.pojo.entity.discussion.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.CommentVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-24
 */
public interface CommentMapper extends BaseMapper<Comment> {

    IPage<CommentVO> getCommentList(Page<CommentVO> commentVOPage, @Param("did") Integer did);
}
