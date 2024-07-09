package com.github.copilot.user.feign;

import com.github.copilot.user.model.UserInfo;


public interface UserQueryInterface {

    UserInfo getByUserId(String userId);
}