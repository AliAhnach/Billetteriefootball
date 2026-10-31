package ahnach.lajbabde;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
// Annotation pour lier l'URL "/hello" à cette servlet
@WebServlet("/hello")
public class Main extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().write("<h1>Hello Servlet!</h1>");
    }
}