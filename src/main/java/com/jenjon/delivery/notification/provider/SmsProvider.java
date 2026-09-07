package com.jenjon.delivery.notification.provider;

/**
 * Abstraction pour brancher un fournisseur SMS tunisien/international
 * (ex: Tunisie Telecom SMS Gateway, Twilio, Vonage...).
 * Implémenter cette interface avec le SDK/API HTTP du fournisseur choisi,
 * puis configurer jenjon.notification.sms.enabled=true dans application.yml.
 */
public interface SmsProvider {
    void envoyer(String telephone, String message);
}
