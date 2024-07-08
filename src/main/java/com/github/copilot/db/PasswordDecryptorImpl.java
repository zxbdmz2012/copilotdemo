package com.github.copilot.db;

import org.springframework.stereotype.Component;

@Component
public class PasswordDecryptorImpl implements PasswordDecryptor {
    public String decrypt(String encryptedPassword){
        return encryptedPassword;
    }
}