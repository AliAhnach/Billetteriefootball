package ahnach.lajbabde.dao;

import ahnach.lajbabde.model.Billet;
import java.util.List;
import java.util.Optional;

/** Contrat d'accès aux données des billets. */
public interface BilletDao extends IBilletDao {
    List<Billet> findByMatchId(long matchId);
    Optional<Billet> findById(long id);
    Billet save(Billet billet);
}
