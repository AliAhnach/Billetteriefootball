package ahnach.lajbabde.dao.hibernate;

import ahnach.lajbabde.config.HibernateUtil;
import ahnach.lajbabde.dao.MatchDao;
import ahnach.lajbabde.model.Match;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateMatchDao implements MatchDao {
    public List<Match> findAll() { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return s.createQuery("from Match order by dateHeure", Match.class).getResultList(); } }
    public Optional<Match> findById(long id) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return Optional.ofNullable(s.get(Match.class, id)); } }
    public Match save(Match v) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { tx = s.beginTransaction(); if (v.getId() == null) s.persist(v); else v = (Match) s.merge(v); tx.commit(); return v; }
        catch (RuntimeException e) { if (tx != null && tx.isActive()) tx.rollback(); throw e; }
    }
    public boolean deleteById(long id) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { tx = s.beginTransaction(); Match v = s.get(Match.class, id); if (v == null) { tx.commit(); return false; } s.remove(v); tx.commit(); return true; }
        catch (RuntimeException e) { if (tx != null && tx.isActive()) tx.rollback(); throw e; }
    }
}
