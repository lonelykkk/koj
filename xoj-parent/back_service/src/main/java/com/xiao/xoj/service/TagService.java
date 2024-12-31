package com.xiao.xoj.service;

import com.xiao.xoj.pojo.entity.problem.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
public interface TagService extends IService<Tag> {

    boolean addTag(Tag tag);

    boolean updateTag(Tag tag);

    boolean deleteTag(Integer tid);

    List<Tag> getAllTag();

    List<Tag> getTagByPid(Integer pid);
}
