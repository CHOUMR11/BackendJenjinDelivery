package com.jenjon.delivery.reglement;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReglementService {

    private final ReglementRepository reglementRepository;

    public Reglement create(Reglement reglement) {
        reglement.setStatut(StatutReglement.EN_ATTENTE);
        return reglementRepository.save(reglement);
    }

    public Page<Reglement> findByBeneficiaire(Long beneficiaireId, TypeBeneficiaire type, Pageable pageable) {
        return reglementRepository.findByBeneficiaireIdAndTypeBeneficiaire(beneficiaireId, type, pageable);
    }

    public Page<Reglement> findByStatut(StatutReglement statut, Pageable pageable) {
        return reglementRepository.findByStatut(statut, pageable);
    }

    public Reglement findById(Long id) {
        return reglementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Règlement introuvable : " + id));
    }

    public Reglement valider(Long id) {
        Reglement r = findById(id);
        r.setStatut(StatutReglement.VALIDE);
        return reglementRepository.save(r);
    }

    public Reglement marquerPaye(Long id) {
        Reglement r = findById(id);
        r.setStatut(StatutReglement.PAYE);
        r.setDatePaiement(LocalDateTime.now());
        return reglementRepository.save(r);
    }
}
