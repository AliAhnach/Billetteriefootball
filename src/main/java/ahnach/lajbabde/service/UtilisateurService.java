package ahnach.lajbabde.service;

import ahnach.lajbabde.dao.UtilisateurDao;
import ahnach.lajbabde.model.Utilisateur;
import java.util.Optional;

/** Règles métier liées aux comptes utilisateurs. */
public class UtilisateurService {
    private final UtilisateurDao utilisateurDao;

    public UtilisateurService(UtilisateurDao utilisateurDao) {
        this.utilisateurDao = utilisateurDao;
    }

    public Optional<Utilisateur> trouverParEmail(String email) {
        return utilisateurDao.findByEmail(email);
    }
}
