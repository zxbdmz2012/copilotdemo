package com.github.copilot.user.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserContextConstants {

    @Value("${userId.header:userId}")
    public static String userIdHeader;

}