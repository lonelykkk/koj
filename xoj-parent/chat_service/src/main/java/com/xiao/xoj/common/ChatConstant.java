package com.xiao.xoj.common;

import lombok.Data;

/**
 * @author 肖恩
 * @create 2023/8/5 22:11
 * @description: 常量枚举类
 */
@Data
public class ChatConstant {

    /**
     * 文心千帆模型名称，及请求接口枚举常量
     */
    public enum ModelType {

        ERNIE_BOT("ERNIE-Bot", "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token="),
        ERNIE_BOT_TURBO("ERNIE-Bot-turbo", "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant?access_token=");

        private String name;
        private String url;

        ModelType(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

    }

    /**
     * 对话请求错误码
     */
    public enum ErrorCode {

        UnknownError(1, "服务器内部错误，请再次请求， 如果持续出现此类错误，请在百度云控制台内提交工单反馈"),
        ServiceTemporarilyUnavailable(2, "服务暂不可用，请再次请求， 如果持续出现此类错误，请在百度云控制台内提交工单反馈"),
        InvalidParameter(100, "无效的access_token参数，请检查后重新尝试"),
        AccessTokenInvalidOrNoLongerValid(110, "access_token无效"),
        AccessTokenExpired(111, "access token过期"),
        InternalError(336000, "服务内部错误");

        private Integer errorCode;
        private String errorMsg;

        ErrorCode(Integer errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }


}
