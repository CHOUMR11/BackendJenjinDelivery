package com.jenjon.delivery.notification.provider;

/**
 * Abstraction pour les notifications push (ex: Firebase Cloud Messaging).
 * Implémenter avec le SDK firebase-admin et le fichier de credentials
 * (jenjon.notification.push.firebase-credentials-path).
 */
public interface PushProvider {
    void envoyer(String tokenAppareil, String titre, String message);
}
