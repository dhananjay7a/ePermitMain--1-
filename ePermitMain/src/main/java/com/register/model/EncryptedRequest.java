package com.register.model;

import lombok.Data;

@Data
public class EncryptedRequest {
    private String encryptedData;
    private String iv;
}
