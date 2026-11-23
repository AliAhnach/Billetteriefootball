package ahnach.lajbabde.dao;
import ahnach.lajbabde.model.Match;
import java.util.List;
import java.util.Optional;
public interface IMatchDao {
    List<Match> findAll(); Optional<Match> findById(long id); Match save(Match match); boolean deleteById(long id);
}
