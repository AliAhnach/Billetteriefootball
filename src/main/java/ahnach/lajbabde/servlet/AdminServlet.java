package ahnach.lajbabde.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/admin", "/admin/*"})
public class AdminServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String page = path == null || path.equals("/") || path.startsWith("/match") ? "matchs" :
                path.startsWith("/stade") ? "stades" : path.startsWith("/place") ? "places" :
                path.startsWith("/reservation") ? "reservations" : null;
        if (page == null) { resp.sendError(404); return; }
        req.getRequestDispatcher("/WEB-INF/views/admin/" + page + ".jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(501, "La persistance d'administration n'est pas configurée");
    }
}
