package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.UtilisateurService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/inscription"})
public class AuthServlet extends HttpServlet {
    private UtilisateurService service;
    @Override public void init() { service = (UtilisateurService) getServletContext().getAttribute("utilisateurService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String page = req.getServletPath().equals("/login") ? "login" : "inscription";
        req.getRequestDispatcher("/WEB-INF/views/" + page + ".jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (service == null) { resp.sendError(503, "Service utilisateur indisponible"); return; }
        String email = req.getParameter("email");
        if (email == null || email.isBlank()) { req.setAttribute("erreur", "Saisissez votre adresse e-mail."); doGet(req, resp); return; }
        // La vérification du mot de passe nécessite un encodeur et une implémentation DAO configurés.
        req.setAttribute("erreur", "Authentification non configurée."); doGet(req, resp);
    }
}
