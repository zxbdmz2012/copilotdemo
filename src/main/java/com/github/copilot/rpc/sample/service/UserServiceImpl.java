package com.github.copilot.rpc.sample.service;

import com.github.copilot.rpc.common.RegisterService;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    @Override
    public Integer insertUserId(String userId) {
        return 1;
    }
}
