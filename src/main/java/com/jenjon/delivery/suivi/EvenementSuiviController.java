package com.jenjon.delivery.suivi;

import com.jenjon.delivery.suivi.domain.EvenementSuivi;
import com.jenjon.delivery.suivi.domain.TypeEvenementSuivi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Timeline de suivi détaillée, consultable côté expéditeur pour un colis
 * ou un courrier (mêmes endpoints pour les deux types).
 * Ex: GET /api/colis/42/timeline
 */
@RestController
@RequestMapping("/api/colis/{colisId}/timeline")
@RequiredArgsConstructor
public class EvenementSuiviController {

    private final EvenementSuiviService evenementSuiviService;

    @GetMapping
    public ResponseEntity<List<EvenementSuivi>> timeline(@PathVariable Long colisId) {
        return ResponseEntity.ok(evenementSuiviService.timeline(colisId));
    }

    /** Ajout manuel d'un événement (ex: depuis l'app Livreur — appel, SMS, retour dépôt...). */
    @PostMapping
    public ResponseEntity<EvenementSuivi> ajouter(
            @PathVariable Long colisId,
            @RequestParam TypeEvenementSuivi type,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String livreurNom,
            @RequestParam(required = false) Integer dureeAppelSecondes,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(evenementSuiviService.ajouter(colisId, type, ville, livreurNom, dureeAppelSecondes, note));
    }
}
