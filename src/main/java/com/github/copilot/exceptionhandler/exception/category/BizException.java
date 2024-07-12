package com.github.copilot.exceptionhandler.exception.category;

import com.github.copilot.exceptionhandler.exception.error.CommonErrorCode;
import com.github.copilot.exceptionhandler.exception.error.details.BusinessErrorCode;
import lombok.Getter;

/**
 * Custom exceptionhandler class for business logic errors.
 * This class extends RuntimeException and is used to represent various business logic errors
 * that can occur within the application. It supports initialization with specific error codes
 * and messages defined in BusinessErrorCode or CommonErrorCode, or with custom strings.
 */
public class BizException extends RuntimeException {

    @Getter
    private String code; // The error code associated with the exceptionhandler.

    /**
     * Constructor for creating a BizException with a BusinessErrorCode.
     * This allows for standardized error handling across the application by using predefined error codes and messages.
     *
     * @param errorCode The BusinessErrorCode enum representing the specific error.
     */
    public BizException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * Constructor for creating a BizException with a CommonErrorCode.
     * Similar to the BusinessErrorCode constructor, but uses the CommonErrorCode enum for more general errors.
     *
     * @param errorCode The CommonErrorCode enum representing the specific error.
     */
    public BizException(CommonErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * Constructor for creating a BizException with a custom error code and message.
     * This constructor allows for flexibility in defining error codes and messages that may not be covered by the enums.
     *
     * @param code The custom error code as a String.
     * @param msg The detailed error message as a String.
     */
    public BizException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * Indicates whether the error message should be shown to the user.
     * This method can be overridden to hide certain error messages from the user, based on the business logic.
     *
     * @return true if the error message should be shown, false otherwise.
     */
    public boolean isShowMsg() {
        return true;
    }

}