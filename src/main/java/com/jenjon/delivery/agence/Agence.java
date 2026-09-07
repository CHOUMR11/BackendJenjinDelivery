package com.jenjon.delivery.agence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // ex: "06" (Gabes)

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String ville;

    private String adresse;

    private Long chefAgenceId; // référence vers User (role CHEF_AGENCE)

    @Builder.Default
    private boolean active = true;
}
