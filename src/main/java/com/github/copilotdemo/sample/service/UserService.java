package com.github.copilotdemo.sample.service;

import com.github.copilotdemo.client.Service;

public interface UserService {

    @Service(url = "service1")
    Integer insertUserId(String userId);
}