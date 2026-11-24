package ahnach.lajbabde.config;

import ahnach.lajbabde.dao.hibernate.HibernateBilletDao;
import ahnach.lajbabde.dao.hibernate.HibernateMatchDao;
import ahnach.lajbabde.dao.hibernate.HibernatePlaceDao;
import ahnach.lajbabde.dao.hibernate.HibernateReservationDao;
import ahnach.lajbabde.dao.hibernate.HibernateStadeDao;
import ahnach.lajbabde.dao.hibernate.HibernateUtilisateurDao;
import ahnach.lajbabde.model.Billet;
import ahnach.lajbabde.model.Match;
import ahnach.lajbabde.model.Place;
import ahnach.lajbabde.model.Stade;
import ahnach.lajbabde.service.BilletService;
import ahnach.lajbabde.service.MatchService;
import ahnach.lajbabde.service.ReservationService;
import ahnach.lajbabde.service.UtilisateurService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.Session;
import org.hibernate.Transaction;

/** Construit les DAO/services au démarrage et ferme Hibernate à l'arrêt. */
public class ApplicationInitializer implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        try {
            HibernateUtil.getSessionFactory();
            ajouterDonneesDemo();
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

    private void ajouterDonneesDemo() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Long dejaPresent = session.createQuery(
                    "select count(m) from Match m where m.equipeDomicile = :domicile and m.equipeExterieure = :exterieur", Long.class)
                    .setParameter("domicile", "Raja Casablanca")
                    .setParameter("exterieur", "Wydad Casablanca")
                    .getSingleResult();
            if (dejaPresent.longValue() == 0L) {
                Stade stade = new Stade(null, "Stade Mohammed V", "Casablanca", 45000);
                session.persist(stade);
                session.flush();

                Match match = new Match(null, "Raja Casablanca", "Wydad Casablanca", "Botola Pro",
                        "Stade Mohammed V, Casablanca", LocalDateTime.of(2026, 12, 20, 20, 0));
                session.persist(match);
                session.flush();

                session.persist(new Place(null, stade.getId(), "Tribune centrale", "A", "1", true));
                session.persist(new Billet(null, match.getId(), "Tribune centrale", new BigDecimal("250.00"), true));
            }
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    @Override public void contextDestroyed(ServletContextEvent event) { HibernateUtil.shutdown(); }
}
