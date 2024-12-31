package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.discussion.Category;
import com.xiao.xoj.mapper.CategoryMapper;
import com.xiao.xoj.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoryList() {
        return this.list(null);
    }

    @Override
    public boolean addCategory(Category category) {
        // 判断该分类是否已经存在
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", category.getName());
        Category exitCategory = this.getOne(queryWrapper);
        if (exitCategory != null) {
            throw new StatusFailException("该分类已经存在！请勿重复添加！");
        }

        return this.save(category);
    }

    @Override
    public boolean updateCategory(Category category) {
        // 判断该分类是否已经存在
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", category.getName());
        Category exitCategory = this.getOne(queryWrapper);
        if (exitCategory != null) {
            throw new StatusFailException("该分类已经存在！请勿重复添加！");
        }

        return this.saveOrUpdate(category);
    }

    @Override
    public boolean deleteCategory(Integer cid) {
        Category category = this.getById(cid);
        if (category == null) {
            throw new StatusFailException("该分类不存在！");
        }
        return this.removeById(cid);
    }
}
