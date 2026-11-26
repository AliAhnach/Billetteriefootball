package ahnach.lajbabde.dao;
import ahnach.lajbabde.model.Reservation;
import java.util.List;
import java.util.Optional;
public interface IReservationDao {
    List<Reservation> findByClientId(long clientId); List<Reservation> findAll(); Optional<Reservation> findById(long id);
    Reservation save(Reservation reservation); void delete(Reservation reservation);
}
