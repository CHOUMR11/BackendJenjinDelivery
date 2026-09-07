package com.jenjon.delivery.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String telephone;

    @NotBlank
    private String motDePasse;
}
