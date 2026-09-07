package com.jenjon.delivery.suivi.domain;

/**
 * Types d'événements affichés dans la timeline de suivi côté expéditeur
 * (inspiré du fournisseur First Delivery : dépôt, en cours, appels, SMS,
 * retour dépôt, client injoignable...).
 */
public enum TypeEvenementSuivi {
    AU_DEPOT,
    RECU_AU_DEPOT,
    EN_COURS_LIVRAISON,
    APPEL_SORTANT,
    APPEL_ENTRANT,
    SMS_ENVOYE_CLIENT,
    CLIENT_INJOIGNABLE,
    RETOUR_DEPOT,
    LIVRE,
    RETOURNE
}
