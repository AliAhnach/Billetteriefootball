package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.ReservationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ReservationServlet extends HttpServlet {
    private ReservationService service;
    @Override public void init() { service = (ReservationService) getServletContext().getAttribute("reservationService"); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (service == null) { resp.sendError(503, "Service de réservation indisponible"); return; }
        try {
            long clientId = Long.parseLong(String.valueOf(req.getSession().getAttribute("utilisateurId")));
            long matchId = Long.parseLong(req.getParameter("matchId"));
            long placeId = Long.parseLong(req.getParameter("placeId"));
            service.reserver(clientId, matchId, placeId);
            resp.sendRedirect(req.getContextPath() + "/billets");
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.sendError(400, e.getMessage());
        }
    }
}
