package com.github.copilot.db;

public interface PasswordDecryptor {
    String decrypt(String encryptedPassword);
}