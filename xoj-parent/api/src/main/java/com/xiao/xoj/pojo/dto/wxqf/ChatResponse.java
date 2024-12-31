package com.xiao.xoj.pojo.dto.wxqf;

import lombok.Data;

/**
 * @author 肖恩
 * @create 2023/8/5 14:06
 * @description: 文心千帆chat 响应对象
 */
@Data
public class ChatResponse {

    /**
     * 本轮对话的id
     *
     */
    private String id;

    /**
     * 回包类型
     * chat.completion：多轮对话返回
     *
     */
    private String object;

    /**
     * 时间戳
     *
     */
    private Integer created;

    /**
     * 表示当前子句的序号。只有在流式接口模式下会返回该字段
     *
     */
    private Integer sentenceId;

    /**
     * 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段
     *
     */
    private Boolean isEnd;

    /**
     * 当前生成的结果是否被截断
     *
     */
    private Boolean isTruncated;

    /**
     * 对话返回结果
     *
     */
    private String result;

    /**
     * 表示用户输入是否存在安全，是否关闭当前会话，清理历史回话信息。
     * true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息。
     * false：否，表示用户输入无安全风险
     *
     */
    private Boolean needClearHistory;

    /**
     * 当need_clear_history为true时，此字段会告知第几轮对话有敏感信息，如果是当前问题，ban_round=-1
     *
     */
    private Integer banRound;

    /**
     * token统计信息，token数 = 汉字数+单词数*1.3 （仅为估算逻辑）
     *
     */
    private Usage usage;


    @Data
    public static class Usage {

        private Integer promptTokens; // 问题tokens数

        private Integer completionTokens; // 回答tokens数

        private Integer totalTokens; // tokens总数

    }

}
