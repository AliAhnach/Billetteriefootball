package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.ReservationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;

public class ReservationServlet extends HttpServlet {
    private ReservationService service;
    @Override public void init() { service = (ReservationService) getServletContext().getAttribute("reservationService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.getRequestDispatcher("/WEB-INF/views/reservation.jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (service == null) { resp.sendError(503, "Service de réservation indisponible"); return; }
        try {
            long clientId = Long.parseLong(String.valueOf(req.getSession().getAttribute("utilisateurId")));
            long matchId = Long.parseLong(req.getParameter("matchId"));
            String categorie = req.getParameter("categorie");
            int nombrePlaces = Integer.parseInt(req.getParameter("nombrePlaces"));
            service.reserver(clientId, matchId, categorie, nombrePlaces);
            resp.sendRedirect(req.getContextPath() + "/billets");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("erreur", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/reservation.jsp").forward(req, resp);
        }
    }
}
