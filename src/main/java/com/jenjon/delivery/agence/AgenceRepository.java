package com.jenjon.delivery.agence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
    Optional<Agence> findByCode(String code);
}
