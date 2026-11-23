package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.BilletService;
import ahnach.lajbabde.model.Billet;
import jakarta.servlet.ServletException;
import java.util.List;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BilletServlet extends HttpServlet {
    private BilletService service;
    @Override public void init() { service = (BilletService) getServletContext().getAttribute("billetService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (service == null) { resp.sendError(503, "Service des billets indisponible"); return; }
        try {
            List<Billet> billets = service.billetsPourMatch(Long.parseLong(req.getParameter("matchId")));
            req.setAttribute("billets", billets);
            req.getRequestDispatcher("/WEB-INF/views/mesBillets.jsp").forward(req, resp);
        } catch (NumberFormatException e) { resp.sendError(400, "Identifiant de match invalide"); }
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(501, "Paiement non configuré");
    }
}
