package com.jenjon.delivery.auth;

import com.jenjon.delivery.auth.domain.Role;
import com.jenjon.delivery.auth.domain.StatutCompte;
import com.jenjon.delivery.auth.domain.User;
import com.jenjon.delivery.auth.dto.AuthResponse;
import com.jenjon.delivery.auth.dto.LoginRequest;
import com.jenjon.delivery.auth.dto.RegisterRequest;
import com.jenjon.delivery.shared.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Flux d'inscription inspiré d'Intigo : "Créez votre compte en 15 minutes.
 * Notre équipe valide votre inscription." -> self-signup, compte créé en
 * statut EN_ATTENTE_VALIDATION, connexion bloquée tant qu'un Admin/Superviseur
 * n'a pas validé le compte (cf. validerCompte / rejeterCompte).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByTelephone(request.getTelephone())) {
            throw new IllegalArgumentException("Un utilisateur existe déjà avec ce numéro de téléphone.");
        }

        // Le self-signup public est réservé aux expéditeurs (comme Intigo).
        // Les rôles internes (Admin, Superviseur, Chef d'agence, Livreur) ne
        // peuvent PAS être créés ici : ils passent par creerCompteInterne()
        // (réservé aux Admin), sinon n'importe qui pourrait s'auto-déclarer Admin.
        Role role = request.getRole();
        if (role != Role.EXPEDITEUR && role != Role.EXPEDITEUR_PRO) {
            throw new IllegalArgumentException(
                "L'inscription publique est réservée aux expéditeurs. "
                + "Les comptes internes (agence, livreur, admin...) sont créés par un administrateur.");
        }

        User user = User.builder()
                .nomComplet(request.getNomComplet())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(role)
                .actif(true)
                .statutCompte(StatutCompte.EN_ATTENTE_VALIDATION)
                .build();

        userRepository.save(user);

        // Un token est émis mais restera inutilisable (401) tant que le compte
        // n'est pas validé : voir la vérification isEnabled() dans JwtAuthFilter.
        String token = jwtUtil.generateToken(user, Map.of("role", user.getRole().name(), "userId", user.getId()));

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .nomComplet(user.getNomComplet())
                .role(user.getRole())
                .statutCompte(user.getStatutCompte())
                .message("Inscription reçue. Votre compte est en attente de validation par notre équipe.")
                .build();
    }

    /**
     * Création d'un compte interne (Admin, Superviseur, Chef d'agence, Livreur)
     * — réservée aux Admin (cf. @PreAuthorize sur le contrôleur). Auto-validé :
     * pas besoin de passer par la file d'attente puisque c'est déjà un Admin
     * qui le crée en connaissance de cause.
     */
    public AuthResponse creerCompteInterne(RegisterRequest request) {
        if (userRepository.existsByTelephone(request.getTelephone())) {
            throw new IllegalArgumentException("Un utilisateur existe déjà avec ce numéro de téléphone.");
        }
        if (request.getRole() == Role.EXPEDITEUR || request.getRole() == Role.EXPEDITEUR_PRO) {
            throw new IllegalArgumentException(
                "Utilisez /api/auth/register pour créer un compte expéditeur.");
        }
        if (request.getRole() == Role.CHEF_AGENCE && request.getAgenceId() == null) {
            throw new IllegalArgumentException("Un Chef d'agence doit être rattaché à une agence (agenceId).");
        }

        User user = User.builder()
                .nomComplet(request.getNomComplet())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .agenceId(request.getAgenceId())
                .actif(true)
                .statutCompte(StatutCompte.VALIDE)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user, Map.of("role", user.getRole().name(), "userId", user.getId()));

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .nomComplet(user.getNomComplet())
                .role(user.getRole())
                .statutCompte(user.getStatutCompte())
                .message("Compte interne créé et déjà actif.")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getTelephone(), request.getMotDePasse())
            );
        } catch (DisabledException ex) {
            throw new IllegalStateException("Votre compte est en attente de validation par notre équipe.");
        }

        User user = userRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides."));

        String token = jwtUtil.generateToken(user, Map.of("role", user.getRole().name(), "userId", user.getId()));

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .nomComplet(user.getNomComplet())
                .role(user.getRole())
                .statutCompte(user.getStatutCompte())
                .build();
    }

    // ------- Validation des inscriptions par un Admin/Superviseur (comme Intigo) -------

    public Page<User> findComptesEnAttente(Pageable pageable) {
        return userRepository.findByStatutCompte(StatutCompte.EN_ATTENTE_VALIDATION, pageable);
    }

    /** Répond à "qui sont les chefs d'agence / livreurs / superviseurs ?" */
    public Page<User> findByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    public User validerCompte(Long userId) {
        User user = getUser(userId);
        user.setStatutCompte(StatutCompte.VALIDE);
        return userRepository.save(user);
        // NB: c'est ici qu'on déclencherait une notification email "Votre compte a été validé".
    }

    public User rejeterCompte(Long userId) {
        User user = getUser(userId);
        user.setStatutCompte(StatutCompte.REJETE);
        return userRepository.save(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userId));
    }
}
