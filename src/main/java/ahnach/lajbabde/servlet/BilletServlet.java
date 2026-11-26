package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.ReservationService;
import ahnach.lajbabde.service.MatchService;
import ahnach.lajbabde.service.UtilisateurService;
import ahnach.lajbabde.service.BilletService;
import ahnach.lajbabde.model.Match;
import ahnach.lajbabde.model.Reservation;
import ahnach.lajbabde.model.Utilisateur;
import ahnach.lajbabde.service.BilletPdfService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Map;

public class BilletServlet extends HttpServlet {
    private ReservationService service;
    private MatchService matchService;
    private UtilisateurService utilisateurService;
    private BilletService billetService;
    @Override public void init() {
        service = (ReservationService) getServletContext().getAttribute("reservationService");
        matchService = (MatchService) getServletContext().getAttribute("matchService");
        utilisateurService = (UtilisateurService) getServletContext().getAttribute("utilisateurService");
        billetService = (BilletService) getServletContext().getAttribute("billetService");
    }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (service == null) { resp.sendError(503, "Service des réservations indisponible"); return; }
        try {
            long clientId = Long.parseLong(String.valueOf(req.getSession().getAttribute("utilisateurId")));
            String billetParam = req.getParameter("billet");
            if (billetParam != null) {
                telechargerBillet(req, resp, clientId, Long.parseLong(billetParam));
                return;
            }
            req.setAttribute("reservations", service.reservationsDuClient(clientId));
            req.getRequestDispatcher("/WEB-INF/views/mesBillets.jsp").forward(req, resp);
        } catch (NumberFormatException e) { resp.sendError(400, "Identifiant invalide"); }
    }

    private void telechargerBillet(HttpServletRequest req, HttpServletResponse resp, long clientId, long reservationId)
            throws IOException {
        if (matchService == null || utilisateurService == null) {
            resp.sendError(503, "Les informations nécessaires au billet sont indisponibles");
            return;
        }
        try {
            Reservation reservation = service.reservationPourClient(clientId, reservationId);
            if (!"VALIDEE".equals(reservation.getStatut())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Le billet PDF est disponible après confirmation de la réservation.");
                return;
            }
            Match match = matchService.trouverMatch(reservation.getMatchId()).orElse(null);
            Utilisateur client = utilisateurService.trouverParId(clientId).orElse(null);
            if (match == null || client == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            BigDecimal prix = match.getPrix();
            if (billetService != null && reservation.getCategorie() != null) {
                Map<String, BigDecimal> tarifs = billetService.tarifsDisponibles(match.getId());
                prix = tarifs.getOrDefault(reservation.getCategorie(), prix);
            }
            byte[] logo = chargerLogoFootTicket();
            byte[] pdf = BilletPdfService.generer(reservation, match, client, prix, logo);
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=billet-reservation-" + reservationId + ".pdf");
            resp.setContentLength(pdf.length);
            resp.getOutputStream().write(pdf);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Réservation introuvable");
        }
    }

    private byte[] chargerLogoFootTicket() throws IOException {
        try (InputStream input = getServletContext().getResourceAsStream("/images/logos/logo-FootTicket.png")) {
            if (input == null) return null;
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int lus;
                while ((lus = input.read(buffer)) != -1) output.write(buffer, 0, lus);
                return output.toByteArray();
            }
        }
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (service == null) { resp.sendError(503, "Service des réservations indisponible"); return; }
        try {
            long clientId = Long.parseLong(String.valueOf(req.getSession().getAttribute("utilisateurId")));
            long reservationId = Long.parseLong(req.getParameter("reservationId"));
            String action = req.getParameter("action");
            if ("annuler".equals(action)) {
                service.annulerPourClient(clientId, reservationId);
                resp.sendRedirect(req.getContextPath() + "/billets?message=annulee");
            } else if ("supprimer".equals(action)) {
                service.supprimerPourClient(clientId, reservationId);
                resp.sendRedirect(req.getContextPath() + "/billets?message=supprimee");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant de réservation invalide");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("erreur", e.getMessage());
            try {
                long clientId = Long.parseLong(String.valueOf(req.getSession().getAttribute("utilisateurId")));
                req.setAttribute("reservations", service.reservationsDuClient(clientId));
            } catch (NumberFormatException ignored) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session client invalide");
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/mesBillets.jsp").forward(req, resp);
        }
    }
}
