package com.xiao.xoj.service;

import com.xiao.xoj.pojo.entity.wxqf.Prompt;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-08-05
 */
public interface PromptService extends IService<Prompt> {

    List<Prompt> getPromptList();

    boolean addPrompt(Prompt prompt);

    boolean updatePrompt(Prompt prompt);

    boolean deletePrompt(Integer pid);
}
