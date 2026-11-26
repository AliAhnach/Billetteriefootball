package ahnach.lajbabde.service;

import ahnach.lajbabde.model.Match;
import ahnach.lajbabde.model.Reservation;
import ahnach.lajbabde.model.Utilisateur;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/** Produit un billet PDF imprimable pour une reservation confirmee. */
public final class BilletPdfService {
    private static final Color VERT = new Color(16, 37, 28);
    private static final Color VERT_CLAIR = new Color(8, 127, 91);
    private static final Color TEXTE = new Color(35, 48, 41);
    private static final Color GRIS = new Color(105, 117, 110);
    private static final DateTimeFormatter DATE_HEURE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private BilletPdfService() { }

    public static byte[] generer(Reservation reservation, Match match, Utilisateur client,
                                 BigDecimal prixUnitaire, byte[] logoFootTicket) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDType1Font normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font gras = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            try (PDPageContentStream contenu = new PDPageContentStream(document, page)) {
                float largeur = page.getMediaBox().getWidth();
                rectangle(contenu, 0, 742, largeur, 100, VERT);
                if (logoFootTicket != null && logoFootTicket.length > 0) {
                    rectangle(contenu, 42, 762, 60, 60, Color.WHITE);
                    PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoFootTicket, "logo-FootTicket");
                    contenu.drawImage(logo, 48, 768, 48, 48);
                }
                texte(contenu, gras, 25, 116, 792, "FootTicket", Color.WHITE);
                texte(contenu, normal, 12, 116, 768, "Votre billet de match", new Color(218, 239, 225));

                rectangle(contenu, 42, 144, largeur - 84, 566, Color.WHITE);
                contenu.setStrokingColor(new Color(220, 231, 224));
                contenu.setLineWidth(1);
                contenu.addRect(42, 144, largeur - 84, 566);
                contenu.stroke();

                texte(contenu, gras, 16, 64, 676, "BILLET CONFIRME", VERT_CLAIR);
                texte(contenu, gras, 12, 405, 678, "REFERENCE  #" + reservation.getId(), VERT);
                contenu.setStrokingColor(new Color(220, 231, 224));
                contenu.moveTo(64, 655);
                contenu.lineTo(531, 655);
                contenu.stroke();

                float y = 626;
                ligne(contenu, normal, gras, "Client", client.getNom(), y); y -= 34;
                ligne(contenu, normal, gras, "E-mail", client.getEmail(), y); y -= 34;
                ligne(contenu, normal, gras, "Identifiant du match", String.valueOf(match.getId()), y); y -= 34;
                ligne(contenu, normal, gras, "Match", match.getEquipeDomicile() + " - " + match.getEquipeExterieure(), y); y -= 34;
                ligne(contenu, normal, gras, "Competition", match.getCompetition(), y); y -= 34;
                ligne(contenu, normal, gras, "Stade", match.getStade(), y); y -= 34;
                ligne(contenu, normal, gras, "Date du match", match.getDateHeure() == null ? "A confirmer" : DATE_HEURE.format(match.getDateHeure()), y); y -= 34;
                ligne(contenu, normal, gras, "Prix du match", match.getPrix() == null ? "A confirmer" : match.getPrix().toPlainString() + " DH", y); y -= 34;
                ligne(contenu, normal, gras, "Tribune", reservation.getCategorie() == null ? "A attribuer" : reservation.getCategorie(), y); y -= 34;
                ligne(contenu, normal, gras, "Nombre de places", String.valueOf(reservation.getNombrePlaces()), y); y -= 34;
                ligne(contenu, normal, gras, "Reservation faite le", String.valueOf(reservation.getDateReservation()), y); y -= 34;
                ligne(contenu, normal, gras, "Prix par place", prixUnitaire == null ? "A confirmer" : prixUnitaire.toPlainString() + " DH", y); y -= 34;
                BigDecimal total = prixUnitaire == null || reservation.getNombrePlaces() == null
                        ? null : prixUnitaire.multiply(BigDecimal.valueOf(reservation.getNombrePlaces()));
                ligne(contenu, normal, gras, "Total", total == null ? "A confirmer" : total.toPlainString() + " DH", y);

                rectangle(contenu, 64, 174, largeur - 128, 36, new Color(235, 247, 239));
                texte(contenu, normal, 10, 77, 187, "Presentez ce billet a l'entree du stade.", VERT_CLAIR);
            }
            document.save(sortie);
            return sortie.toByteArray();
        }
    }

    private static void ligne(PDPageContentStream contenu, PDType1Font normal, PDType1Font gras,
                              String etiquette, String valeur, float y) throws IOException {
        texte(contenu, normal, 10, 64, y, etiquette.toUpperCase(java.util.Locale.ROOT), GRIS);
        texte(contenu, gras, 12, 220, y, raccourcir(valeur), TEXTE);
    }

    private static void texte(PDPageContentStream contenu, PDType1Font police, float taille,
                              float x, float y, String valeur, Color couleur) throws IOException {
        contenu.beginText();
        contenu.setFont(police, taille);
        contenu.setNonStrokingColor(couleur);
        contenu.newLineAtOffset(x, y);
        contenu.showText(texteCompatible(valeur));
        contenu.endText();
    }

    private static void rectangle(PDPageContentStream contenu, float x, float y, float largeur,
                                  float hauteur, Color couleur) throws IOException {
        contenu.setNonStrokingColor(couleur);
        contenu.addRect(x, y, largeur, hauteur);
        contenu.fill();
    }

    private static String raccourcir(String valeur) {
        String texte = texteCompatible(valeur == null ? "" : valeur);
        return texte.length() > 52 ? texte.substring(0, 49) + "..." : texte;
    }

    private static String texteCompatible(String valeur) {
        String sansMarques = Normalizer.normalize(valeur, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
        StringBuilder resultat = new StringBuilder();
        for (int i = 0; i < sansMarques.length(); i++) {
            char c = sansMarques.charAt(i);
            if (c == '\u2019' || c == '\u2018') resultat.append('\'');
            else if (c == '\u2013' || c == '\u2014') resultat.append('-');
            else if (c >= 32 && c <= 255) resultat.append(c);
            else if (Character.isWhitespace(c)) resultat.append(' ');
            else resultat.append('?');
        }
        return resultat.toString();
    }
}
