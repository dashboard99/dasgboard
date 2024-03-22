package com.ecommerce.server.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String email;

}
