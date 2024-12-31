package com.xiao.xoj.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 肖恩
 * @create 2023/3/21 9:56
 * @description: 结果类
 */
@Data
public class ResultData<T> {

    @ApiModelProperty("状态码")
    private Integer code;

    @ApiModelProperty("返回信息")
    private String message;

    @ApiModelProperty("返回的数据")
    private T data;


    /**
     * @des 成功
     *
     * @return
     */
    public static <T> ResultData<T> success() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ResultStatus.SUCCESS.getStatus());
        resultData.setData(null);
        resultData.setMessage(ResultStatus.SUCCESS.getDescription());
        return resultData;
//        return new ResultData<>(ResultStatus.SUCCESS.getStatus(), null, ResultStatus.SUCCESS.getDescription());
    }

    /**
     * @des 成功
     *
     * @param message 返回信息
     * @return
     */
    public static <T> ResultData<T> success(String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ResultStatus.SUCCESS.getStatus());
        resultData.setData(null);
        resultData.setMessage(message);
        return resultData;
//        return new ResultData<>(ResultStatus.SUCCESS.getStatus(), null, message);
    }

    /**
     * @des 成功
     *
     * @param data 返回数据
     * @return
     */
    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ResultStatus.SUCCESS.getStatus());
        resultData.setData(data);
        resultData.setMessage(ResultStatus.SUCCESS.getDescription());
        return resultData;
//        return new ResultData<>(ResultStatus.SUCCESS.getStatus(), data, ResultStatus.SUCCESS.getDescription());
    }

    /**
     * @des 成功
     *
     * @param data 返回数据
     * @param message 返回信息
     * @return
     */
    public static <T> ResultData<T> success(T data, String message) {
        ResultData<T> resultData = new ResultData<T>();
        resultData.setCode(ResultStatus.SUCCESS.getStatus());
        resultData.setData(data);
        resultData.setMessage(message);
        return resultData;
//        return new ResultData<>(ResultStatus.SUCCESS.getStatus(), data, message);
    }


    /**
     * @des 失败，无异常
     *
     * @return
     */
    public static <T> ResultData<T> fail() {
        ResultData<T> resultData = new ResultData<T>();
        resultData.setCode(ResultStatus.FAIL.getStatus());
        resultData.setData(null);
        resultData.setMessage(ResultStatus.FAIL.getDescription());
        return resultData;
    }


    public static <T> ResultData<T> fail(String message) {
        ResultData<T> resultData = new ResultData<T>();
        resultData.setCode(ResultStatus.FAIL.getStatus());
        resultData.setData(null);
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> ResultData<T> fail(ResultStatus resultStatus) {
        ResultData<T> resultData = new ResultData<T>();
        resultData.setCode(resultStatus.getStatus());
        resultData.setData(null);
        resultData.setMessage(resultStatus.getDescription());
        return resultData;
    }

    public static <T> ResultData<T> fail(Integer status, String message) {
        ResultData<T> resultData = new ResultData<T>();
        resultData.setCode(status);
        resultData.setData(null);
        resultData.setMessage(message);
        return resultData;
    }

}
