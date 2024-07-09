package com.github.copilot.exception.exception.error;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode {

    /**
     * 404 The web server cannot find the file or script you requested. Please check the URL to ensure the path is correct.
     */
    NOT_FOUND("404",
            String.format("Oops, can't find this resource (%s)", HttpStatus.NOT_FOUND.getReasonPhrase())),

    /**
     * 405 The method specified in the request line is not allowed for the resource identified by the request. Please ensure the correct MIME type is set for the requested resource.
     */
    METHOD_NOT_ALLOWED("405",
            String.format("Try a different approach (%s)", HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())),

    /**
     * 415 Unsupported Media Type
     */
    UNSUPPORTED_MEDIA_TYPE("415",
            String.format("Oops, this media type is not supported (%s)", HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())),

    /**
     * System exception 500 Internal server error
     */
    EXCEPTION("500", "The server is temporarily unavailable, please try again later"),

    /**
     * System traffic limiting
     */
    TRAFFIC_LIMITING("CLOUD-429", "Oops, the network is congested, please try again later"),

    /**
     * Service call exception
     */
    API_GATEWAY_ERROR("9999", "The network is busy, please try again later"),

    /**
     * Parameter error
     */
    PARAM_ERROR("100", "Parameter error"),

    /**
     * Business exception
     */
    BUSINESS_ERROR("400", "Business exception"),

    /**
     * Illegal request
     */
    ILLEGAL_REQUEST("ILLEGAL_REQUEST", "Illegal request"),

    /**
     * RPC call exception
     */
    RPC_ERROR("510", "Oops, there is a problem with the network!");

    private final String code;

    private final String message;

    CommonErrorCode(String code, String message) {
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
