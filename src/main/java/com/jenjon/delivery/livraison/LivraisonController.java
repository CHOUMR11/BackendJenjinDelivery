package com.jenjon.delivery.livraison;

import com.jenjon.delivery.livraison.domain.BonDeLivraison;
import com.jenjon.delivery.livraison.domain.StatutBonLivraison;
import com.jenjon.delivery.livraison.dto.BilanTournee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;

    @PostMapping
    public ResponseEntity<BonDeLivraison> create(@RequestBody BonDeLivraison bl) {
        return ResponseEntity.ok(livraisonService.create(bl));
    }

    /** Liste paginée. Ex: GET /api/livraisons?page=0&size=20&sort=dateBL,desc */
    @GetMapping
    public ResponseEntity<Page<BonDeLivraison>> findAll(Pageable pageable) {
        return ResponseEntity.ok(livraisonService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BonDeLivraison> findById(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.findById(id));
    }

    @GetMapping("/livreur/{livreurId}")
    public ResponseEntity<List<BonDeLivraison>> findByLivreur(@PathVariable Long livreurId) {
        return ResponseEntity.ok(livraisonService.findByLivreur(livreurId));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<BonDeLivraison> updateStatut(@PathVariable Long id, @RequestParam StatutBonLivraison statut) {
        return ResponseEntity.ok(livraisonService.updateStatut(id, statut));
    }

    /** "Bilan du jour" / compteurs Total-En cours-Livrés-Retours du runsheet Livreur. */
    @GetMapping("/{id}/bilan")
    public ResponseEntity<BilanTournee> bilan(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.bilan(id));
    }
}
