package com.jenjon.delivery.document;

import com.jenjon.delivery.agence.Agence;
import com.jenjon.delivery.agence.AgenceService;
import com.jenjon.delivery.colis.ColisService;
import com.jenjon.delivery.colis.domain.Colis;
import com.jenjon.delivery.colis.domain.TypeColis;
import com.jenjon.delivery.livraison.LivraisonService;
import com.jenjon.delivery.livraison.domain.BonDeLivraison;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

/**
 * Expose la génération du bordereau d'envoi / bon de livraison en PDF,
 * au format défini dans templates/bordereau-template.html.
 *
 * Exemple : GET /api/documents/bordereau/{bonDeLivraisonId}/colis/{colisId}
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final BordereauPdfService bordereauPdfService;
    private final ColisService colisService;
    private final LivraisonService livraisonService;
    private final AgenceService agenceService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping(value = "/bordereau/{bonDeLivraisonId}/colis/{colisId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererBordereau(
            @PathVariable Long bonDeLivraisonId,
            @PathVariable Long colisId) {

        Colis colis = colisService.findById(colisId);
        BonDeLivraison bl = livraisonService.findById(bonDeLivraisonId);

        Agence agenceOrigine = bl.getAgenceOrigineId() != null ? agenceService.findById(bl.getAgenceOrigineId()) : null;
        Agence agenceDestination = bl.getAgenceDestinationId() != null ? agenceService.findById(bl.getAgenceDestinationId()) : null;

        BordereauData data = BordereauData.builder()
                .agenceCode(agenceOrigine != null ? agenceOrigine.getCode() : "-")
                .agenceVille(agenceOrigine != null ? agenceOrigine.getVille() : "-")
                .codeBarres(colis.getCodeBarres())
                .typeLabel(colis.getType() == TypeColis.COURRIER ? "COURRIER" : "COLIS")
                .typeColor(colis.getType() == TypeColis.COURRIER ? "#C9972B" : "#1F3864")
                .numeroBL(bl.getNumeroBL())
                .dateBL(bl.getDateBL() != null ? bl.getDateBL().format(DATE_FMT) : "-")
                .agenceOrigine(agenceOrigine != null ? agenceOrigine.getVille() : "-")
                .agenceDestination(agenceDestination != null ? agenceDestination.getVille() : "-")
                .expediteurNom(colis.getExpediteurNom())
                .expediteurAdresse(colis.getExpediteurAdresse())
                .expediteurTelephone(colis.getExpediteurTelephone())
                .expediteurMatriculeFiscal(colis.getExpediteurMatriculeFiscal())
                .destinataireNom(bl.getDestinataireNom())
                .destinataireAdresse(bl.getDestinataireAdresse())
                .destinataireTelephone(bl.getDestinataireTelephone())
                .designation(colis.getDesignation())
                .quantite(colis.getQuantite())
                .montant(colis.getMontantTTC() != null ? colis.getMontantTTC().toPlainString() : "0,000")
                .ouverturePermise(colis.isOuverturePermise())
                .build();

        byte[] pdf = bordereauPdfService.genererPdf(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=bordereau-" + bl.getNumeroBL() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
