package com.epermit.register.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockCodeResponseDTO {
    private String blockCode;

    public BlockCodeResponseDTO() {
    }

    public BlockCodeResponseDTO(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }
}
