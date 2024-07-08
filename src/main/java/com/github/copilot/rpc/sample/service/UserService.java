package com.github.copilot.rpc.sample.service;

import com.github.copilot.rpc.client.TargetService;

public interface UserService {

    @TargetService(url = "service1")
    Integer insertUserId(String userId);
}