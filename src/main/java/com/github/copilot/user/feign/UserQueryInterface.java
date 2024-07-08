package com.github.copilot.user.feign;

import com.github.copilot.user.model.UserInfo;

/**
 * 用户操作API
 */
public interface UserQueryInterface {
    /**
     * 根据id 查询用户详情
     */
    UserInfo getByUserId(String userId);
}