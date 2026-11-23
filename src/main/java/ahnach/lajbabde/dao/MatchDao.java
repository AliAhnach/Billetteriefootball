package ahnach.lajbabde.dao;

import ahnach.lajbabde.model.Match;
import java.util.List;
import java.util.Optional;

/** Contrat d'accès aux données des matchs. */
public interface MatchDao {
    List<Match> findAll();
    Optional<Match> findById(long id);
    Match save(Match match);
    boolean deleteById(long id);
}
