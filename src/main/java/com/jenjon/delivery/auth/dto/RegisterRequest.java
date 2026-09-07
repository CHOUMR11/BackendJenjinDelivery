package com.jenjon.delivery.auth.dto;

import com.jenjon.delivery.auth.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String nomComplet;

    @NotBlank
    private String telephone;

    private String email;

    @NotBlank
    private String motDePasse;

    @NotNull
    private Role role;

    private Long agenceId;
}
