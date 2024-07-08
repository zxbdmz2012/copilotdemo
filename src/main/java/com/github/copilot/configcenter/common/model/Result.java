package com.github.copilot.configcenter.common.model;

import com.github.copilot.configcenter.common.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Result<T> {

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应描述
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public static <E> Result<E> fail() {
        return fail(ResultEnum.FAIL.getMessage());
    }

    public static <E> Result<E> fail(String message) {
        return new Result<>(ResultEnum.FAIL.getCode(), message, null);
    }

    public static <E> Result<E> fail(ResultEnum resultEnum) {
        return new Result<>(resultEnum.getCode(), resultEnum.getMessage(), null);
    }

    public static <E> Result<E> fail(ResultEnum resultEnum, String message) {
        return fail(resultEnum.getCode(), message);
    }

    public static <E> Result<E> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static Result<Void> empty() {
        return success(null);
    }

    public static <E> Result<E> success(E message) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), message);
    }

    public static <E> Result<E> resultToFail(Result<?> result) {
        return new Result<>(result.getCode(), result.getMessage(), null);
    }

    public boolean failed() {
        return !isSuccess();
    }

    public boolean isSuccess() {
        return code == ResultEnum.SUCCESS.getCode();
    }
}
