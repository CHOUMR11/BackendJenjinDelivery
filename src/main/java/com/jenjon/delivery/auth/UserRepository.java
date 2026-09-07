package com.jenjon.delivery.auth;

import com.jenjon.delivery.auth.domain.Role;
import com.jenjon.delivery.auth.domain.StatutCompte;
import com.jenjon.delivery.auth.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelephone(String telephone);
    boolean existsByTelephone(String telephone);
    Page<User> findByStatutCompte(StatutCompte statutCompte, Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);
}
