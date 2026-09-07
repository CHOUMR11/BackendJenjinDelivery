package com.jenjon.delivery.auth;

import com.jenjon.delivery.auth.domain.User;
import com.jenjon.delivery.auth.dto.AuthResponse;
import com.jenjon.delivery.auth.dto.LoginRequest;
import com.jenjon.delivery.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Création d'un compte interne (Chef d'agence, Livreur, Superviseur, Admin)
     * — réservée aux Admin. Répond à "qui sont les chefs d'agence ?" : ce sont
     * des comptes créés ici, avec un rôle CHEF_AGENCE et un agenceId obligatoire.
     */
    @PostMapping("/interne")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> creerCompteInterne(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.creerCompteInterne(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /** Liste des utilisateurs internes par rôle — ex: GET /api/auth/internes?role=CHEF_AGENCE */
    @GetMapping("/internes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<Page<User>> listerParRole(@RequestParam com.jenjon.delivery.auth.domain.Role role, Pageable pageable) {
        return ResponseEntity.ok(authService.findByRole(role, pageable));
    }

    /** Liste des inscriptions en attente de validation (comme le back-office Intigo). */
    @GetMapping("/comptes-en-attente")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<Page<User>> comptesEnAttente(Pageable pageable) {
        return ResponseEntity.ok(authService.findComptesEnAttente(pageable));
    }

    @PatchMapping("/{userId}/valider")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<User> validerCompte(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.validerCompte(userId));
    }

    @PatchMapping("/{userId}/rejeter")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<User> rejeterCompte(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.rejeterCompte(userId));
    }
}
