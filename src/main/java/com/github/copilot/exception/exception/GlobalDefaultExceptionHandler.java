package com.github.copilot.exception.exception;


import com.github.copilot.exception.R;
import com.github.copilot.exception.entity.ExceptionInfo;
import com.github.copilot.exception.exception.category.BizException;
import com.github.copilot.exception.exception.error.CommonErrorCode;
import com.github.copilot.exception.repository.ExceptionRepository;
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


@Slf4j
@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

    @Autowired
    ExceptionRepository exceptionRepository;


    /**
     * NoHandlerFoundException 404 异常处理
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R handlerNoHandlerFoundException(NoHandlerFoundException e) throws Throwable {
        Long id = saveLog(NoHandlerFoundException.class, e);
        outPutErrorWarn(NoHandlerFoundException.class, CommonErrorCode.NOT_FOUND, e);
        return R.ofFail(CommonErrorCode.NOT_FOUND, id);
    }

    /**
     * HttpRequestMethodNotSupportedException 405 异常处理
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
     * HttpMediaTypeNotSupportedException 415 异常处理
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
     * Exception 类捕获 500 异常处理
     */
    @ExceptionHandler(value = Exception.class)
    public R handlerException(Exception e) throws Throwable {
        return ifDepthExceptionType(e);
    }

    /**
     * 二次深度检查错误类型
     */
    private R ifDepthExceptionType(Throwable throwable) throws Throwable {
        Long id = saveLog(Exception.class, throwable);
//        if (cause instanceof ClientException) {
//            return handlerClientException((ClientException) cause);
//        }
//        if (cause instanceof FeignException) {
//            return handlerFeignException((FeignException) cause);
//        }
        outPutError(Exception.class, CommonErrorCode.EXCEPTION, throwable);
        return R.ofFail(CommonErrorCode.EXCEPTION, id);
    }

//    /**
//     * FeignException
//     */
//    @ExceptionHandler(value = FeignException.class)
//    public R handlerFeignException(FeignException e) throws Throwable {
//        errorDispose(e);
//        outPutError(FeignException.class, CommonErrorCode.RPC_ERROR, e);
//        return R.ofFail(CommonErrorCode.RPC_ERROR);
//    }
//


    /**
     * BusinessException 类捕获
     */
    @ExceptionHandler(value = BizException.class)
    public R handlerBusinessException(BizException e) throws Throwable {
        Long id = saveLog(BizException.class, e);
        outPutError(BizException.class, CommonErrorCode.BUSINESS_ERROR, e);
        return R.ofFail(e.getCode(), e.getMessage(), id);
    }

    /**
     * HttpMessageNotReadableException 参数错误异常
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
     * MethodArgumentNotValidException 参数错误异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e) throws Throwable {
        Long id = saveLog(MethodArgumentNotValidException.class, e);
        BindingResult bindingResult = e.getBindingResult();
        return getBindResultDTO(bindingResult, id);
    }

    /**
     * BindException 参数错误异常
     */
    @ExceptionHandler(BindException.class)
    public R handleBindException(BindException e) throws Throwable {
        Long id = saveLog(BindException.class, e);
        outPutError(BindException.class, CommonErrorCode.PARAM_ERROR, e);
        BindingResult bindingResult = e.getBindingResult();
        return getBindResultDTO(bindingResult, id);
    }

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


    private Long saveLog(Class errorType, Throwable throwable) throws Throwable {
        ExceptionInfo exceptionInfo = new ExceptionInfo(LocalDateTime.now(), errorType, throwable);
        exceptionRepository.save(exceptionInfo);
        return exceptionInfo.getId();
    }

    public void outPutError(Class errorType, Enum secondaryErrorType, Throwable throwable) {
        log.error("[{}] {}: {}", errorType.getSimpleName(), secondaryErrorType, throwable.getMessage(),
                throwable);
    }

    public void outPutErrorWarn(Class errorType, Enum secondaryErrorType, Throwable throwable) {
        log.warn("[{}] {}: {}", errorType.getSimpleName(), secondaryErrorType, throwable.getMessage());
    }

}
