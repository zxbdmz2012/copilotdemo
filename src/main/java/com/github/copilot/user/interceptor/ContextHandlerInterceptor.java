package com.github.copilot.user.interceptor;

import com.github.copilot.user.context.BaseContextHandler;
import com.github.copilot.user.context.UserContextConstants;
import com.github.copilot.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor for handling user context within incoming requests.
 * This interceptor is responsible for extracting user-related information from the request headers,
 * such as the user ID, and storing it in a thread-local context using {@link BaseContextHandler}.
 * It ensures that user context is available throughout the processing of the request.
 *
 * The interceptor also handles the cleanup of the thread-local context after the request has been processed
 * to prevent memory leaks.
 *
 * The process involves:
 * - Checking if the handler is an instance of {@link HandlerMethod} to ensure that the interceptor
 *   only processes requests that are mapped to controller methods.
 * - Extracting the user ID from the request headers and storing it in the thread-local context.
 * - Logging and handling any exceptions that occur during header parsing.
 * - Removing the thread-local context after the request has been processed.
 */
@Slf4j
public class ContextHandlerInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (!(handler instanceof HandlerMethod)) {
                log.info("not exec!!! url={}", request.getRequestURL());
                return super.preHandle(request, response, handler);
            }
            String userId = getHeader(request, UserContextConstants.userIdHeader);
            BaseContextHandler.set(UserContextConstants.userIdHeader, userId);

        } catch (Exception e) {
            log.warn("error while getting userId from header", e);
        }
        return super.preHandle(request, response, handler);
    }

    private String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return value;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}