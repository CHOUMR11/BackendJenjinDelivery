package com.jenjon.delivery.colis.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "colis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeBarres;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private TypeColis type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ModeCollecte modeCollecte = ModeCollecte.DEPOT_AGENCE;

    // --- Expéditeur (saisi à la création, PAS de destinataire ici) ---
    @NotBlank
    @Column(nullable = false)
    private String expediteurNom;

    @NotBlank
    @Column(nullable = false)
    private String expediteurTelephone;

    private String expediteurAdresse;
    private String expediteurMatriculeFiscal; // optionnel, si société

    private String designation;

    @Builder.Default
    private Integer quantite = 1;

    @Column(precision = 10, scale = 3)
    private BigDecimal montantTTC;

    @Builder.Default
    private boolean ouverturePermise = false; // "Ouvrir le colis : Non/Oui"

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutColis statut = StatutColis.ENREGISTRE;

    private Long agenceOrigineId;

    // --- Résultat de la tournée (écran Livreur "runsheet") ---
    @Column(precision = 10, scale = 3)
    private BigDecimal montantEncaisse; // montant réellement encaissé à la livraison (contre-remboursement)

    @Enumerated(EnumType.STRING)
    private MotifRetour motifRetour; // renseigné si statut = RETOURNE

    private java.time.LocalDate dateDisponibiliteClient; // pour "Client disponible demain" / "Client non disponible (daté)"

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();
}
