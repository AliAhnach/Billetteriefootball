package ahnach.lajbabde.servlet;

import ahnach.lajbabde.service.UtilisateurService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthServlet extends HttpServlet {

    private UtilisateurService service;
    @Override public void init()
    {
        service = (UtilisateurService) getServletContext().getAttribute("utilisateurService");
    }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
    {
        String page = req.getServletPath().equals("/login") ? "login" : "inscription";
        req.getRequestDispatcher("/WEB-INF/views/" + page + ".jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
    {
        req.setCharacterEncoding("UTF-8");
        if (service == null) {
            resp.sendError(503, "Service utilisateur indisponible");
            return;
        }
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");
        try {
            if ("/inscription".equals(req.getServletPath())) {
                String nom = req.getParameter("nom");
                ahnach.lajbabde.model.Utilisateur client = service.inscrireClient(nom, email, motDePasse);
                connecter(req, client);
                resp.sendRedirect(req.getContextPath() + "/matchs");
                return;
            }

            java.util.Optional<ahnach.lajbabde.model.Utilisateur> compte = service.authentifier(email, motDePasse);
            if (!compte.isPresent()) {
                req.setAttribute("erreur", "Adresse e-mail ou mot de passe incorrect.");
                doGet(req, resp);
                return;
            }
            connecter(req, compte.get());
            String destination = "ADMIN".equals(compte.get().getRole()) ? "/admin" : "/matchs";
            resp.sendRedirect(req.getContextPath() + destination);
        } catch (IllegalArgumentException e) {
            req.setAttribute("erreur", e.getMessage());
            doGet(req, resp);
        } catch (RuntimeException e) {
            getServletContext().log("Erreur pendant l'authentification", e);
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Impossible d'accéder aux comptes. Vérifiez la connexion à la base de données.");
        }
    }

    private void connecter(HttpServletRequest req, ahnach.lajbabde.model.Utilisateur compte) {
        if (req.getSession(false) != null) req.changeSessionId();
        jakarta.servlet.http.HttpSession session = req.getSession(true);
        session.setAttribute("utilisateurId", compte.getId());
        session.setAttribute("nom", compte.getNom());
        session.setAttribute("email", compte.getEmail());
        session.setAttribute("role", compte.getRole());
    }
}
