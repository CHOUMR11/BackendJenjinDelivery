package com.jenjon.delivery.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implémentation par défaut (MVP) : log uniquement.
 * Remplacée automatiquement dès qu'une vraie implémentation (ex: TwilioSmsProvider)
 * est ajoutée et activée via jenjon.notification.sms.enabled=true.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "jenjon.notification.sms", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LoggingSmsProvider implements SmsProvider {
    @Override
    public void envoyer(String telephone, String message) {
        log.info("[SMS-SIMULÉ] -> {} : {}", telephone, message);
    }
}
