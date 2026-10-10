package com.supermarket.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String phone;
    private String employeeCode;
    private String password;
    private String imageUrl;

}
