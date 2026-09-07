package com.jenjon.delivery.colis;

import com.jenjon.delivery.colis.domain.Colis;
import com.jenjon.delivery.colis.domain.StatutColis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColisRepository extends JpaRepository<Colis, Long> {
    Optional<Colis> findByCodeBarres(String codeBarres);
    Page<Colis> findByStatut(StatutColis statut, Pageable pageable);
    List<Colis> findByAgenceOrigineId(Long agenceId);
}
