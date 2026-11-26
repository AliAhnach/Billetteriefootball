package ahnach.lajbabde.service;

import ahnach.lajbabde.dao.BilletDao;
import ahnach.lajbabde.model.Billet;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
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

    public Map<String, BigDecimal> tarifsDisponibles(long matchId) {
        Map<String, BigDecimal> tarifs = new TreeMap<>();
        for (Billet billet : billetDao.findByMatchId(matchId)) {
            if (billet.isDisponible() && billet.getCategorie() != null && billet.getPrix() != null) {
                tarifs.putIfAbsent(billet.getCategorie(), billet.getPrix());
            }
        }
        return tarifs;
    }

    public void mettreAJourTarifs(long matchId, Map<String, BigDecimal> nouveauxTarifs) {
        Map<String, Boolean> tarifsMisAJour = new TreeMap<>();
        for (Billet billet : billetDao.findByMatchId(matchId)) {
            BigDecimal prix = nouveauxTarifs.get(billet.getCategorie());
            if (billet.isDisponible() && prix != null) {
                billet.setPrix(prix);
                billetDao.save(billet);
                tarifsMisAJour.put(billet.getCategorie(), true);
            }
        }
        for (Map.Entry<String, BigDecimal> tarif : nouveauxTarifs.entrySet()) {
            if (!tarifsMisAJour.containsKey(tarif.getKey())) {
                billetDao.save(new Billet(null, matchId, tarif.getKey(), tarif.getValue(), true));
            }
        }
    }
}
