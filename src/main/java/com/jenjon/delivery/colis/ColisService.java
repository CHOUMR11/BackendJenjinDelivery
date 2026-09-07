package com.jenjon.delivery.colis;

import com.jenjon.delivery.colis.domain.Colis;
import com.jenjon.delivery.colis.domain.MotifRetour;
import com.jenjon.delivery.colis.domain.StatutColis;
import com.jenjon.delivery.notification.event.ColisStatutChangeEvent;
import com.jenjon.delivery.suivi.EvenementSuiviService;
import com.jenjon.delivery.suivi.domain.TypeEvenementSuivi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColisService {

    private final ColisRepository colisRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EvenementSuiviService evenementSuiviService;

    // Correspondance StatutColis -> événement affiché dans la timeline expéditeur
    private static final Map<StatutColis, TypeEvenementSuivi> STATUT_VERS_EVENEMENT = Map.of(
            StatutColis.ENREGISTRE, TypeEvenementSuivi.AU_DEPOT,
            StatutColis.COLLECTE, TypeEvenementSuivi.RECU_AU_DEPOT,
            StatutColis.AFFECTE, TypeEvenementSuivi.EN_COURS_LIVRAISON,
            StatutColis.EN_COURS_LIVRAISON, TypeEvenementSuivi.EN_COURS_LIVRAISON,
            StatutColis.LIVRE, TypeEvenementSuivi.LIVRE,
            StatutColis.RETOURNE, TypeEvenementSuivi.RETOURNE
    );

    public Colis create(Colis colis) {
        if (colis.getCodeBarres() == null || colis.getCodeBarres().isBlank()) {
            colis.setCodeBarres(genererCodeBarres());
        }
        colis.setStatut(StatutColis.ENREGISTRE);
        Colis saved = colisRepository.save(colis);

        // Premier événement de la timeline expéditeur : "Au dépôt"
        evenementSuiviService.ajouter(saved.getId(), TypeEvenementSuivi.AU_DEPOT, null, null, null,
                "Colis enregistré à l'agence");

        return saved;
    }

    public Page<Colis> findAll(Pageable pageable) {
        return colisRepository.findAll(pageable);
    }

    public Colis findById(Long id) {
        return colisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Colis introuvable : " + id));
    }

    public Colis findByCodeBarres(String codeBarres) {
        return colisRepository.findByCodeBarres(codeBarres)
                .orElseThrow(() -> new IllegalArgumentException("Colis introuvable pour le code-barres : " + codeBarres));
    }

    /**
     * Met à jour le statut du colis, ajoute automatiquement l'événement
     * correspondant à la timeline de suivi expéditeur (cf. module suivi),
     * et publie un événement interne pour les notifications.
     */
    public Colis updateStatut(Long id, StatutColis nouveauStatut, String destinataireTelephone,
                               String destinataireEmail, String canalPrefere) {
        Colis colis = findById(id);
        colis.setStatut(nouveauStatut);
        Colis saved = colisRepository.save(colis);

        TypeEvenementSuivi typeEvenement = STATUT_VERS_EVENEMENT.getOrDefault(nouveauStatut, TypeEvenementSuivi.EN_COURS_LIVRAISON);
        evenementSuiviService.ajouter(saved.getId(), typeEvenement, null, null, null, null);

        eventPublisher.publishEvent(new ColisStatutChangeEvent(
                this, saved.getId(), saved.getCodeBarres(),
                destinataireTelephone, destinataireEmail, canalPrefere, nouveauStatut.name()
        ));

        return saved;
    }

    private String genererCodeBarres() {
        return "JD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * Bouton vert "LIVRER — X DT" de l'app Livreur : marque le colis comme livré
     * et enregistre le montant réellement encaissé (peut différer du montant TTC
     * prévu en cas de règlement partiel).
     */
    public Colis livrer(Long id, BigDecimal montantEncaisse) {
        Colis colis = findById(id);
        colis.setStatut(StatutColis.LIVRE);
        colis.setMontantEncaisse(montantEncaisse != null ? montantEncaisse : colis.getMontantTTC());
        Colis saved = colisRepository.save(colis);

        evenementSuiviService.ajouter(saved.getId(), TypeEvenementSuivi.LIVRE, null, null, null,
                "Livré — " + saved.getMontantEncaisse() + " DT encaissés");

        eventPublisher.publishEvent(new ColisStatutChangeEvent(
                this, saved.getId(), saved.getCodeBarres(), null, null, null, StatutColis.LIVRE.name()
        ));

        return saved;
    }

    /**
     * Bouton rouge "Retour : choisir un motif" de l'app Livreur : marque le colis
     * comme retourné avec le motif sélectionné (mêmes motifs que l'écran fourni).
     */
    public Colis retourner(Long id, MotifRetour motif, LocalDate dateDisponibiliteClient) {
        Colis colis = findById(id);
        colis.setStatut(StatutColis.RETOURNE);
        colis.setMotifRetour(motif);
        colis.setDateDisponibiliteClient(dateDisponibiliteClient);
        Colis saved = colisRepository.save(colis);

        TypeEvenementSuivi typeEvenement = (motif == MotifRetour.INJOIGNABLE)
                ? TypeEvenementSuivi.CLIENT_INJOIGNABLE
                : TypeEvenementSuivi.RETOUR_DEPOT;
        evenementSuiviService.ajouter(saved.getId(), typeEvenement, null, null, null,
                "Motif de retour : " + motif.name());

        eventPublisher.publishEvent(new ColisStatutChangeEvent(
                this, saved.getId(), saved.getCodeBarres(), null, null, null, StatutColis.RETOURNE.name()
        ));

        return saved;
    }
}
