package com.github.copilot.user.annotation;

import java.lang.annotation.*;

/**
 * Annotation to bind a method parameter to the current user of the application.
 * This annotation can be used on controller method parameters to automatically
 * inject the current user's information into the method call. It supports
 * optionally fetching full user details via a remote procedure call (RPC).
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
    /**
     * Specifies whether to fetch the full SysUser object information.
     * If set to true, the application will perform an RPC to retrieve
     * the complete user details. This is useful when more than just the
     * user's identifier is required for the operation being performed.
     *
     * @return true to fetch full user details, false to use minimal user information.
     */
    boolean isFull() default false;
}