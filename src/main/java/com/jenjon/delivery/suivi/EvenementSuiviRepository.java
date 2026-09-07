package com.jenjon.delivery.suivi;

import com.jenjon.delivery.suivi.domain.EvenementSuivi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementSuiviRepository extends JpaRepository<EvenementSuivi, Long> {
    List<EvenementSuivi> findByColisIdOrderByDateEvenementAsc(Long colisId);
}
