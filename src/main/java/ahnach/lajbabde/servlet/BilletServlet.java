package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.BilletService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/billets")
public class BilletServlet extends HttpServlet {
    private BilletService service;
    @Override public void init() { service = (BilletService) getServletContext().getAttribute("billetService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (service == null) { resp.sendError(503, "Service des billets indisponible"); return; }
        req.setAttribute("billets", service.billetsPourMatch(Long.parseLong(req.getParameter("matchId"))));
        req.getRequestDispatcher("/WEB-INF/views/mesBillets.jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(501, "Paiement non configuré");
    }
}
