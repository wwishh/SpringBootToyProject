package com.wish.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordConfirm;
}
