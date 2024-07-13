//package com.github.copilot;
//
//import com.github.copilot.configcenter.client.annotation.EnableConfigClient;
//import com.github.copilot.configcenter.server.annotation.EnableConfigServer;
//import com.github.copilot.exceptionhandler.annotation.EnableGlobalExceptionHandler;
//import com.github.copilot.user.annotation.EnableUserInfoArgResolver;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//@SpringBootApplication
//@EnableUserInfoArgResolver
//@EnableScheduling
//@EnableGlobalExceptionHandler
////@EntityScan(basePackages = {"com.github.copilot.task.entity", "com.github.copilot.configcenter.entity","com.github.copilot.exceptionhandler.entity"})
////@EnableJpaRepositories({"com.github.copilot.task.repository", "com.github.copilot.configcenter.repository","com.github.copilot.exceptionhandler.repository"})
//
//@EnableConfigServer
//@EnableConfigClient
//public class Application {
//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//    }
//}