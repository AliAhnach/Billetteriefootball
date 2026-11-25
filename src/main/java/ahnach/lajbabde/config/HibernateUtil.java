package ahnach.lajbabde.config;

import ahnach.lajbabde.model.Administrateur;
import ahnach.lajbabde.model.Billet;
import ahnach.lajbabde.model.Client;
import ahnach.lajbabde.model.Match;
import ahnach.lajbabde.model.Place;
import ahnach.lajbabde.model.Reservation;
import ahnach.lajbabde.model.Stade;
import ahnach.lajbabde.model.Utilisateur;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Fabrique Hibernate partagée par l'application. */
public final class HibernateUtil {
    private static SessionFactory sessionFactory;

    private HibernateUtil() { }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Properties properties = new Properties();
            try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) properties.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Impossible de lire application.properties", e);
            }
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.driver_class", setting("DB_DRIVER", "db.driver", "com.mysql.cj.jdbc.Driver", properties));
            configuration.setProperty("hibernate.connection.url", setting("DB_URL", "db.url", "jdbc:mysql://localhost:3306/gestion_projet?serverTimezone=UTC", properties));
            configuration.setProperty("hibernate.connection.username", setting("DB_USER", "db.user", "billetterie_app", properties));
            configuration.setProperty("hibernate.connection.password", setting("DB_PASSWORD", "db.password", "", properties));
            configuration.setProperty("hibernate.dialect", setting("HIBERNATE_DIALECT", "hibernate.dialect", "org.hibernate.dialect.MySQLDialect", properties));
            configuration.setProperty("hibernate.hbm2ddl.auto", setting("HIBERNATE_DDL_AUTO", "hibernate.hbm2ddl.auto", "validate", properties));
            configuration.setProperty("hibernate.show_sql", setting("HIBERNATE_SHOW_SQL", "hibernate.show_sql", "false", properties));
            configuration.setProperty("hibernate.format_sql", "true");
            configuration.addAnnotatedClass(Utilisateur.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Administrateur.class);
            configuration.addAnnotatedClass(Match.class);
            configuration.addAnnotatedClass(Stade.class);
            configuration.addAnnotatedClass(Place.class);
            configuration.addAnnotatedClass(Reservation.class);
            configuration.addAnnotatedClass(Billet.class);
            sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory;
    }

    private static String setting(String envName, String propertyName, String fallback, Properties properties) {
        String value = System.getenv(envName);
        // The Docker Compose .env uses MYSQL_PASSWORD; accept it for Tomcat too.
        if ((value == null || value.trim().isEmpty()) && "DB_PASSWORD".equals(envName)) {
            value = System.getenv("MYSQL_PASSWORD");
        }
        if (value == null || value.trim().isEmpty()) value = properties.getProperty(propertyName, fallback);
        return value;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
