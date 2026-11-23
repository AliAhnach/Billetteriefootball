package ahnach.lajbabde.model;

import java.math.BigDecimal;

/** Représente un billet disponible ou réservé. */
public class Billet {
    private Long id;
    private Long matchId;
    private String categorie;
    private BigDecimal prix;
    private boolean disponible;

    public Billet() {
    }

    public Billet(Long id, Long matchId, String categorie, BigDecimal prix, boolean disponible) {
        this.id = id;
        this.matchId = matchId;
        this.categorie = categorie;
        this.prix = prix;
        this.disponible = disponible;
    }
// getters et settrs
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
