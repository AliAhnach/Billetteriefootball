package ahnach.lajbabde.dao.hibernate;

import ahnach.lajbabde.config.HibernateUtil;
import ahnach.lajbabde.dao.IStadeDao;
import ahnach.lajbabde.model.Stade;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateStadeDao implements IStadeDao {
    public List<Stade> findAll() { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return s.createQuery("from Stade", Stade.class).getResultList(); } }
    public Optional<Stade> findById(long id) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return Optional.ofNullable(s.get(Stade.class, id)); } }
    public Stade save(Stade v) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { tx = s.beginTransaction(); if (v.getId() == null) s.persist(v); else v = (Stade) s.merge(v); tx.commit(); return v; }
        catch (RuntimeException e) { if (tx != null && tx.isActive()) tx.rollback(); throw e; }
    }
    public boolean deleteById(long id) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { tx = s.beginTransaction(); Stade v = s.get(Stade.class, id); if (v == null) { tx.commit(); return false; } s.remove(v); tx.commit(); return true; }
        catch (RuntimeException e) { if (tx != null && tx.isActive()) tx.rollback(); throw e; }
    }
}
