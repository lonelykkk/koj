package com.xiao.xoj.manager.wxqf;

import cn.hutool.json.JSONUtil;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.dto.wxqf.ChatRequest;
import com.xiao.xoj.pojo.dto.wxqf.ChatResponse;
import com.xiao.xoj.pojo.entity.wxqf.ChatInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/8/6 16:09
 * @description: TODO
 */
@Slf4j(topic = "xoj-chat")
@Component
public class ChatManager {

    @Autowired
    private RestTemplate restTemplate;

    public String send(List<ChatInfo> chatRecord) throws Exception {
        String url = "http://localhost:9004/send";

        // 封装请求对象
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setAccessToken("");
        chatRequest.setMessages(getChatMessages(chatRecord));

        // 发送请求
        ResultData result =  restTemplate.postForObject(url, chatRequest, ResultData.class);

        ChatResponse chatResponse = JSONUtil.toBean(JSONUtil.toJsonStr(result.getData()), ChatResponse.class);

        return chatResponse.getResult();
    }

    // 将对话信息封装到List中
    public List<ChatRequest.Message> getChatMessages(List<ChatInfo> chatRecord) {
        LinkedList<ChatRequest.Message> messages = new LinkedList<>();
        chatRecord.forEach(chatInfo -> {
            ChatRequest.Message userMessage = new ChatRequest.Message(ChatRequest.Role.USER.getName(), chatInfo.getQuestion());
            ChatRequest.Message assistantMessage = new ChatRequest.Message(ChatRequest.Role.ASSISTANT.getName(), chatInfo.getAnswer());
            messages.addLast(userMessage);
            messages.addLast(assistantMessage);
        });
        // 移除最后一个message
        messages.removeLast();
        return messages;
    }
}
