package ahnach.lajbabde.servlet;

import ahnach.lajbabde.model.Utilisateur;
import ahnach.lajbabde.service.UtilisateurService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ProfilServlet extends HttpServlet {
    private UtilisateurService service;

    @Override public void init() {
        service = (UtilisateurService) getServletContext().getAttribute("utilisateurService");
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        Utilisateur utilisateur = utilisateurConnecte(req, resp);
        if (utilisateur == null) return;
        if ("/photo".equals(req.getPathInfo())) {
            envoyerPhoto(utilisateur, resp);
            return;
        }
        req.setAttribute("profil", utilisateur);
        req.getRequestDispatcher("/WEB-INF/views/profil.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        Utilisateur utilisateur = utilisateurConnecte(req, resp);
        if (utilisateur == null) return;
        String nouveauFichier = null;
        try {
            String nouveauMotDePasse = req.getParameter("nouveauMotDePasse");
            String confirmation = req.getParameter("confirmationMotDePasse");
            if (nouveauMotDePasse != null && !nouveauMotDePasse.isEmpty()
                    && !nouveauMotDePasse.equals(confirmation)) {
                throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas.");
            }
            Part photo = req.getPart("photoProfil");
            if (photo != null && photo.getSize() > 0) nouveauFichier = enregistrerPhoto(photo);
            Utilisateur maj = service.modifierProfil(utilisateur.getId(), req.getParameter("nom"),
                    req.getParameter("email"), nouveauMotDePasse, nouveauFichier);
            req.getSession().setAttribute("nom", maj.getNom());
            req.getSession().setAttribute("email", maj.getEmail());
            req.getSession().setAttribute("photoProfilFichier", maj.getPhotoProfilFichier());
            if (nouveauFichier != null && utilisateur.getPhotoProfilFichier() != null
                    && !utilisateur.getPhotoProfilFichier().equals(nouveauFichier)) {
                supprimerPhoto(utilisateur.getPhotoProfilFichier());
            }
            resp.sendRedirect(req.getContextPath() + "/profil?message=modifie");
        } catch (IllegalArgumentException | IllegalStateException e) {
            supprimerPhoto(nouveauFichier);
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("profil", service.trouverParId(utilisateur.getId()).orElse(utilisateur));
            req.getRequestDispatcher("/WEB-INF/views/profil.jsp").forward(req, resp);
        } catch (RuntimeException e) {
            supprimerPhoto(nouveauFichier);
            getServletContext().log("Erreur pendant la mise à jour du profil", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible de mettre à jour le profil.");
        }
    }

    private Utilisateur utilisateurConnecte(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (service == null) { resp.sendError(503, "Service utilisateur indisponible"); return null; }
        Object idSession = req.getSession(false) == null ? null : req.getSession(false).getAttribute("utilisateurId");
        if (!(idSession instanceof Number)) { resp.sendRedirect(req.getContextPath() + "/login"); return null; }
        Utilisateur utilisateur = service.trouverParId(((Number) idSession).longValue()).orElse(null);
        if (utilisateur == null || !"CLIENT".equals(utilisateur.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return utilisateur;
    }

    private String enregistrerPhoto(Part part) throws IOException {
        if (part.getSize() > 3 * 1024 * 1024) throw new IllegalArgumentException("La photo ne peut pas dépasser 3 Mo.");
        String type = part.getContentType();
        if (type == null || !(type.equalsIgnoreCase("image/png") || type.equalsIgnoreCase("image/jpeg")
                || type.equalsIgnoreCase("image/webp"))) {
            throw new IllegalArgumentException("Choisissez une image PNG, JPEG ou WebP.");
        }
        byte[] contenu;
        try (InputStream input = part.getInputStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int lus;
            while ((lus = input.read(buffer)) != -1) output.write(buffer, 0, lus);
            contenu = output.toByteArray();
        }
        if (!signatureValide(contenu, type)) throw new IllegalArgumentException("Le fichier envoyé n'est pas une image valide.");
        String extension = type.equalsIgnoreCase("image/png") ? ".png" :
                type.equalsIgnoreCase("image/jpeg") ? ".jpg" : ".webp";
        String nom = UUID.randomUUID().toString() + extension;
        Path dossier = dossierPhotos();
        Files.createDirectories(dossier);
        Files.write(dossier.resolve(nom), contenu);
        return nom;
    }

    private boolean signatureValide(byte[] b, String type) {
        if (type.equalsIgnoreCase("image/png")) return b.length >= 8 && (b[0] & 255) == 137 && b[1] == 80 && b[2] == 78 && b[3] == 71;
        if (type.equalsIgnoreCase("image/jpeg")) return b.length >= 3 && (b[0] & 255) == 255 && (b[1] & 255) == 216;
        return b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
    }

    private void envoyerPhoto(Utilisateur utilisateur, HttpServletResponse resp) throws IOException {
        String nom = utilisateur.getPhotoProfilFichier();
        if (nom == null || !nom.matches("[a-f0-9-]+\\.(png|jpg|webp)")) { resp.sendError(404); return; }
        Path dossier = dossierPhotos();
        Path fichier = dossier.resolve(nom).normalize();
        if (!fichier.startsWith(dossier) || !Files.isRegularFile(fichier)) { resp.sendError(404); return; }
        resp.setContentType(nom.endsWith(".png") ? "image/png" : nom.endsWith(".jpg") ? "image/jpeg" : "image/webp");
        resp.setHeader("Cache-Control", "private, no-store");
        Files.copy(fichier, resp.getOutputStream());
    }

    private void supprimerPhoto(String nom) {
        if (nom == null || !nom.matches("[a-f0-9-]+\\.(png|jpg|webp)")) return;
        try { Files.deleteIfExists(dossierPhotos().resolve(nom).normalize()); }
        catch (IOException e) { getServletContext().log("Impossible de supprimer la photo de profil temporaire", e); }
    }

    private Path dossierPhotos() {
        String configure = System.getProperty("billetterie.upload.dir");
        Path racine = configure != null && !configure.trim().isEmpty()
                ? Paths.get(configure).toAbsolutePath().normalize()
                : Paths.get(System.getProperty("user.home", "."), "Billetteriefootball", "uploads").toAbsolutePath().normalize();
        return racine.resolve("profils").normalize();
    }
}
