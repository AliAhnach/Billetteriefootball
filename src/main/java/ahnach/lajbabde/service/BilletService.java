package ahnach.lajbabde.service;

import ahnach.lajbabde.dao.BilletDao;
import ahnach.lajbabde.model.Billet;
import java.util.List;

/** Règles métier liées à la réservation des billets. */
public class BilletService {
    private final BilletDao billetDao;

    public BilletService(BilletDao billetDao) {
        this.billetDao = billetDao;
    }

    public List<Billet> billetsPourMatch(long matchId) {
        return billetDao.findByMatchId(matchId);
    }
}
