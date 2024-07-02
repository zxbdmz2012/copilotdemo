package com.github.copilotdemo.sample.service;

import com.github.copilotdemo.client.Service;
import com.github.copilotdemo.sample.User;

public interface UserService {
    @Service(url = "${service.service1}")
    User getUserByUserId(Integer id);

    @Service(url = "${service.service2}")
    Integer insertUserId(User user);
}