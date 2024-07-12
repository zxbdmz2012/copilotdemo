package com.github.copilot.exceptionhandler.exception.error.details;


public enum BusinessErrorCode {


    BUSINESS_ERROR("500", "bussiness error"),
    ;

    private final String code;

    private final String message;

    BusinessErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
