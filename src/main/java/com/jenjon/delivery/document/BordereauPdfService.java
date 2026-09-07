package com.jenjon.delivery.document;

import lombok.RequiredArgsConstructor;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Génère le PDF du bordereau d'envoi / bon de livraison à partir du template HTML
 * (src/main/resources/templates/bordereau-template.html) et des données du colis.
 *
 * NB: pour ce MVP, le rendu HTML->PDF utilise openhtmltopdf. Le template est
 * volontairement en un seul fichier HTML/CSS autonome (pas de dépendance JS)
 * car openhtmltopdf ne supporte que du HTML/CSS statique.
 */
@Service
@RequiredArgsConstructor
public class BordereauPdfService {

    private final BarcodeGenerator barcodeGenerator;

    public byte[] genererPdf(BordereauData data) {
        String html = chargerTemplate();
        String barcodeBase64 = barcodeGenerator.genererBase64(data.getCodeBarres());

        html = html
                .replace("{{AGENCE_CODE}}", nvl(data.getAgenceCode()))
                .replace("{{AGENCE_VILLE}}", nvl(data.getAgenceVille()))
                .replace("{{BARCODE_BASE64}}", barcodeBase64)
                .replace("{{CODE_BARRES}}", nvl(data.getCodeBarres()))
                .replace("{{TYPE_LABEL}}", nvl(data.getTypeLabel()))
                .replace("{{TYPE_COLOR}}", nvl(data.getTypeColor(), "#1F3864"))
                .replace("{{NUMERO_BL}}", nvl(data.getNumeroBL()))
                .replace("{{DATE_BL}}", nvl(data.getDateBL()))
                .replace("{{AGENCE_ORIGINE}}", nvl(data.getAgenceOrigine()))
                .replace("{{AGENCE_DESTINATION}}", nvl(data.getAgenceDestination()))
                .replace("{{EXP_NOM}}", nvl(data.getExpediteurNom()))
                .replace("{{EXP_ADRESSE}}", nvl(data.getExpediteurAdresse()))
                .replace("{{EXP_TEL}}", nvl(data.getExpediteurTelephone()))
                .replace("{{EXP_MF}}", nvl(data.getExpediteurMatriculeFiscal()))
                .replace("{{DEST_NOM}}", nvl(data.getDestinataireNom()))
                .replace("{{DEST_ADRESSE}}", nvl(data.getDestinataireAdresse()))
                .replace("{{DEST_TEL}}", nvl(data.getDestinataireTelephone()))
                .replace("{{DESIGNATION}}", nvl(data.getDesignation()))
                .replace("{{QUANTITE}}", String.valueOf(data.getQuantite() != null ? data.getQuantite() : 1))
                .replace("{{MONTANT}}", nvl(data.getMontant()))
                .replace("{{OUVERTURE_OUI}}", data.isOuverturePermise() ? "☑" : "☐")
                .replace("{{OUVERTURE_NON}}", !data.isOuverturePermise() ? "☑" : "☐");

        return renderHtmlToPdf(html);
    }

    private byte[] renderHtmlToPdf(String html) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF du bordereau", e);
        }
    }

    private String chargerTemplate() {
        // getInputStream() (et non getFile()) : fonctionne aussi bien en dev
        // (classes déballées) qu'en production (app packagée en .jar).
        try (java.io.InputStream is = new ClassPathResource("templates/bordereau-template.html").getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger le template du bordereau", e);
        }
    }

    private String nvl(String value) {
        return nvl(value, "");
    }

    private String nvl(String value, String fallback) {
        return value != null ? value : fallback;
    }
}
