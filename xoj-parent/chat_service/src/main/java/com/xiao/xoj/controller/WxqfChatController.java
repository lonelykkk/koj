package com.xiao.xoj.controller;

import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.manager.ChatManager;
import com.xiao.xoj.pojo.dto.wxqf.ChatRequest;
import com.xiao.xoj.pojo.dto.wxqf.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 肖恩
 * @create 2023/8/5 19:35
 * @description: 文心千帆对话相关接口
 */
@RestController
public class WxqfChatController {

    @Autowired
    private ChatManager chatManager;

    // 获取token
    @PostMapping("/get-access-token")
    public ResultData<String> getAccessToken(@RequestParam("apiKey") String apiKey,
                                             @RequestParam("secretKey") String secretKey) {

        String accessToken = chatManager.getAccessToken(apiKey, secretKey);
        if (accessToken == null) {
            return ResultData.fail("获取失败！请稍后在试！");
        }
        return ResultData.success(accessToken, "获取成功");
    }


    // 发送消息
    @PostMapping("/send")
    public ResultData<ChatResponse> send(@RequestBody ChatRequest chatRequest) {
        ChatResponse chatResponse = null;
        try {
            chatResponse = chatManager.send(chatRequest);
        } catch (StatusFailException e) {
            return ResultData.fail(e.getMessage());
        }

        if (chatResponse == null) {
            return ResultData.fail("AI 调用失败！");
        }
        return ResultData.success(chatResponse);
    }
}
