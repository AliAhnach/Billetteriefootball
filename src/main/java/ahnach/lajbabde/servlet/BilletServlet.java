package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.BilletService;
import ahnach.lajbabde.model.Billet;
import java.util.List;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/billets")
public class BilletServlet extends HttpServlet {
    private BilletService service;
    @Override public void init() { service = (BilletService) getServletContext().getAttribute("billetService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (service == null) { resp.sendError(503, "Service des billets indisponible"); return; }
        try {
            List<Billet> billets = service.billetsPourMatch(Long.parseLong(req.getParameter("matchId")));
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().print("[");
            for (int i = 0; i < billets.size(); i++) {
                Billet billet = billets.get(i);
                if (i > 0) resp.getWriter().print(",");
                resp.getWriter().printf("{\"id\":%d,\"matchId\":%d,\"categorie\":\"%s\",\"prix\":\"%s\",\"disponible\":%s}",
                        billet.getId(), billet.getMatchId(), billet.getCategorie(), billet.getPrix(), billet.isDisponible());
            }
            resp.getWriter().print("]");
        } catch (NumberFormatException e) { resp.sendError(400, "Identifiant de match invalide"); }
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(501, "Paiement non configuré");
    }
}
