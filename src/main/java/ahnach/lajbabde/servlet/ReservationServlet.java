package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.ReservationService;
import ahnach.lajbabde.service.BilletService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class ReservationServlet extends HttpServlet {
    private ReservationService service;
    private BilletService billetService;
    @Override public void init() {
        service = (ReservationService) getServletContext().getAttribute("reservationService");
        billetService = (BilletService) getServletContext().getAttribute("billetService");
    }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            long matchId = Long.parseLong(req.getParameter("matchId"));
            chargerTarifs(req, matchId);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant de match invalide");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/reservation.jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (service == null) { resp.sendError(503, "Service de réservation indisponible"); return; }
        try {
            long clientId = Long.parseLong(String.valueOf(req.getSession().getAttribute("utilisateurId")));
            long matchId = Long.parseLong(req.getParameter("matchId"));
            String categorie = req.getParameter("categorie");
            int nombrePlaces = Integer.parseInt(req.getParameter("nombrePlaces"));
            if (!categorieValide(categorie)) {
                throw new IllegalArgumentException("Veuillez choisir une catégorie de tribune valide");
            }
            service.reserver(clientId, matchId, categorie, nombrePlaces);
            resp.sendRedirect(req.getContextPath() + "/billets");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("erreur", e.getMessage());
            try {
                long matchId = Long.parseLong(req.getParameter("matchId"));
                chargerTarifs(req, matchId);
            } catch (NumberFormatException ignored) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant de match invalide");
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/reservation.jsp").forward(req, resp);
        }
    }

    private void chargerTarifs(HttpServletRequest req, long matchId) {
        req.setAttribute("tarifs", tarifsDisponibles(matchId));
    }

    private Map<String, BigDecimal> tarifsDisponibles(long matchId) {
        if (billetService == null) return java.util.Collections.emptyMap();
        return billetService.tarifsDisponibles(matchId);
    }

    private boolean categorieValide(String categorie) {
        return "Tribune centrale".equals(categorie)
                || "Tribune latérale".equals(categorie)
                || "Tribune populaire".equals(categorie);
    }
}
