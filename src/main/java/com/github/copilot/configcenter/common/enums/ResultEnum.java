package com.github.copilot.configcenter.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerates the possible outcomes of operations within the system.
 * This enumeration provides a standardized set of result codes and messages that can be used
 * across the system to indicate the success or failure of operations.
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {

    /**
     * Represents a successful operation.
     * Code: 0
     * Message: "success"
     */
    SUCCESS(0, "success"),

    /**
     * Represents a failed operation.
     * Code: 1
     * Message: "fail"
     */
    FAIL(1, "fail");

    private int code; // Numeric code representing the result.
    private String message; // Human-readable message describing the result.
}