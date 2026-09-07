package com.jenjon.delivery.livraison.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Résumé d'une tournée (runsheet), affiché en tête de l'app Livreur :
 * compteurs Total/En cours/Livrés/Retours, et "Bilan du jour" en bas
 * (colis livrés, total encaissé, colis retour, taux de réussite).
 */
@Data
@Builder
@AllArgsConstructor
public class BilanTournee {
    private int total;
    private int enCours;
    private int livres;
    private int retours;
    private BigDecimal totalEncaisse;
    private double tauxReussitePourcent;
}
