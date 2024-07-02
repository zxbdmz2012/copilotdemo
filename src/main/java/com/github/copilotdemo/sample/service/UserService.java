package com.github.copilotdemo.sample.service;

import com.github.copilotdemo.client.Service;
import com.github.copilotdemo.common.RegisterService;

public interface UserService {

    @Service(url = "service1")
    Integer insertUserId(String userId);
}