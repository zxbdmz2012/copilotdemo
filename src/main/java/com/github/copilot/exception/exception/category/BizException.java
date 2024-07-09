package com.github.copilot.exception.exception.category;


import com.github.copilot.exception.exception.error.CommonErrorCode;
import com.github.copilot.exception.exception.error.details.BusinessErrorCode;
import lombok.Getter;

public class BizException extends RuntimeException {

    @Getter
    private String code;

    /**
     * 使用枚举传参
     *
     * @param errorCode 异常枚举
     */
    public BizException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 使用CommonErrorCode枚举传参
     *
     * @param errorCode 异常枚举
     */
    public BizException(CommonErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 使用自定义消息
     *
     * @param code 值
     * @param msg  详情
     */
    public BizException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public boolean isShowMsg() {
        return true;
    }

}
