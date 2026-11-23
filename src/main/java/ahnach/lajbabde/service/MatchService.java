package ahnach.lajbabde.service;

import ahnach.lajbabde.dao.MatchDao;
import ahnach.lajbabde.model.Match;
import java.util.List;
import java.util.Optional;

/** Règles métier liées aux matchs. */
public class MatchService {
    private final MatchDao matchDao;

    public MatchService(MatchDao matchDao) {
        this.matchDao = matchDao;
    }

    public List<Match> listerMatchs() {
        return matchDao.findAll();
    }

    public Optional<Match> trouverMatch(long id) {
        return matchDao.findById(id);
    }
}
