package ahnach.lajbabde.dao;

import ahnach.lajbabde.model.Utilisateur;
import java.util.Optional;

/** Contrat d'accès aux comptes utilisateurs. */
public interface UtilisateurDao {
    Optional<Utilisateur> findByEmail(String email);
    Utilisateur save(Utilisateur utilisateur);
}
