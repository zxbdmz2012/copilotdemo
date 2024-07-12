package com.github.copilot.user.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Data
public class UserContextConstants {

    public static String userIdHeader;

    @Value("${userId.header:userId}")
    public  String header;

    @PostConstruct
    public void init() {
        userIdHeader = header;
    }

}