package com.jenjon.delivery.livraison.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bons_de_livraison")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonDeLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroBL; // ex: 202607-0083

    @Builder.Default
    private LocalDateTime dateBL = LocalDateTime.now();

    private Long agenceOrigineId;
    private Long agenceDestinationId;

    private String vehiculeImmatriculation;
    private Long livreurId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ModeLivraison modeLivraison = ModeLivraison.STANDARD;

    // Destinataire : renseigné à l'affectation (PAS à la création du colis)
    private String destinataireNom;
    private String destinataireAdresse;
    private String destinataireTelephone;

    @ElementCollection
    @CollectionTable(name = "bon_livraison_colis", joinColumns = @JoinColumn(name = "bon_livraison_id"))
    @Column(name = "colis_id")
    private List<Long> colisIds;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutBonLivraison statut = StatutBonLivraison.CREE;
}
