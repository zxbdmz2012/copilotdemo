package com.github.copilotdemo.sample.service;

import com.github.copilotdemo.client.Service;

public interface UserService {

    @Service(url = "${service.service2}")
    Integer insertUserId(Object user);
}