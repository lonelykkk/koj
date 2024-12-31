package com.xiao.xoj.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/8/6 9:29
 * @description: TODO
 */
@Data
public class ChatAskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer chatId;

    private String content;

}
