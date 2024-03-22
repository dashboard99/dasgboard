package com.ecommerce.server.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accesstoken;
    private String tokenType ="Bearer ";

    public AuthResponseDTO(String accesstoken){
        this.accesstoken = accesstoken;
    }
}
