package com.xiao.xoj.service;

import com.xiao.xoj.pojo.dto.ChatAskDTO;
import com.xiao.xoj.pojo.entity.wxqf.Chat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.entity.wxqf.ChatInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-08-05
 */
public interface ChatService extends IService<Chat> {

    List<Chat> getChatList();

    boolean addChat(String title);

    List<ChatInfo> getChatRecord(Integer chatId);

    ChatInfo sendAsk(ChatAskDTO chatAskDTO);

    boolean deleteChat(Integer chatId);
}
