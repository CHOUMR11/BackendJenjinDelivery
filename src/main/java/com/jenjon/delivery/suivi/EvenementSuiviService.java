package com.jenjon.delivery.suivi;

import com.jenjon.delivery.suivi.domain.EvenementSuivi;
import com.jenjon.delivery.suivi.domain.TypeEvenementSuivi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementSuiviService {

    private final EvenementSuiviRepository repository;

    public EvenementSuivi ajouter(Long colisId, TypeEvenementSuivi type, String ville,
                                   String livreurNom, Integer dureeAppelSecondes, String note) {
        EvenementSuivi event = EvenementSuivi.builder()
                .colisId(colisId)
                .type(type)
                .ville(ville)
                .livreurNom(livreurNom)
                .dureeAppelSecondes(dureeAppelSecondes)
                .note(note)
                .build();
        return repository.save(event);
    }

    /** Timeline complète d'un colis, triée chronologiquement — vue "côté expéditeur". */
    public List<EvenementSuivi> timeline(Long colisId) {
        return repository.findByColisIdOrderByDateEvenementAsc(colisId);
    }
}
