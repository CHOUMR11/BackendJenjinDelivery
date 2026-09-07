package com.jenjon.delivery.document;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
public class BarcodeGenerator {

    /**
     * Génère un code-barres Code128 pour le code fourni et le renvoie
     * encodé en Base64 (PNG), prêt à être injecté dans un <img> en data URI.
     */
    public String genererBase64(String code) {
        try {
            BitMatrix matrix = new Code128Writer().encode(code, BarcodeFormat.CODE_128, 400, 120);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "png", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du code-barres pour : " + code, e);
        }
    }
}
