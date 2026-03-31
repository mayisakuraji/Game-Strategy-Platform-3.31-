package org.terminate.common;

import lombok.Data;

/**
 * 全局统一响应结果封装
 * 理由：规范前后端交互协议，便于前端统一处理逻辑
 */
@Data
public class Result<T> {
    private Integer code;    // 状态码：200成功，500错误
    private String message;  // 提示信息
    private T data;          // 数据载体

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}