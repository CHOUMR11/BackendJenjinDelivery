package com.jenjon.delivery.notification;

import com.jenjon.delivery.notification.provider.PushProvider;
import com.jenjon.delivery.notification.provider.SmsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service centralisant l'envoi de notifications (push, SMS, email).
 * - Email : réellement fonctionnel via JavaMailSender (à activer avec
 *   jenjon.notification.email.enabled=true + credentials SMTP).
 * - SMS / Push : délèguent à une implémentation de SmsProvider / PushProvider
 *   (implémentation "log" par défaut, à remplacer par un vrai fournisseur).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final SmsProvider smsProvider;
    private final PushProvider pushProvider;

    @Value("${jenjon.notification.email.enabled}")
    private boolean emailEnabled;

    @Value("${jenjon.notification.email.from}")
    private String emailFrom;

    public void envoyerEmail(String destinataire, String sujet, String corps) {
        if (!emailEnabled || destinataire == null || destinataire.isBlank()) {
            log.info("[EMAIL-SIMULÉ] -> {} : {} — {}", destinataire, sujet, corps);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(destinataire);
            message.setSubject(sujet);
            message.setText(corps);
            mailSender.send(message);
        } catch (Exception e) {
            // Ne doit jamais faire échouer l'opération métier appelante (souvent async) :
            // on logue proprement plutôt que de laisser une MailException remonter.
            log.error("Échec d'envoi d'email à {} : {}", destinataire, e.getMessage());
        }
    }

    public void envoyerSms(String telephone, String message) {
        smsProvider.envoyer(telephone, message);
    }

    public void envoyerPush(String tokenAppareil, String titre, String message) {
        pushProvider.envoyer(tokenAppareil, titre, message);
    }

    /**
     * Notifie le destinataire du changement de statut de son colis.
     * Décision produit actuelle : UNIQUEMENT par email pour le moment
     * (SMS et push sont implémentés mais volontairement désactivés en attendant
     * qu'un fournisseur SMS/push soit choisi — cf. jenjon.notification.sms/push.enabled).
     * Le paramètre `canal` est conservé pour compatibilité future : quand SMS/push
     * seront réactivés, il suffira de retirer le forçage ci-dessous.
     */
    public void notifierChangementStatutColis(String canal, String telephone, String email, String message) {
        envoyerEmail(email, "Mise à jour de votre colis", message);
    }

    public void notifierSuperviseur(String message) {
        log.info("[NOTIFICATION][SUPERVISEUR] {}", message);
    }
}
