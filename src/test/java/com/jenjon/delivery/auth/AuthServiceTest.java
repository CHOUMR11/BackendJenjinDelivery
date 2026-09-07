package com.jenjon.delivery.auth;

import com.jenjon.delivery.auth.domain.Role;
import com.jenjon.delivery.auth.domain.User;
import com.jenjon.delivery.auth.dto.AuthResponse;
import com.jenjon.delivery.auth.dto.RegisterRequest;
import com.jenjon.delivery.shared.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_creeUnUtilisateurEtRenvoieUnToken() {
        RegisterRequest request = new RegisterRequest();
        request.setNomComplet("Amine Test");
        request.setTelephone("20111222");
        request.setMotDePasse("secret123");
        request.setRole(Role.EXPEDITEUR);

        when(userRepository.existsByTelephone("20111222")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken(any(), any())).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getRole()).isEqualTo(Role.EXPEDITEUR);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_refuseUnRoleInterne() {
        // Seuls EXPEDITEUR/EXPEDITEUR_PRO peuvent s'auto-inscrire ; les rôles
        // internes (Chef d'agence, Livreur, Admin...) passent par creerCompteInterne().
        RegisterRequest request = new RegisterRequest();
        request.setNomComplet("Faux Admin");
        request.setTelephone("20111333");
        request.setMotDePasse("secret123");
        request.setRole(Role.CHEF_AGENCE);

        when(userRepository.existsByTelephone("20111333")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_refuseSiTelephoneDejaUtilise() {
        RegisterRequest request = new RegisterRequest();
        request.setTelephone("20111222");
        request.setMotDePasse("secret123");
        request.setNomComplet("Test");

        when(userRepository.existsByTelephone("20111222")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }
}
