package com.organisation.dto;

import lombok.Data;

@Data
public class ChangeDefaultPasswordDTO {
    private String userId;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;

}
