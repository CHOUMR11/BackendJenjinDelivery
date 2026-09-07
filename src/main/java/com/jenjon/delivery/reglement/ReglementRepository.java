package com.jenjon.delivery.reglement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReglementRepository extends JpaRepository<Reglement, Long> {
    Page<Reglement> findByBeneficiaireIdAndTypeBeneficiaire(Long beneficiaireId, TypeBeneficiaire type, Pageable pageable);
    Page<Reglement> findByStatut(StatutReglement statut, Pageable pageable);
}
