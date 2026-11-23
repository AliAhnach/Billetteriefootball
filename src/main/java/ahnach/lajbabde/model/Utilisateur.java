package ahnach.lajbabde.model;

/** Représente un compte client ou administrateur. */
public class Utilisateur {
    private Long id;
    private String nom;
    private String email;
    private String motDePasseHash;
    private String role;

    public Utilisateur() {
    }

    public Utilisateur(Long id, String nom, String email, String motDePasseHash, String role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasseHash() { return motDePasseHash; }
    public void setMotDePasseHash(String motDePasseHash) { this.motDePasseHash = motDePasseHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
