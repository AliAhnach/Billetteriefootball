package ahnach.lajbabde.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/admin", "/admin/*"})
public class AdminServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(501, "L'interface d'administration doit être fournie par un client frontend");
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(501, "La persistance d'administration n'est pas configurée");
    }
}
