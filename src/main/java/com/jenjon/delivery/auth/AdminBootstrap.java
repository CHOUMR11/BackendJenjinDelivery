package com.jenjon.delivery.auth;

import com.jenjon.delivery.auth.domain.Role;
import com.jenjon.delivery.auth.domain.StatutCompte;
import com.jenjon.delivery.auth.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Amorçage du tout premier compte Admin au démarrage.
 *
 * Problème résolu : POST /api/auth/register est réservé aux expéditeurs,
 * et POST /api/auth/interne (qui crée les Admin/Superviseur/Chef d'agence/
 * Livreur) est lui-même réservé aux Admin -> sans ce bootstrap, aucun Admin
 * ne pourrait jamais être créé ("œuf et la poule").
 *
 * Ne fait rien si un Admin existe déjà. Le mot de passe DOIT être changé
 * après la première connexion.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jenjon.bootstrap.admin-telephone:20000000}")
    private String adminTelephone;

    @Value("${jenjon.bootstrap.admin-password:ChangeMoiImmediatement123!}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean adminExiste = userRepository.findByRole(Role.ADMIN, org.springframework.data.domain.Pageable.ofSize(1))
                .hasContent();
        if (adminExiste) {
            return;
        }

        User admin = User.builder()
                .nomComplet("Administrateur Jenjon")
                .telephone(adminTelephone)
                .motDePasse(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .actif(true)
                .statutCompte(StatutCompte.VALIDE)
                .build();
        userRepository.save(admin);

        log.warn("=== Compte Admin initial créé — téléphone: {} — CHANGEZ CE MOT DE PASSE IMMÉDIATEMENT ===", adminTelephone);
    }
}
