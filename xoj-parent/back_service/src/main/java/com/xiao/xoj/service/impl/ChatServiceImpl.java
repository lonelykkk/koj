package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.exception.StatusForbiddenException;
import com.xiao.xoj.manager.wxqf.ChatManager;
import com.xiao.xoj.pojo.dto.ChatAskDTO;
import com.xiao.xoj.pojo.entity.wxqf.Chat;
import com.xiao.xoj.mapper.ChatMapper;
import com.xiao.xoj.pojo.entity.wxqf.ChatInfo;
import com.xiao.xoj.service.ChatInfoService;
import com.xiao.xoj.service.ChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.AIChatLimitUtil;
import com.xiao.xoj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j(topic = "AI Chat")
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

    @Autowired
    private ChatInfoService chatInfoService;

    @Autowired
    private ChatManager chatManager;

    @Autowired
    private AIChatLimitUtil aiChatLimitUtil;

    @Override
    public List<Chat> getChatList() {
        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", accountProfile.getId())
                .orderByDesc("gmt_create"); // 根据创建时间降序
        return this.list(queryWrapper);
    }


    @Override
    public boolean addChat(String title) {
        title = title.trim();
        if (StringUtils.isEmpty(title)) {
            throw new StatusFailException("对话标题不能为空！");
        }
        if (title.length() > 20) {
            throw new StatusFailException("标题长度请勿超过20");
        }

        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        Chat chat = new Chat();
        chat.setTitle(title);
        chat.setUserId(accountProfile.getId());
        chat.setChatCount(0);

        return this.save(chat);
    }


    @Override
    public List<ChatInfo> getChatRecord(Integer chatId) {
        QueryWrapper<ChatInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chat_id", chatId)
                .orderByAsc("gmt_create");
        return chatInfoService.list(queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatInfo sendAsk(ChatAskDTO chatAskDTO) {

        // 判断当前令牌数量是否足够
        if(!aiChatLimitUtil.hasToken()) {
            throw new StatusFailException("当前请求人数过多，请稍后再试！");
        }

        // 判断该次对话条数是否超过50条，如果让用户重新建立对话
        List<ChatInfo> chatRecord = this.getChatRecord(chatAskDTO.getChatId());
        if (chatRecord.size() > 50) {
            throw new StatusFailException("该次对话次数已经超过50，请新建对话");
        }

        // todo 判断是否是本人调用

        // 令牌数量 - 1
        aiChatLimitUtil.decrToken();

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setChatId(chatAskDTO.getChatId());
        chatInfo.setQuestion(chatAskDTO.getContent());

        // 将本次提问添加到最近50次对话中
        chatRecord.add(chatInfo);
        String answer = null;
        try {
            answer = chatManager.send(chatRecord);

            // 返回结果为空，也设置为AI 调用异常
            if (StringUtils.isEmpty(answer)) {
                chatInfo.setStatus(1);
                chatInfo.setAnswer("AI 调用错误"); // 调用错误不需要保持到数据库中
                aiChatLimitUtil.incrToken(); // 令牌数量 + 1
            }

            throw new StatusFailException("AI 调用错误, 请重新尝试。");
        } catch (Exception e) {
            log.error("AI 调用错误，error message： {}", e.getMessage());
        } finally {
            aiChatLimitUtil.incrToken(); // 令牌数量 + 1
        }


        // 保存到数据库中
        chatInfo.setAnswer(answer);
        chatInfoService.save(chatInfo);

        // 对话条数+1
        Chat chat = this.getById(chatAskDTO.getChatId());
        chat.setChatCount(chat.getChatCount() + 1);
        this.updateById(chat);

        aiChatLimitUtil.incrToken(); // 令牌数量 + 1
        return chatInfo;
    }

    @Override
    public boolean deleteChat(Integer chatId) {
        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Chat chat = this.getById(chatId);
        if (!chat.getUserId().equals(accountProfile.getId())) {
            throw new StatusForbiddenException("非本人无法删除！");
        }

        // todo 删除chatInfo表中的数据

        return this.removeById(chatId);
    }

}
