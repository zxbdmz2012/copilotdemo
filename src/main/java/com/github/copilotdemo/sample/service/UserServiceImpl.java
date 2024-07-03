package com.github.copilotdemo.sample.service;

import com.github.copilotdemo.common.RegisterService;
import org.springframework.stereotype.Component;

@Component
@RegisterService
public class UserServiceImpl implements UserService {
    @Override
    public Integer insertUserId(String userId) {
        return 1;
    }
}
