package ahnach.lajbabde.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import ahnach.lajbabde.model.Match;
import ahnach.lajbabde.service.MatchService;
import jakarta.servlet.http.Part;

public class AdminServlet extends HttpServlet {
    private MatchService matchService;
    @Override public void init() { matchService = (MatchService) getServletContext().getAttribute("matchService"); }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
    {
        String path = req.getPathInfo();
        if (path != null && path.startsWith("/photo/")) { afficherPhoto(req, resp, path); return; }
        String page = path == null || path.equals("/") || path.startsWith("/match") ? "matchs" :
                path.startsWith("/stade") ? "stades" : path.startsWith("/place") ? "places" :
                path.startsWith("/reservation") ? "reservations" : null;
        if (page == null) { resp.sendError(404); return; }
        if ("matchs".equals(page)) {
            if (matchService == null) { resp.sendError(503, "Service des matchs indisponible"); return; }
            req.setAttribute("matchs", matchService.listerMatchs());
            String modifier = req.getParameter("modifier");
            if (modifier != null) {
                try {
                    Match match = matchService.trouverMatch(Long.parseLong(modifier)).orElse(null);
                    if (match == null) { resp.sendError(404); return; }
                    req.setAttribute("matchEnEdition", match);
                } catch (NumberFormatException e) { resp.sendError(400, "Identifiant de match invalide"); return; }
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/" + page + ".jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (matchService == null) { resp.sendError(503, "Service des matchs indisponible"); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        if (path == null || !path.startsWith("/match")) { resp.sendError(501, "Cette opération n'est pas disponible"); return; }
        String contentType = req.getContentType();
        String action = contentType == null || !contentType.toLowerCase(java.util.Locale.ROOT).startsWith("multipart/form-data")
                ? req.getParameter("action") : null;
        if ("supprimer".equals(action)) {
            try {
                matchService.supprimerMatch(Long.parseLong(req.getParameter("id")));
                resp.sendRedirect(req.getContextPath() + "/admin/matchs?resultat=supprime");
            } catch (NumberFormatException e) { resp.sendError(400, "Identifiant de match invalide"); }
            return;
        }

        final String idParam;
        final Long id;
        final LocalDateTime dateHeure;
        final BigDecimal prix;
        final String domicile;
        final String exterieure;
        final String equipeDomicile;
        final String equipeExterieure;
        final String competition;
        final String stade;
        try {
            idParam = req.getParameter("id");
            id = idParam == null || idParam.trim().isEmpty() ? null : Long.valueOf(idParam);
            dateHeure = LocalDateTime.parse(req.getParameter("dateHeure"));
            prix = new BigDecimal(req.getParameter("prix"));
            equipeDomicile = req.getParameter("equipeDomicile");
            equipeExterieure = req.getParameter("equipeExterieure");
            competition = req.getParameter("competition");
            stade = req.getParameter("stade");
            domicile = lirePhoto(req.getPart("photoDomicile"));
            exterieure = lirePhoto(req.getPart("photoExterieure"));
        } catch (IllegalStateException e) {
            getServletContext().log("Tomcat a refusé le formulaire multipart admin (" + req.getContentLengthLong()
                    + " octets): " + e.getMessage(), e);
            resp.sendError(413, "Tomcat a refusé le formulaire multipart. Consultez le journal Tomcat pour connaître la limite exacte.");
            return;
        } catch (IllegalArgumentException | DateTimeParseException e) {
            afficherErreurSaisie(req, resp, e.getMessage());
            return;
        }

        try {
            matchService.enregistrerMatch(id, equipeDomicile, equipeExterieure, competition, stade,
                    dateHeure, prix, domicile, exterieure);
        } catch (IllegalArgumentException e) {
            afficherErreurSaisie(req, resp, e.getMessage());
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/matchs?resultat=" + (id == null ? "ajoute" : "modifie"));
    }

    private void afficherErreurSaisie(HttpServletRequest req, HttpServletResponse resp, String message)
            throws IOException, ServletException {
        req.setAttribute("erreur", "Vérifiez les champs saisis : " + message);
        req.setAttribute("matchs", matchService.listerMatchs());
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try { req.setAttribute("matchEnEdition", matchService.trouverMatch(Long.parseLong(idParam)).orElse(null)); }
            catch (NumberFormatException ignored) { }
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/matchs.jsp").forward(req, resp);
    }

    private String lirePhoto(Part part) throws IOException {
        if (part == null || part.getSize() == 0) return null;
        String type = part.getContentType();
        if (type == null || !(type.equalsIgnoreCase("image/png") || type.equalsIgnoreCase("image/jpeg") || type.equalsIgnoreCase("image/webp"))) {
            throw new IllegalArgumentException("Les images doivent être au format PNG, JPEG ou WebP.");
        }
        byte[] contenu;
        try (InputStream input = part.getInputStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int lus;
            while ((lus = input.read(buffer)) != -1) output.write(buffer, 0, lus);
            contenu = output.toByteArray();
        }
        if (contenu.length < 8 || !signatureImageValide(contenu, type)) throw new IllegalArgumentException("Le fichier envoyé n'est pas une image valide.");
        String extension = type.equalsIgnoreCase("image/png") ? ".png" :
                type.equalsIgnoreCase("image/jpeg") ? ".jpg" : ".webp";
        String nomFichier = UUID.randomUUID().toString() + extension;
        Path dossier = dossierPhotos();
        Files.createDirectories(dossier);
        Files.write(dossier.resolve(nomFichier), contenu);
        return nomFichier;
    }

    private boolean signatureImageValide(byte[] b, String type) {
        if (type.equalsIgnoreCase("image/png")) return (b[0]&255)==137 && b[1]==80 && b[2]==78 && b[3]==71;
        if (type.equalsIgnoreCase("image/jpeg")) return (b[0]&255)==255 && (b[1]&255)==216;
        return b.length >= 12 && b[0]=='R' && b[1]=='I' && b[2]=='F' && b[3]=='F' && b[8]=='W' && b[9]=='E' && b[10]=='B' && b[11]=='P';
    }

    private void afficherPhoto(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Match match = matchService.trouverMatch(id).orElse(null);
            if (match == null) { resp.sendError(404); return; }
            String nomFichier = path.endsWith("domicile") ? match.getPhotoDomicileFichier() :
                    path.endsWith("exterieure") ? match.getPhotoExterieureFichier() : null;
            if (nomFichier == null || !nomFichier.matches("[a-f0-9-]+\\.(png|jpg|webp)")) { resp.sendError(404); return; }
            Path dossier = dossierPhotos();
            Path fichier = dossier.resolve(nomFichier).normalize();
            if (!fichier.startsWith(dossier)) { resp.sendError(404); return; }
            if (!Files.isRegularFile(fichier)) { resp.sendError(404); return; }
            byte[] photo = Files.readAllBytes(fichier);
            resp.setContentType(detecterType(nomFichier));
            resp.setHeader("Cache-Control", "private, max-age=3600");
            resp.getOutputStream().write(photo);
        } catch (NumberFormatException e) { resp.sendError(400); }
    }

    private Path dossierPhotos() {
        String configure = System.getProperty("billetterie.upload.dir");
        if (configure != null && !configure.trim().isEmpty()) return Paths.get(configure).toAbsolutePath().normalize();
        return Paths.get(System.getProperty("user.home", "."), "Billetteriefootball", "uploads").toAbsolutePath().normalize();
    }

    private String detecterType(String nomFichier) {
        if (nomFichier.endsWith(".png")) return "image/png";
        if (nomFichier.endsWith(".jpg")) return "image/jpeg";
        return "image/webp";
    }
}
