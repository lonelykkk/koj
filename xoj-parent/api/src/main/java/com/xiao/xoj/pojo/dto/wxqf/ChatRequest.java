package com.xiao.xoj.pojo.dto.wxqf;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * @author 肖恩
 * @create 2023/8/5 14:05
 * @description: 文心千帆chat 请求对象
 */
@Data
public class ChatRequest {

    private String accessToken;

    private List<Message> messages;

    @Data
    @AllArgsConstructor
    public static class Message {

        private String role;

        private String content;
    }

    public enum Role {
        USER("user"),
        ASSISTANT("assistant");

        private String name;

        private Role(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}

