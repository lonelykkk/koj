package com.xiao.xoj.mapper;

import com.xiao.xoj.pojo.entity.discussion.Discussion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.DiscussionVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-24
 */
public interface DiscussionMapper extends BaseMapper<Discussion> {

    DiscussionVO getDiscussion(@Param("did")Integer did, @Param("uid")String uid);

}
