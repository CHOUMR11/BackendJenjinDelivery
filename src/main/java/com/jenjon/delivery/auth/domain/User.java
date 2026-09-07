package com.jenjon.delivery.auth.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomComplet;

    @Column(nullable = false, unique = true)
    private String telephone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Agence de rattachement (nullable pour Admin/Expéditeur)
    private Long agenceId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CanalNotification canalNotificationPrefere = CanalNotification.EMAIL;

    /**
     * Comme chez Intigo : l'inscription (self-signup) crée un compte EN_ATTENTE_VALIDATION.
     * Un Admin/Superviseur doit valider le compte avant que l'utilisateur puisse
     * se connecter et utiliser l'API (cf. AuthService.validerCompte).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutCompte statutCompte = StatutCompte.EN_ATTENTE_VALIDATION;

    @Builder.Default
    private boolean actif = true;

    public enum CanalNotification { PUSH, SMS, EMAIL }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    @JsonIgnore
    public String getPassword() { return motDePasse; }

    @Override
    public String getUsername() { return telephone; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return actif; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return actif && statutCompte == StatutCompte.VALIDE; }
}
