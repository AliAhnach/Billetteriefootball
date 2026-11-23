package ahnach.lajbabde.dao;
import ahnach.lajbabde.model.Billet;
import java.util.List;
import java.util.Optional;
public interface IBilletDao {
    List<Billet> findByMatchId(long matchId); Optional<Billet> findById(long id); Billet save(Billet billet);
}
