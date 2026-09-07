package com.jenjon.delivery.reglement;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reglements")
@RequiredArgsConstructor
public class ReglementController {

    private final ReglementService reglementService;

    @PostMapping
    public ResponseEntity<Reglement> create(@RequestBody Reglement reglement) {
        return ResponseEntity.ok(reglementService.create(reglement));
    }

    @GetMapping("/beneficiaire/{beneficiaireId}")
    public ResponseEntity<Page<Reglement>> findByBeneficiaire(
            @PathVariable Long beneficiaireId,
            @RequestParam TypeBeneficiaire type,
            Pageable pageable) {
        return ResponseEntity.ok(reglementService.findByBeneficiaire(beneficiaireId, type, pageable));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<Page<Reglement>> findByStatut(@PathVariable StatutReglement statut, Pageable pageable) {
        return ResponseEntity.ok(reglementService.findByStatut(statut, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reglement> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reglementService.findById(id));
    }

    @PatchMapping("/{id}/valider")
    public ResponseEntity<Reglement> valider(@PathVariable Long id) {
        return ResponseEntity.ok(reglementService.valider(id));
    }

    @PatchMapping("/{id}/payer")
    public ResponseEntity<Reglement> marquerPaye(@PathVariable Long id) {
        return ResponseEntity.ok(reglementService.marquerPaye(id));
    }
}
