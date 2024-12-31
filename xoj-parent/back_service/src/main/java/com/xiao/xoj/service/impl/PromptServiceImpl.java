package com.xiao.xoj.service.impl;

import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.wxqf.Prompt;
import com.xiao.xoj.mapper.PromptMapper;
import com.xiao.xoj.service.PromptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-08-05
 */
@Service
public class PromptServiceImpl extends ServiceImpl<PromptMapper, Prompt> implements PromptService {

    @Override
    public List<Prompt> getPromptList() {
        List<Prompt> prompts = this.list(null);
        return prompts;
    }

    @Override
    public boolean addPrompt(Prompt prompt) {
        // 参数校验
        if (StringUtils.isEmpty(prompt.getTitle()) || StringUtils.isEmpty(prompt.getContent())) {
            throw new StatusFailException("模板标题或内容不能为空！");
        }

        return this.save(prompt);
    }

    @Override
    public boolean updatePrompt(Prompt prompt) {
        // 参数校验
        if (StringUtils.isEmpty(prompt.getTitle()) || StringUtils.isEmpty(prompt.getContent())) {
            throw new StatusFailException("模板标题或内容不能为空！");
        }
        return this.updateById(prompt);
    }

    @Override
    public boolean deletePrompt(Integer pid) {
        // 检查是否存在，不存在再删除
        Prompt prompt = this.getById(pid);
        if (prompt != null) {
            return this.removeById(pid);
        }
        return true;
    }
}
