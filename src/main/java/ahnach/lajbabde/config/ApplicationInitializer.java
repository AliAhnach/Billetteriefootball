package ahnach.lajbabde.config;

import ahnach.lajbabde.dao.hibernate.HibernateBilletDao;
import ahnach.lajbabde.dao.hibernate.HibernateMatchDao;
import ahnach.lajbabde.dao.hibernate.HibernatePlaceDao;
import ahnach.lajbabde.dao.hibernate.HibernateReservationDao;
import ahnach.lajbabde.dao.hibernate.HibernateStadeDao;
import ahnach.lajbabde.dao.hibernate.HibernateUtilisateurDao;
import ahnach.lajbabde.service.BilletService;
import ahnach.lajbabde.service.MatchService;
import ahnach.lajbabde.service.ReservationService;
import ahnach.lajbabde.service.UtilisateurService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/** Construit les DAO/services au démarrage et ferme Hibernate à l'arrêt. */
public class ApplicationInitializer implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        try {
            HibernateUtil.getSessionFactory();
            HibernateMatchDao matchDao = new HibernateMatchDao();
            HibernateBilletDao billetDao = new HibernateBilletDao();
            HibernateUtilisateurDao utilisateurDao = new HibernateUtilisateurDao();
            HibernatePlaceDao placeDao = new HibernatePlaceDao();
            HibernateReservationDao reservationDao = new HibernateReservationDao();
            event.getServletContext().setAttribute("matchService", new MatchService(matchDao));
            event.getServletContext().setAttribute("billetService", new BilletService(billetDao));
            event.getServletContext().setAttribute("utilisateurService", new UtilisateurService(utilisateurDao));
            event.getServletContext().setAttribute("reservationService", new ReservationService(reservationDao, placeDao));
            event.getServletContext().setAttribute("stadeDao", new HibernateStadeDao());
        } catch (RuntimeException e) {
            event.getServletContext().log("Échec de connexion à la base BilletrieDB", e);
            // Laisser le site déployer ses vues; les endpoints de données signaleront 503.
        }
    }

    @Override public void contextDestroyed(ServletContextEvent event) { HibernateUtil.shutdown(); }
}
