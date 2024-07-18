package com.github.copilot.exceptionhandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.copilot.exceptionhandler.exception.error.CommonErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * A generic class for wrapping responses in a unified data structure.
 * This class is designed to standardize the format of responses sent from the server to clients,
 * encapsulating both successful results and error information. It implements Serializable to allow
 * instances of this class to be easily serialized and deserialized, especially useful when sending data across networks.
 *
 * @param <T> The type of the data being returned in the response. This allows the class to be used
 *            for any type of data, providing flexibility in response data types.
 */
@Getter
@Setter
public class Result<T> implements Serializable {

    /**
     * Indicates whether the operation associated with the response was successful.
     */
    private Boolean succ;

    /**
     * A unique identifier for the response, which can be used for logging or tracking purposes.
     */
    private Long id;

    /**
     * The data payload of the response. This can be any type of data and is generic.
     */
    private T data;

    /**
     * The error code associated with the response. This is typically used when the response
     * represents an error or failure.
     */
    private String code;

    /**
     * A human-readable message providing more details about the response. This can be used
     * to convey success messages or detailed error information.
     */
    private String msg;

    /**
     * Default constructor.
     */
    public Result() {
    }

    /**
     * Constructs a new response object with specified properties.
     *
     * @param succ Whether the operation was successful.
     * @param id   The unique identifier for the response.
     * @param data The data payload of the response.
     * @param code The error code associated with the response.
     * @param msg  The detailed message about the response.
     */
    public Result(Boolean succ, Long id, T data, String code, String msg) {
        this.succ = succ;
        this.id = id;
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    /**
     * Creates a success response without data.
     *
     * @return A new R instance representing a successful operation.
     */
    public static Result ofSuccess() {
        Result result = new Result();
        result.succ = true;
        return result;
    }

    /**
     * Creates a success response with data.
     *
     * @param data The data to include in the success response.
     * @return A new R instance representing a successful operation with data.
     */
    public static Result ofSuccess(Object data) {
        Result result = new Result();
        result.succ = true;
        result.setData(data);
        return result;
    }

    /**
     * Creates a failure response with an error code and message.
     *
     * @param code The error code.
     * @param msg  The error message.
     * @param id   The unique identifier for the response.
     * @return A new R instance representing a failed operation.
     */
    public static Result ofFail(String code, String msg, Long id) {
        Result result = new Result();
        result.succ = false;
        result.code = code;
        result.msg = msg;
        result.id = id;
        return result;
    }

    /**
     * Creates a failure response with an error code, message, and data.
     *
     * @param code The error code.
     * @param msg  The error message.
     * @param data The data associated with the failure.
     * @param id   The unique identifier for the response.
     * @return A new R instance representing a failed operation with data.
     */
    public static Result ofFail(String code, String msg, Object data, Long id) {
        Result result = new Result();
        result.succ = false;
        result.code = code;
        result.msg = msg;
        result.setData(data);
        result.id = id;
        return result;
    }

    /**
     * Creates a failure response based on a predefined db error code.
     *
     * @param resultEnum The db error code enumeration.
     * @param id         The unique identifier for the response.
     * @return A new R instance representing a failed operation based on a db error code.
     */
    public static Result ofFail(CommonErrorCode resultEnum, Long id) {
        Result result = new Result();
        result.id = id;
        result.succ = false;
        result.code = resultEnum.getCode();
        result.msg = resultEnum.getMessage();
        return result;
    }

    // Getters and setters omitted for brevity

    /**
     * Builds and returns a JSON string representation of the response object.
     * This method uses the fastjson library to serialize the response object into a JSON string,
     * excluding any circular references to prevent serialization errors.
     *
     * @return A JSON string representation of the response object.
     */
    public String buildResultJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("succ", this.succ);
        jsonObject.put("code", this.code);
        jsonObject.put("id", this.id);
        jsonObject.put("msg", this.msg);
        jsonObject.put("data", this.data);
        return JSON.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public String toString() {
        return "Result{" +
                "succ=" + succ +
                ", id=" + id +
                ", data=" + data +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}