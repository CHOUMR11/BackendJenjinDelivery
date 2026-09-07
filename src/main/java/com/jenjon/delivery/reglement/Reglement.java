package com.jenjon.delivery.reglement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Récapitulatif des montants (contre-remboursement) collectés par un livreur,
 * ou dus à un expéditeur professionnel, sur une période donnée.
 * La fréquence (quotidien / hebdomadaire / instantané) est configurable par bénéficiaire.
 */
@Entity
@Table(name = "reglements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reglement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeBeneficiaire typeBeneficiaire;

    @Column(nullable = false)
    private Long beneficiaireId; // userId du livreur, ou id de l'ExpediteurPro

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FrequenceReglement frequence = FrequenceReglement.HEBDOMADAIRE;

    private LocalDate periodeDebut;
    private LocalDate periodeFin;

    @ElementCollection
    @CollectionTable(name = "reglement_bons_livraison", joinColumns = @JoinColumn(name = "reglement_id"))
    @Column(name = "bon_livraison_id")
    private List<Long> bonsDeLivraisonIds;

    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal montantTotal;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutReglement statut = StatutReglement.EN_ATTENTE;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime datePaiement;
}
