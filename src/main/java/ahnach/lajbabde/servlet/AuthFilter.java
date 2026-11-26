package ahnach.lajbabde.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/reserver", "/billets", "/profil/*", "/admin/*"})
public class AuthFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        Object role = session == null ? null : session.getAttribute("role");
        String path = req.getServletPath();
        if (role == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        if (path.startsWith("/admin") && !"ADMIN".equals(role)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
        if (path.startsWith("/profil") && !"CLIENT".equals(role)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
        chain.doFilter(request, response);
    }
}
