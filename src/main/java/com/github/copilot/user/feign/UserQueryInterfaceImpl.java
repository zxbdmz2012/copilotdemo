package com.github.copilot.user.feign;

import com.github.copilot.user.model.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserQueryInterfaceImpl implements UserQueryInterface {
    @Override
    public UserInfo getByUserId(String userId) {
        return new UserInfo();
    }
}
