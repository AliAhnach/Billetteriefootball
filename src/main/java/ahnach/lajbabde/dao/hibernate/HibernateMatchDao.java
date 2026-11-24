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
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                if (v.getId() == null) s.persist(v); else v = (Match) s.merge(v);
                tx.commit();
                return v;
            } catch (RuntimeException e) {
                rollbackWithoutMasking(tx, e);
                throw e;
            }
        }
    }
    public boolean deleteById(long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                Match v = s.get(Match.class, id);
                if (v == null) { tx.commit(); return false; }
                s.createMutationQuery("delete from Billet where matchId = :id").setParameter("id", id).executeUpdate();
                s.createMutationQuery("delete from Reservation where matchId = :id").setParameter("id", id).executeUpdate();
                s.remove(v);
                tx.commit();
                return true;
            } catch (RuntimeException e) {
                rollbackWithoutMasking(tx, e);
                throw e;
            }
        }
    }

    private void rollbackWithoutMasking(Transaction tx, RuntimeException original) {
        if (tx == null || !tx.isActive()) return;
        try {
            tx.rollback();
        } catch (RuntimeException rollbackError) {
            original.addSuppressed(rollbackError);
        }
    }
}
