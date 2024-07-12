package com.github.copilot.exceptionhandler.exception;

import com.github.copilot.exceptionhandler.R;
import com.github.copilot.exceptionhandler.entity.ExceptionInfo;
import com.github.copilot.exceptionhandler.exception.category.BizException;
import com.github.copilot.exceptionhandler.exception.error.CommonErrorCode;
import com.github.copilot.exceptionhandler.repository.ExceptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Centralized exceptionhandler handling class for the application.
 * This class uses @RestControllerAdvice to provide global exceptionhandler handling across all controllers.
 * It captures exceptions thrown by the application and returns a standardized response to the client,
 * ensuring a consistent error handling strategy throughout the application.
 */
@Slf4j
@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

    @Autowired
    ExceptionRepository exceptionRepository;

    /**
     * Handles NoHandlerFoundException (404 Not Found).
     * Logs the exceptionhandler and returns a standardized error response.
     *
     * @param e The caught NoHandlerFoundException.
     * @return A standardized error response.
     * @throws Throwable
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R handlerNoHandlerFoundException(NoHandlerFoundException e) throws Throwable {
        Long id = saveLog(NoHandlerFoundException.class, e);
        outPutErrorWarn(NoHandlerFoundException.class, CommonErrorCode.NOT_FOUND, e);
        return R.ofFail(CommonErrorCode.NOT_FOUND, id);
    }

    /**
     * Handles HttpRequestMethodNotSupportedException (405 Method Not Allowed).
     * Logs the exceptionhandler and returns a standardized error response.
     *
     * @param e The caught HttpRequestMethodNotSupportedException.
     * @return A standardized error response.
     * @throws Throwable
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R handlerHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) throws Throwable {
        Long id = saveLog(HttpRequestMethodNotSupportedException.class, e);
        outPutErrorWarn(HttpRequestMethodNotSupportedException.class,
                CommonErrorCode.METHOD_NOT_ALLOWED, e);
        return R.ofFail(CommonErrorCode.METHOD_NOT_ALLOWED, id);
    }

    /**
     * Handles HttpMediaTypeNotSupportedException (415 Unsupported Media Type).
     * Logs the exceptionhandler and returns a standardized error response.
     *
     * @param e The caught HttpMediaTypeNotSupportedException.
     * @return A standardized error response.
     * @throws Throwable
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public R handlerHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e) throws Throwable {
        Long id = saveLog(HttpMediaTypeNotSupportedException.class, e);
        outPutErrorWarn(HttpMediaTypeNotSupportedException.class,
                CommonErrorCode.UNSUPPORTED_MEDIA_TYPE, e);
        return R.ofFail(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE, id);
    }

    /**
     * Handles generic Exception (500 Internal Server Error).
     * Performs a secondary check for more specific exceptionhandler types and logs the exceptionhandler.
     *
     * @param e The caught Exception.
     * @return A standardized error response after secondary exceptionhandler type checking.
     * @throws Throwable
     */
    @ExceptionHandler(value = Exception.class)
    public R handlerException(Exception e) throws Throwable {
        return ifDepthExceptionType(e);
    }

    /**
     * Secondary depth check for specific exceptionhandler types.
     * This method allows for more granular handling of exceptions by checking their specific types
     * and handling them accordingly.
     *
     * @param throwable The throwable to check.
     * @return A standardized error response based on the specific exceptionhandler type.
     * @throws Throwable
     */
    private R ifDepthExceptionType(Throwable throwable) throws Throwable {
        Long id = saveLog(Exception.class, throwable);
        outPutError(Exception.class, CommonErrorCode.EXCEPTION, throwable);
        return R.ofFail(CommonErrorCode.EXCEPTION, id);
    }

    /**
     * Handles BizException, custom business logic exceptions.
     * Logs the exceptionhandler and returns a response with the specific error code and message from the exceptionhandler.
     *
     * @param e The caught BizException.
     * @return A standardized error response with specific error details.
     * @throws Throwable
     */
    @ExceptionHandler(value = BizException.class)
    public R handlerBusinessException(BizException e) throws Throwable {
        Long id = saveLog(BizException.class, e);
        outPutError(BizException.class, CommonErrorCode.BUSINESS_ERROR, e);
        return R.ofFail(e.getCode(), e.getMessage(), id);
    }

    /**
     * Handles HttpMessageNotReadableException, indicating a parameter error.
     * Logs the exceptionhandler and returns a detailed error response.
     *
     * @param e The caught HttpMessageNotReadableException.
     * @return A standardized error response indicating a parameter error.
     * @throws Throwable
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R handleHttpMessageNotReadableException(HttpMessageNotReadableException e) throws Throwable {
        Long id = saveLog(HttpMessageNotReadableException.class, e);
        outPutError(HttpMessageNotReadableException.class, CommonErrorCode.PARAM_ERROR, e);
        String msg = String.format("%s : 错误详情( %s )", CommonErrorCode.PARAM_ERROR.getMessage(),
                e.getRootCause().getMessage());
        return R.ofFail(CommonErrorCode.PARAM_ERROR.getCode(), msg, id);
    }

    /**
     * Handles MethodArgumentNotValidException and BindException, indicating validation errors.
     * Extracts and logs the specific field errors and returns a detailed error response.
     *
     * @param e The caught exceptionhandler (MethodArgumentNotValidException or BindException).
     * @return A standardized error response with details about the validation errors.
     * @throws Throwable
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R handleValidationException(Exception e) throws Throwable {
        Long id = saveLog(e.getClass(), e);
        BindingResult bindingResult = (e instanceof MethodArgumentNotValidException) ?
                ((MethodArgumentNotValidException) e).getBindingResult() :
                ((BindException) e).getBindingResult();
        return getBindResultDTO(bindingResult, id);
    }

    /**
     * Extracts validation error messages from BindingResult and constructs a detailed error response.
     *
     * @param bindingResult The BindingResult containing validation errors.
     * @param id The log id for the error.
     * @return A standardized error response with validation error details.
     */
    private R getBindResultDTO(BindingResult bindingResult, Long id) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (log.isDebugEnabled()) {
            for (FieldError error : fieldErrors) {
                log.error("{} -> {}", error.getDefaultMessage(), error.getDefaultMessage());
            }
        }

        if (fieldErrors.isEmpty()) {
            log.error("validExceptionHandler error fieldErrors is empty");
            R.ofFail(CommonErrorCode.BUSINESS_ERROR, id);
        }

        return R
                .ofFail(CommonErrorCode.PARAM_ERROR.getCode(), fieldErrors.get(0).getDefaultMessage(), id);
    }

    /**
     * Saves the exceptionhandler information to the database and returns the log id.
     *
     * @param errorType The class of the error.
     * @param throwable The caught throwable.
     * @return The id of the saved log entry.
     * @throws Throwable
     */
    private Long saveLog(Class errorType, Throwable throwable) throws Throwable {
        ExceptionInfo exceptionInfo = new ExceptionInfo(LocalDateTime.now(), errorType, throwable);
        exceptionRepository.save(exceptionInfo);
        return exceptionInfo.getId();
    }

    /**
     * Logs an error with the error type, secondary error type, and throwable details.
     *
     * @param errorType The class of the error.
     * @param secondaryErrorType The secondary error type for more specific logging.
     * @param throwable The caught throwable.
     */
    public void outPutError(Class errorType, Enum secondaryErrorType, Throwable throwable) {
        log.error("[{}] {}: {}", errorType.getSimpleName(), secondaryErrorType, throwable.getMessage(),
                throwable);
    }

    /**
     * Logs a warning with the error type, secondary error type, and throwable details.
     *
     * @param errorType The class of the error.
     * @param secondaryErrorType The secondary error type for more specific logging.
     * @param throwable The caught throwable.
     */
    public void outPutErrorWarn(Class errorType, Enum secondaryErrorType, Throwable throwable) {
        log.warn("[{}] {}: {}", errorType.getSimpleName(), secondaryErrorType, throwable.getMessage());
    }

}