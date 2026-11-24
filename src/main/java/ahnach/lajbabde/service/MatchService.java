package ahnach.lajbabde.service;

import ahnach.lajbabde.dao.MatchDao;
import ahnach.lajbabde.model.Match;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public Match enregistrerMatch(Long id, String domicile, String exterieur, String competition,
                                  String stade, LocalDateTime dateHeure, BigDecimal prix,
                                  String photoDomicile, String photoExterieure) {
        if (vide(domicile) || vide(exterieur) || vide(competition) || vide(stade)
                || dateHeure == null || prix == null || prix.signum() < 0) {
            throw new IllegalArgumentException("Renseignez toutes les informations du match et un prix positif ou nul.");
        }
        Match match;
        if (id == null) {
            match = new Match();
        } else {
            match = matchDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Match introuvable."));
        }
        match.setEquipeDomicile(domicile.trim());
        match.setEquipeExterieure(exterieur.trim());
        match.setCompetition(competition.trim());
        match.setStade(stade.trim());
        match.setDateHeure(dateHeure);
        match.setPrix(prix);
        if (photoDomicile != null) match.setPhotoDomicileFichier(photoDomicile);
        if (photoExterieure != null) match.setPhotoExterieureFichier(photoExterieure);
        return matchDao.save(match);
    }

    public boolean supprimerMatch(long id) {
        return matchDao.deleteById(id);
    }

    private boolean vide(String valeur) { return valeur == null || valeur.trim().isEmpty(); }
}
