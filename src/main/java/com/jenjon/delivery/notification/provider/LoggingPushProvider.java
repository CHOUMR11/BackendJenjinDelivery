package com.jenjon.delivery.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "jenjon.notification.push", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LoggingPushProvider implements PushProvider {
    @Override
    public void envoyer(String tokenAppareil, String titre, String message) {
        log.info("[PUSH-SIMULÉ] -> {} : {} — {}", tokenAppareil, titre, message);
    }
}
