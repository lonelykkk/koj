package com.xiao.xoj.manager;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiao.xoj.common.ChatConstant;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.dto.wxqf.ChatRequest;
import com.xiao.xoj.pojo.dto.wxqf.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author 肖恩
 * @create 2023/8/5 21:40
 * @description: TODO
 */
@Slf4j(topic = "chat")
@Component
public class ChatManager {

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    // todo
    static final String apiKey = "3OoG03O6aFbWGyclVnXpyLdk";
    static final String secretKey = "hZ3h3q4DA5ytUTRzFt7OLNu5dLiCKNdf";

    public String getAccessToken(String apiKey, String secretKey) {
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey +"&client_secret=" + secretKey;
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        Response response = null;
        String access_token = null;
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            // 获取access_token
            access_token = (String) JSONUtil.parseObj(response.body().string()).get("access_token");
        } catch (IOException e) {
            log.error("文心千帆accessToken获取失败", e);
        }

        return access_token;
    }

    public ChatResponse send(ChatRequest chatRequest) throws StatusFailException{

        String accessToken = this.getAccessToken(apiKey, secretKey);

        String url = ChatConstant.ModelType.ERNIE_BOT_TURBO.getUrl() + accessToken;

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("messages", chatRequest.getMessages());
        jsonObject.set("stream", false);

        // 构建 Request
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONUtil.toJsonStr(jsonObject), mediaType))
                .build();

        // 发起请求
        Response response = null;
        ChatResponse chatResponse = new ChatResponse();
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            JSONObject respJson = new JSONObject(response.body().string());
            // 判断请求是否成功
            if (respJson.containsKey("error_code")) {
                // accessToken过期异常
                if (respJson.get("error_code").equals(ChatConstant.ErrorCode.AccessTokenExpired.getErrorCode())) {
                    throw new StatusFailException("对话accessToken过期，请重新发送或者新建对话");
                }
                // accessToken无效异常
                if (respJson.get("error_code").equals(ChatConstant.ErrorCode.AccessTokenInvalidOrNoLongerValid.getErrorCode())) {
                    throw new StatusFailException("对话accessToken无效，请重新发送或者新建对话");
                }
                log.error("对话请求失败，失败错误码: {}, 错误信息: {} ", respJson.get("error_code"), respJson.get("error_msg"));
                return null;
            }
            // 响应成功
            chatResponse = respJson.toBean(ChatResponse.class);
        } catch (IOException e) {
            log.error("请求失败！");
            e.printStackTrace();
        }

        return chatResponse;
    }


}
