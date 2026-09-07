package com.jenjon.delivery.document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BordereauData {
    private String agenceCode;
    private String agenceVille;
    private String codeBarres;
    private String typeLabel;      // "COLIS" ou "COURRIER"
    private String typeColor;      // couleur du badge (hex)
    private String numeroBL;
    private String dateBL;
    private String agenceOrigine;
    private String agenceDestination;

    private String expediteurNom;
    private String expediteurAdresse;
    private String expediteurTelephone;
    private String expediteurMatriculeFiscal;

    private String destinataireNom;
    private String destinataireAdresse;
    private String destinataireTelephone;

    private String designation;
    private Integer quantite;
    private String montant; // déjà formaté, ex: "107,000"

    private boolean ouverturePermise;
}
