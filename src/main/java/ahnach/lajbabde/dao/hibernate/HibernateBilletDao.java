package ahnach.lajbabde.dao.hibernate;

import ahnach.lajbabde.config.HibernateUtil;
import ahnach.lajbabde.dao.BilletDao;
import ahnach.lajbabde.model.Billet;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateBilletDao implements BilletDao {
    public List<Billet> findByMatchId(long matchId) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return s.createQuery("from Billet where matchId = :id", Billet.class).setParameter("id", matchId).getResultList(); } }
    public Optional<Billet> findById(long id) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return Optional.ofNullable(s.get(Billet.class, id)); } }
    public Billet save(Billet v) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { tx = s.beginTransaction(); if (v.getId() == null) s.persist(v); else v = (Billet) s.merge(v); tx.commit(); return v; }
        catch (RuntimeException e) { if (tx != null && tx.isActive()) tx.rollback(); throw e; }
    }
}
