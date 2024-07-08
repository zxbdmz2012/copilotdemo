package com.github.copilot.user.annotation;

import java.lang.annotation.*;


@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
    /**
     * 是否查询SysUser对象所有信息，true则通过rpc接口查询
     */
    boolean isFull() default false;

}
