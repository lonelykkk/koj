package com.xiao.xoj.service;

import com.xiao.xoj.pojo.entity.discussion.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-24
 */
public interface CategoryService extends IService<Category> {

    List<Category> getCategoryList();

    boolean addCategory(Category category);

    boolean updateCategory(Category category);

    boolean deleteCategory(Integer cid);
}
