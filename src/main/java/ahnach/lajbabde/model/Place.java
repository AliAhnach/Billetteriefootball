package ahnach.lajbabde.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "place")
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stadeId;
    private String secteur;
    private String rangee;
    private String numero;
    private boolean disponible = true;

    public Place() { }
    public Place(Long id, Long stadeId, String secteur, String rangee, String numero, boolean disponible) {
        this.id = id; this.stadeId = stadeId; this.secteur = secteur; this.rangee = rangee;
        this.numero = numero; this.disponible = disponible;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStadeId() { return stadeId; }
    public void setStadeId(Long stadeId) { this.stadeId = stadeId; }
    public String getSecteur() { return secteur; }
    public void setSecteur(String secteur) { this.secteur = secteur; }
    public String getRangee() { return rangee; }
    public void setRangee(String rangee) { this.rangee = rangee; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
