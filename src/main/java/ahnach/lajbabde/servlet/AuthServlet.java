package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.UtilisateurService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/inscription"})
public class AuthServlet extends HttpServlet {
    private UtilisateurService service;
    @Override public void init() { service = (UtilisateurService) getServletContext().getAttribute("utilisateurService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(501, "L'authentification doit être appelée par un client frontend");
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (service == null) { resp.sendError(503, "Service utilisateur indisponible"); return; }
        String email = req.getParameter("email");
        if (email == null || email.isBlank()) { resp.sendError(400, "Adresse e-mail requise"); return; }
        // La vérification du mot de passe nécessite un encodeur et une implémentation DAO configurés.
        resp.sendError(501, "Authentification non configurée");
    }
}
