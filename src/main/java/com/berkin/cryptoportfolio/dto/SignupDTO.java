package com.berkin.cryptoportfolio.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupDTO {
    private String username;
    private String email;
    private String password;
}