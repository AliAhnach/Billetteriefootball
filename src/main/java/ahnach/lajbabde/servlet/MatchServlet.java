package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.MatchService;
import ahnach.lajbabde.model.Match;
import jakarta.servlet.ServletException;
import java.util.Optional;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MatchServlet extends HttpServlet {
    private MatchService service;
    @Override public void init() { service = (MatchService) getServletContext().getAttribute("matchService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (service == null) { resp.sendError(503, "Service des matchs indisponible"); return; }
        if ("/photoMatch".equals(req.getServletPath())) { afficherPhoto(req, resp); return; }
        String id = req.getParameter("id");
        if (id == null) {
            req.setAttribute("matchs", service.listerMatchs());
            req.getRequestDispatcher("/WEB-INF/views/matchs.jsp").forward(req, resp);
            return;
        }
        try {
            Optional<Match> match = service.trouverMatch(Long.parseLong(id));
            if (!match.isPresent()) { resp.sendError(404); return; }
            req.setAttribute("match", match.get());
            req.getRequestDispatcher("/WEB-INF/views/detailsMatch.jsp").forward(req, resp);
        } catch (NumberFormatException e) { resp.sendError(400, "Identifiant de match invalide"); }
    }

    private void afficherPhoto(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Match match = service.trouverMatch(id).orElse(null);
            if (match == null) { resp.sendError(404); return; }
            String equipe = req.getParameter("equipe");
            String nomFichier = "domicile".equals(equipe) ? match.getPhotoDomicileFichier() :
                    "exterieure".equals(equipe) ? match.getPhotoExterieureFichier() : null;
            if (nomFichier == null || !nomFichier.matches("[a-f0-9-]+\\.(png|jpg|webp)")) { resp.sendError(404); return; }
            Path dossier = dossierPhotos();
            Path fichier = dossier.resolve(nomFichier).normalize();
            if (!fichier.startsWith(dossier) || !Files.isRegularFile(fichier)) { resp.sendError(404); return; }
            resp.setContentType(nomFichier.endsWith(".png") ? "image/png" :
                    nomFichier.endsWith(".jpg") ? "image/jpeg" : "image/webp");
            resp.setHeader("Cache-Control", "public, max-age=3600");
            Files.copy(fichier, resp.getOutputStream());
        } catch (NumberFormatException e) { resp.sendError(400, "Identifiant de match invalide"); }
    }

    private Path dossierPhotos() {
        String configure = System.getProperty("billetterie.upload.dir");
        if (configure != null && !configure.trim().isEmpty()) return Paths.get(configure).toAbsolutePath().normalize();
        return Paths.get(System.getProperty("user.home", "."), "Billetteriefootball", "uploads").toAbsolutePath().normalize();
    }

}
