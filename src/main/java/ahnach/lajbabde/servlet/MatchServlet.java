package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.MatchService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/matchs", "/match"})
public class MatchServlet extends HttpServlet {
    private MatchService service;
    @Override public void init() { service = (MatchService) getServletContext().getAttribute("matchService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (service == null) { resp.sendError(503, "Service des matchs indisponible"); return; }
        String id = req.getParameter("id");
        if (id == null) { req.setAttribute("matchs", service.listerMatchs()); req.getRequestDispatcher("/WEB-INF/views/matchs.jsp").forward(req, resp); }
        else try {
            service.trouverMatch(Long.parseLong(id)).ifPresent(match -> req.setAttribute("match", match));
            if (req.getAttribute("match") == null) { resp.sendError(404); return; }
            req.getRequestDispatcher("/WEB-INF/views/detailsMatch.jsp").forward(req, resp);
        } catch (NumberFormatException e) { resp.sendError(400, "Identifiant de match invalide"); }
    }
}
