package com.jenjon.delivery.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Publié par le module colis lorsqu'un statut change.
 * Le module notification écoute cet événement pour déclencher push/SMS/email,
 * sans que colis dépende directement de notification (cf. règle de conception
 * "communication inter-modules par événements").
 */
@Getter
public class ColisStatutChangeEvent extends ApplicationEvent {

    private final Long colisId;
    private final String codeBarres;
    private final String destinataireTelephone;
    private final String destinataireEmail;
    private final String canalPrefere; // PUSH, SMS, EMAIL
    private final String nouveauStatut;

    public ColisStatutChangeEvent(Object source, Long colisId, String codeBarres,
                                   String destinataireTelephone, String destinataireEmail,
                                   String canalPrefere, String nouveauStatut) {
        super(source);
        this.colisId = colisId;
        this.codeBarres = codeBarres;
        this.destinataireTelephone = destinataireTelephone;
        this.destinataireEmail = destinataireEmail;
        this.canalPrefere = canalPrefere;
        this.nouveauStatut = nouveauStatut;
    }
}
