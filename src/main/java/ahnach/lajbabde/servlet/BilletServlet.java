package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.ReservationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BilletServlet extends HttpServlet {
    private ReservationService service;
    @Override public void init() { service = (ReservationService) getServletContext().getAttribute("reservationService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (service == null) { resp.sendError(503, "Service des réservations indisponible"); return; }
        try {
            long clientId = Long.parseLong(String.valueOf(req.getSession().getAttribute("utilisateurId")));
            req.setAttribute("reservations", service.reservationsDuClient(clientId));
            req.getRequestDispatcher("/WEB-INF/views/mesBillets.jsp").forward(req, resp);
        } catch (NumberFormatException e) { resp.sendError(400, "Session client invalide"); }
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
