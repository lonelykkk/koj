package com.xiao.xoj.service.impl;

import com.xiao.xoj.pojo.entity.discussion.Comment;
import com.xiao.xoj.mapper.CommentMapper;
import com.xiao.xoj.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-24
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
