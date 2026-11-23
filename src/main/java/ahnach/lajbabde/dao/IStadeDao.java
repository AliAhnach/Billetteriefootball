package ahnach.lajbabde.dao;
import ahnach.lajbabde.model.Stade;
import java.util.List;
import java.util.Optional;
public interface IStadeDao {
    List<Stade> findAll(); Optional<Stade> findById(long id); Stade save(Stade stade); boolean deleteById(long id);
}
