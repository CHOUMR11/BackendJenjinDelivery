package com.jenjon.delivery.livraison;

import com.jenjon.delivery.colis.ColisService;
import com.jenjon.delivery.colis.domain.Colis;
import com.jenjon.delivery.colis.domain.StatutColis;
import com.jenjon.delivery.livraison.domain.BonDeLivraison;
import com.jenjon.delivery.livraison.domain.StatutBonLivraison;
import com.jenjon.delivery.livraison.dto.BilanTournee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final ColisService colisService;

    public BonDeLivraison create(BonDeLivraison bl) {
        if (bl.getNumeroBL() == null || bl.getNumeroBL().isBlank()) {
            bl.setNumeroBL(genererNumeroBL());
        }
        bl.setStatut(StatutBonLivraison.CREE);
        return livraisonRepository.save(bl);
        // NB: ici, publier un événement pour notifier le livreur de sa nouvelle tournée.
    }

    public Page<BonDeLivraison> findAll(Pageable pageable) {
        return livraisonRepository.findAll(pageable);
    }

    public BonDeLivraison findById(Long id) {
        return livraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bon de livraison introuvable : " + id));
    }

    public List<BonDeLivraison> findByLivreur(Long livreurId) {
        return livraisonRepository.findByLivreurId(livreurId);
    }

    public BonDeLivraison updateStatut(Long id, StatutBonLivraison statut) {
        BonDeLivraison bl = findById(id);
        bl.setStatut(statut);
        return livraisonRepository.save(bl);
    }

    private String genererNumeroBL() {
        // Basé sur l'horodatage + suffixe aléatoire : pas de compteur en mémoire
        // qui repartirait à zéro au redémarrage (source de doublons potentiels).
        String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String suffixe = String.valueOf(System.currentTimeMillis() % 100000)
                + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
        return prefix + "-" + suffixe;
    }

    /**
     * "Bilan du jour" / compteurs du runsheet de l'app Livreur : Total, En cours,
     * Livrés, Retours + total encaissé et taux de réussite, calculés à partir
     * des colis affectés à ce bon de livraison.
     */
    public BilanTournee bilan(Long bonDeLivraisonId) {
        BonDeLivraison bl = findById(bonDeLivraisonId);
        List<Long> colisIds = bl.getColisIds() != null ? bl.getColisIds() : List.of();

        int livres = 0, retours = 0, enCours = 0;
        BigDecimal totalEncaisse = BigDecimal.ZERO;

        for (Long colisId : colisIds) {
            Colis colis = colisService.findById(colisId);
            if (colis.getStatut() == StatutColis.LIVRE) {
                livres++;
                if (colis.getMontantEncaisse() != null) {
                    totalEncaisse = totalEncaisse.add(colis.getMontantEncaisse());
                }
            } else if (colis.getStatut() == StatutColis.RETOURNE) {
                retours++;
            } else {
                enCours++;
            }
        }

        int total = colisIds.size();
        double tauxReussite = total > 0 ? (livres * 100.0 / total) : 0.0;

        return BilanTournee.builder()
                .total(total)
                .enCours(enCours)
                .livres(livres)
                .retours(retours)
                .totalEncaisse(totalEncaisse)
                .tauxReussitePourcent(Math.round(tauxReussite * 10.0) / 10.0)
                .build();
    }
}
