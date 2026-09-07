package com.jenjon.delivery.colis;

import com.jenjon.delivery.colis.domain.Colis;
import com.jenjon.delivery.colis.domain.MotifRetour;
import com.jenjon.delivery.colis.domain.StatutColis;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/colis")
@RequiredArgsConstructor
public class ColisController {

    private final ColisService colisService;

    @PostMapping
    public ResponseEntity<Colis> create(@Valid @RequestBody Colis colis) {
        return ResponseEntity.ok(colisService.create(colis));
    }

    /** Liste paginée. Ex: GET /api/colis?page=0&size=20&sort=dateCreation,desc */
    @GetMapping
    public ResponseEntity<Page<Colis>> findAll(Pageable pageable) {
        return ResponseEntity.ok(colisService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Colis> findById(@PathVariable Long id) {
        return ResponseEntity.ok(colisService.findById(id));
    }

    @GetMapping("/code-barres/{codeBarres}")
    public ResponseEntity<Colis> findByCodeBarres(@PathVariable String codeBarres) {
        return ResponseEntity.ok(colisService.findByCodeBarres(codeBarres));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Colis> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutColis statut,
            @RequestParam(required = false) String destinataireTelephone,
            @RequestParam(required = false) String destinataireEmail,
            @RequestParam(required = false) String canalPrefere) {
        return ResponseEntity.ok(colisService.updateStatut(id, statut, destinataireTelephone, destinataireEmail, canalPrefere));
    }

    /** Bouton vert "LIVRER — X DT" de l'app Livreur. */
    @PatchMapping("/{id}/livrer")
    public ResponseEntity<Colis> livrer(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal montantEncaisse) {
        return ResponseEntity.ok(colisService.livrer(id, montantEncaisse));
    }

    /** Bouton rouge "Retour : choisir un motif" de l'app Livreur (mêmes motifs que l'écran fourni). */
    @PatchMapping("/{id}/retour")
    public ResponseEntity<Colis> retourner(
            @PathVariable Long id,
            @RequestParam MotifRetour motif,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dateDisponibiliteClient) {
        return ResponseEntity.ok(colisService.retourner(id, motif, dateDisponibiliteClient));
    }
}
