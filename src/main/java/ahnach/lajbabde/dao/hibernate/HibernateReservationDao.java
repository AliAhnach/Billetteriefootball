package ahnach.lajbabde.dao.hibernate;

import ahnach.lajbabde.config.HibernateUtil;
import ahnach.lajbabde.dao.IReservationDao;
import ahnach.lajbabde.model.Reservation;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateReservationDao implements IReservationDao {
    public List<Reservation> findByClientId(long clientId) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return s.createQuery("from Reservation where clientId = :id order by dateReservation desc", Reservation.class).setParameter("id", clientId).getResultList(); } }
    public List<Reservation> findAll() { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return s.createQuery("from Reservation order by dateReservation desc, id desc", Reservation.class).getResultList(); } }
    public Optional<Reservation> findById(long id) { try (Session s = HibernateUtil.getSessionFactory().openSession()) { return Optional.ofNullable(s.get(Reservation.class, id)); } }
    public Reservation save(Reservation v) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { tx = s.beginTransaction(); if (v.getId() == null) s.persist(v); else v = (Reservation) s.merge(v); tx.commit(); return v; }
        catch (RuntimeException e) { if (tx != null && tx.isActive()) tx.rollback(); throw e; }
    }
    public void delete(Reservation reservation) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Reservation managed = s.get(Reservation.class, reservation.getId());
            if (managed != null) s.remove(managed);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
