package ahnach.lajbabde.dao.hibernate;

import ahnach.lajbabde.config.HibernateUtil;
import ahnach.lajbabde.dao.UtilisateurDao;
import ahnach.lajbabde.model.Utilisateur;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateUtilisateurDao implements UtilisateurDao {
    public Optional<Utilisateur> findByEmail(String email) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { return s.createQuery("from Utilisateur where email = :email", Utilisateur.class).setParameter("email", email).uniqueResultOptional(); }
    }
    public Optional<Utilisateur> findById(long id) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return Optional.ofNullable(s.get(Utilisateur.class, id)); } }
    public Utilisateur save(Utilisateur v) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { tx = s.beginTransaction(); if (v.getId() == null) s.persist(v); else v = (Utilisateur) s.merge(v); tx.commit(); return v; }
        catch (RuntimeException e) { if (tx != null && tx.isActive()) tx.rollback(); throw e; }
    }
}
