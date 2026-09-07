package com.jenjon.delivery.notification;

import com.jenjon.delivery.notification.event.ColisStatutChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ColisNotificationListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onColisStatutChange(ColisStatutChangeEvent event) {
        String message = "Votre colis " + event.getCodeBarres() + " est maintenant : " + event.getNouveauStatut();
        notificationService.notifierChangementStatutColis(
                event.getCanalPrefere(),
                event.getDestinataireTelephone(),
                event.getDestinataireEmail(),
                message
        );
    }
}
