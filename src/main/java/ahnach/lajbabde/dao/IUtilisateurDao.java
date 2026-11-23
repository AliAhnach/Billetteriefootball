package ahnach.lajbabde.dao;
import ahnach.lajbabde.model.Utilisateur;
import java.util.Optional;
public interface IUtilisateurDao {
    Optional<Utilisateur> findByEmail(String email); Optional<Utilisateur> findById(long id); Utilisateur save(Utilisateur utilisateur);
}
