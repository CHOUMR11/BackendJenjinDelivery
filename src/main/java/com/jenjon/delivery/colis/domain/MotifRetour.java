package com.jenjon.delivery.colis.domain;

/**
 * Motifs de retour sélectionnables par le livreur (écran "Retour : choisir un motif").
 */
public enum MotifRetour {
    A_VERIFIER_AVEC_EXPEDITEUR,
    TROIS_TENTATIVES_ACCOMPLIES,
    PAS_DE_REPONSE,
    INJOIGNABLE,
    FERMEE,
    CLIENT_DISPONIBLE_DEMAIN,
    CLIENT_NON_DISPONIBLE_DATE,
    ADRESSE_INCORRECTE,
    ADRESSE_INCOMPLETE,
    PARCOURS_NON_TERMINE
}
