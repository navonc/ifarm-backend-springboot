package com.ifarm.common.exception;

import com.ifarm.common.result.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类
 * 
 * @author ifarm
 * @since 2024-07-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 构造函数
     */
    public BusinessException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param resultCode 结果码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造函数
     *
     * @param resultCode 结果码枚举
     * @param message 自定义错误信息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 500;
    }

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误信息
     * @param cause 异常原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
} 