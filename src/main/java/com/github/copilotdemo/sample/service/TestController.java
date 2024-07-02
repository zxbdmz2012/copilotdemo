package com.github.copilotdemo.sample.service;

import com.github.copilotdemo.client.RPCClientProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    RPCClientProxy rpcClientProxy;

    @Autowired
    UserService userService;

    @GetMapping("/insert1")
    public Integer insertUserId(String userId) {
        return userService.insertUserId(userId);
    }

    @GetMapping("/insert2")
    public Integer insertUserId2(String userId) {
        UserService proxy = rpcClientProxy.getProxy(UserService.class);
        return proxy.insertUserId(userId);
    }
}
