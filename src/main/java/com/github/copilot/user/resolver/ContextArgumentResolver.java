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
 * Token转化SysUser
 */
@Slf4j
public class ContextArgumentResolver implements HandlerMethodArgumentResolver {

    UserQueryInterface userQueryInterface;

    public ContextArgumentResolver(UserQueryInterface userQueryInterface) {
        this.userQueryInterface = userQueryInterface;
    }

    /**
     * 入参筛选
     *
     * @param mp 参数集合
     * @return 格式化后的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter mp) {
        return mp.hasParameterAnnotation(CurrentUser.class) && mp.getParameterType().equals(UserInfo.class);
    }

    /**
     * @param methodParameter       入参集合
     * @param modelAndViewContainer model 和 view
     * @param nativeWebRequest      web相关
     * @param webDataBinderFactory  入参解析
     * @return 包装对象
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        String userId = BaseContextHandler.getUserId();

        //以下代码为 根据 @LoginUser 注解来注入 SysUser 对象
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
            log.warn("注入登录人信息时，发生异常. --> {}", userInfo, e);
        }
        return userInfo;
    }
}
