package ahnach.lajbabde.dao.hibernate;

import ahnach.lajbabde.config.HibernateUtil;
import ahnach.lajbabde.dao.IPlaceDao;
import ahnach.lajbabde.model.Place;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernatePlaceDao implements IPlaceDao {
    public List<Place> findByStadeId(long stadeId) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return s.createQuery("from Place where stadeId = :id", Place.class).setParameter("id", stadeId).getResultList(); } }
    public Optional<Place> findById(long id) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return Optional.ofNullable(s.get(Place.class, id)); } }
    public Place save(Place v) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { tx = s.beginTransaction(); if (v.getId() == null) s.persist(v); else v = (Place) s.merge(v); tx.commit(); return v; }
        catch (RuntimeException e) { if (tx != null && tx.isActive()) tx.rollback(); throw e; }
    }
}
