package com.epermit.register.dto;

import lombok.Data;

@Data
public class UserDigiSignRequestDto {

   
    private String thumbPrint;
    private String alias;
    private String issuer;
}
