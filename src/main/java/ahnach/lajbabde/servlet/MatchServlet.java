package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.MatchService;
import ahnach.lajbabde.model.Match;
import java.util.Optional;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/matchs", "/match"})
public class MatchServlet extends HttpServlet {
    private MatchService service;
    @Override public void init() { service = (MatchService) getServletContext().getAttribute("matchService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (service == null) { resp.sendError(503, "Service des matchs indisponible"); return; }
        String id = req.getParameter("id");
        if (id == null) { resp.sendError(501, "La représentation des matchs doit être fournie par un client frontend"); return; }
        try {
            Optional<Match> match = service.trouverMatch(Long.parseLong(id));
            if (match.isEmpty()) { resp.sendError(404); return; }
            resp.setContentType("application/json;charset=UTF-8");
            Match m = match.get();
            resp.getWriter().printf("{\"id\":%d,\"equipeDomicile\":\"%s\",\"equipeExterieure\":\"%s\",\"competition\":\"%s\",\"stade\":\"%s\",\"dateHeure\":\"%s\"}",
                    m.getId(), json(m.getEquipeDomicile()), json(m.getEquipeExterieure()), json(m.getCompetition()), json(m.getStade()), m.getDateHeure());
        } catch (NumberFormatException e) { resp.sendError(400, "Identifiant de match invalide"); }
    }

    private String json(String value) { return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\""); }
}
