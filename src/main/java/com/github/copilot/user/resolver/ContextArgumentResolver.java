package com.github.copilot.user.resolver;

import com.github.copilot.user.annotation.CurrentUser;
import com.github.copilot.user.context.BaseContextHandler;
import com.github.copilot.user.feign.UserQueryInterface;
import com.github.copilot.user.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Argument resolver for injecting user information into controller methods.
 * This resolver is responsible for converting a token (from the request context) into a {@link UserInfo} object.
 * It checks if the method parameter is annotated with {@link CurrentUser} and if the parameter type is {@link UserInfo}.
 * If both conditions are met, it attempts to retrieve the user ID from the thread-local context and construct a {@link UserInfo} object.
 * Optionally, if the {@link CurrentUser} annotation specifies to fetch full user details, it makes a call to {@link UserQueryInterface}
 * to retrieve and return the complete user information.
 */
@Slf4j
public class ContextArgumentResolver implements HandlerMethodArgumentResolver {

    UserQueryInterface userQueryInterface;

    public ContextArgumentResolver(UserQueryInterface userQueryInterface) {
        this.userQueryInterface = userQueryInterface;
    }

    /**
     * Determines if the resolver supports a given method parameter.
     * It supports the parameter if it's annotated with {@link CurrentUser} and its type is {@link UserInfo}.
     *
     * @param mp The method parameter to check.
     * @return True if the parameter is supported, false otherwise.
     */
    @Override
    public boolean supportsParameter(MethodParameter mp) {
        return mp.hasParameterAnnotation(CurrentUser.class) && mp.getParameterType().equals(UserInfo.class);
    }

    /**
     * Resolves the method argument into an object.
     * Retrieves the user ID from the thread-local context and constructs a {@link UserInfo} object.
     * If the {@link CurrentUser} annotation specifies to fetch full details, it retrieves the full user information.
     *
     * @param methodParameter       The method parameter.
     * @param modelAndViewContainer The container holding the model and view.
     * @param nativeWebRequest      The web request.
     * @param webDataBinderFactory  The factory for creating web data binders.
     * @return The resolved object, which is a {@link UserInfo} instance.
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        String userId = BaseContextHandler.getUserId();

        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .build();

        try {
            CurrentUser currentUser = methodParameter.getParameterAnnotation(CurrentUser.class);
            boolean isFull = currentUser.isFull();

            if (isFull) {
                userInfo = userQueryInterface.getByUserId(userId);
            }
        } catch (Exception e) {
            log.warn("Error injecting user information. --> {}", userInfo, e);
        }
        return userInfo;
    }
}