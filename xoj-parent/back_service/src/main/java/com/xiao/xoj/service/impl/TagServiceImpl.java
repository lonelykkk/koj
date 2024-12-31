package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.problem.ProblemTag;
import com.xiao.xoj.pojo.entity.problem.Tag;
import com.xiao.xoj.mapper.TagMapper;
import com.xiao.xoj.service.ProblemTagService;
import com.xiao.xoj.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 * @description 题目标签管理
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private ProblemTagService problemTagService;

    @Override
    public boolean addTag(Tag tag){
        // 查询该标签是否已经存在
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", tag.getName());
        Tag exitTag = this.getOne(queryWrapper);
        if (exitTag != null) {
            throw new StatusFailException("该标签名称已存在！请勿重复添加！");
        }

        boolean isOk = this.save(tag);
        if (!isOk) {
            throw new StatusFailException("添加失败!");
        }
        return true;
    }

    @Override
    public boolean updateTag(Tag tag) {
        // 查询标签是否存在，存在在修改
        Tag exitTag = this.getById(tag.getId());
        if (exitTag == null) {
            throw new StatusFailException("该标签不存在，请重新尝试！");
        }

        boolean isOk = this.updateById(tag);
        if (!isOk) {
            throw new StatusFailException("修改失败！");
        }
        return true;
    }

    @Override
    public boolean deleteTag(Integer tid) {
        // 查询tagId是否存在
        Tag exitTag = this.getById(tid);
        if (exitTag == null) {
            throw new StatusFailException("该标签不存在，请重新尝试！");
        }

        boolean isOk = this.removeById(tid);
        if (!isOk) {
            throw new StatusFailException("删除失败！");
        }
        return true;
    }

    @Override
    public List<Tag> getAllTag() {
        List<Tag> tagList = this.list(null);
        return tagList;
    }

    @Override
    public List<Tag> getTagByPid(Integer pid) {

        List<Integer> tagIds = new ArrayList<>();

        QueryWrapper<ProblemTag> ptWrapper = new QueryWrapper<>();
        ptWrapper.eq("problem_id", pid);
        problemTagService.list(ptWrapper).forEach(item -> {
            tagIds.add(item.getTagId());
        });

        if (tagIds.size() > 0) {
            List<Tag> tags = (List<Tag>) this.listByIds(tagIds);
            return tags;
        }
        return null;
    }

}
