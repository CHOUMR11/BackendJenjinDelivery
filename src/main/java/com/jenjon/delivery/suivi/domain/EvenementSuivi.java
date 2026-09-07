package com.jenjon.delivery.suivi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Un événement dans la timeline de suivi d'un colis/courrier, visible côté
 * expéditeur (agence, "en cours" avec nom du livreur, appel, SMS envoyé,
 * appel sortant avec durée, retour dépôt, client injoignable, etc.)
 */
@Entity
@Table(name = "evenements_suivi")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvenementSuivi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long colisId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvenementSuivi type;

    @Builder.Default
    private LocalDateTime dateEvenement = LocalDateTime.now();

    private String ville;              // ex: "GABES" — affiché en gros dans la timeline
    private String livreurNom;         // ex: "wassim ajeri"
    private Integer dureeAppelSecondes; // ex: 3 (pour "Appel sortant 3 secondes")
    private String note;               // texte libre optionnel
}
