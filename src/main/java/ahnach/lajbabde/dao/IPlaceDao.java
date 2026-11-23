package ahnach.lajbabde.dao;
import ahnach.lajbabde.model.Place;
import java.util.List;
import java.util.Optional;
public interface IPlaceDao {
    List<Place> findByStadeId(long stadeId); Optional<Place> findById(long id); Place save(Place place);
}
