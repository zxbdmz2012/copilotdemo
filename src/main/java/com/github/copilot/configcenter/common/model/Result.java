package com.github.copilot.configcenter.common.model;

import com.github.copilot.configcenter.common.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Generic class for wrapping the response of operations across the system.
 * It encapsulates the outcome of an operation, including a status code, a descriptive message,
 * and optionally, data related to the operation's result.
 *
 * @param <T> The type of the data field, allowing for flexibility in the type of data that can be returned.
 */
@Data
@AllArgsConstructor
public class Result<T> {

    /**
     * The status code of the operation. It follows predefined codes in {@link ResultEnum}.
     */
    private int code;

    /**
     * A human-readable message providing more details about the operation's outcome.
     */
    private String message;

    /**
     * The data resulting from the operation, if any. Its type is generic, allowing for various types of data to be returned.
     */
    private T data;

    /**
     * Creates a failure result without data, using the default failure message from {@link ResultEnum}.
     *
     * @param <E> The type of the data field in the result.
     * @return A {@link Result} instance representing a failed operation.
     */
    public static <E> Result<E> fail() {
        return fail(ResultEnum.FAIL.getMessage());
    }

    /**
     * Creates a failure result without data, with a custom message.
     *
     * @param message The custom message for the failure.
     * @param <E>     The type of the data field in the result.
     * @return A {@link Result} instance representing a failed operation with a custom message.
     */
    public static <E> Result<E> fail(String message) {
        return new Result<>(ResultEnum.FAIL.getCode(), message, null);
    }

    /**
     * Creates a failure result without data, using a specific {@link ResultEnum} value.
     *
     * @param resultEnum The {@link ResultEnum} value to use for the failure result.
     * @param <E>        The type of the data field in the result.
     * @return A {@link Result} instance representing a failed operation based on the specified {@link ResultEnum} value.
     */
    public static <E> Result<E> fail(ResultEnum resultEnum) {
        return new Result<>(resultEnum.getCode(), resultEnum.getMessage(), null);
    }

    /**
     * Creates a failure result without data, using a specific {@link ResultEnum} value and a custom message.
     *
     * @param resultEnum The {@link ResultEnum} value to use for the failure result.
     * @param message    The custom message for the failure.
     * @return A {@link Result} instance representing a failed operation based on the specified {@link ResultEnum} value and custom message.
     */
    public static <E> Result<E> fail(ResultEnum resultEnum, String message) {
        return fail(resultEnum.getCode(), message);
    }

    /**
     * Creates a failure result without data, specifying a custom code and message.
     *
     * @param code    The custom code for the failure.
     * @param message The custom message for the failure.
     * @param <E>     The type of the data field in the result.
     * @return A {@link Result} instance representing a failed operation with a custom code and message.
     */
    public static <E> Result<E> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * Creates an empty success result, indicating a successful operation without returning any data.
     *
     * @return A {@link Result} instance representing a successful operation without data.
     */
    public static Result<Void> empty() {
        return success(null);
    }

    /**
     * Creates a success result, indicating a successful operation and returning data.
     *
     * @param data The data resulting from the successful operation.
     * @param <E>  The type of the data field in the result.
     * @return A {@link Result} instance representing a successful operation with data.
     */
    public static <E> Result<E> success(E data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    /**
     * Converts an existing {@link Result} instance to a failure result, preserving the original code and message but without data.
     *
     * @param result The original {@link Result} instance to convert.
     * @param <E>    The type of the data field in the new result.
     * @return A {@link Result} instance representing a failed operation, based on the original result.
     */
    public static <E> Result<E> resultToFail(Result<?> result) {
        return new Result<>(result.getCode(), result.getMessage(), null);
    }

    /**
     * Checks if the operation represented by this result instance failed.
     *
     * @return {@code true} if the operation failed, {@code false} otherwise.
     */
    public boolean failed() {
        return !isSuccess();
    }

    /**
     * Checks if the operation represented by this result instance was successful.
     *
     * @return {@code true} if the operation was successful, {@code false} otherwise.
     */
    public boolean isSuccess() {
        return code == ResultEnum.SUCCESS.getCode();
    }
}