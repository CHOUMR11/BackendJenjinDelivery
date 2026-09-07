package com.jenjon.delivery.auth.dto;

import com.jenjon.delivery.auth.domain.Role;
import com.jenjon.delivery.auth.domain.StatutCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String nomComplet;
    private Role role;
    private StatutCompte statutCompte;
    private String message;
}
