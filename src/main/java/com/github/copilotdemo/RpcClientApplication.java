package com.github.copilotdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(RpcClientApplication.class, args);
    }


//    @CommandRunner
//    public void run() {
//        UserService userService = RpcClientProxy.createProxy(UserService.class);
//        User userByUserId = userService.getUserByUserId(10);
//        System.out.println("从服务端得到的user为：" + userByUserId);
//
//        User user = User.builder().userName("张三").id(100).sex(true).build();
//        Integer integer = userService.insertUserId(user);
//        System.out.println("向服务端插入数据：" + integer);
//    }
}