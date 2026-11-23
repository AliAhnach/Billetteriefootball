package ahnach.lajbabde.service;

import ahnach.lajbabde.dao.UtilisateurDao;
import ahnach.lajbabde.model.Utilisateur;
import ahnach.lajbabde.model.Client;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

/** Règles métier liées aux comptes utilisateurs. */
public class UtilisateurService {
    private final UtilisateurDao utilisateurDao;

    public UtilisateurService(UtilisateurDao utilisateurDao) {
        this.utilisateurDao = utilisateurDao;
    }

    public Optional<Utilisateur> trouverParEmail(String email) {
        return utilisateurDao.findByEmail(normaliserEmail(email));
    }

    public Utilisateur inscrireClient(String nom, String email, String motDePasse) {
        if (nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Le nom est obligatoire.");
        if (motDePasse == null || motDePasse.length() < 8) throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
        String adresse = normaliserEmail(email);
        if (adresse.isEmpty() || !adresse.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Saisissez une adresse e-mail valide.");
        }
        if (utilisateurDao.findByEmail(adresse).isPresent()) throw new IllegalArgumentException("Cette adresse e-mail est déjà utilisée.");

        Client client = new Client();
        client.setNom(nom.trim());
        client.setEmail(adresse);
        client.setMotDePasseHash(BCrypt.hashpw(motDePasse, BCrypt.gensalt(12)));
        client.setRole("CLIENT");
        return utilisateurDao.save(client);
    }

    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        if (motDePasse == null || motDePasse.isEmpty()) return Optional.empty();
        Optional<Utilisateur> compte = utilisateurDao.findByEmail(normaliserEmail(email));
        if (!compte.isPresent()) return Optional.empty();
        Utilisateur utilisateur = compte.get();
        String hash = utilisateur.getMotDePasseHash();
        return hash != null && BCrypt.checkpw(motDePasse, hash) ? compte : Optional.empty();
    }

    private String normaliserEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
