package ahnach.lajbabde.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

/** Représente un match proposé à la réservation. */
@Entity
@Table(name = "matchs")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String equipeDomicile;
    private String equipeExterieure;
    private String competition;
    private String stade;
    private LocalDateTime dateHeure;
    @Column(precision = 10, scale = 2)
    private BigDecimal prix;
    private String photoDomicileFichier;
    private String photoExterieureFichier;

    public Match() {
    }

    public Match(Long id, String equipeDomicile, String equipeExterieure,
                 String competition, String stade, LocalDateTime dateHeure) {
        this.id = id;
        this.equipeDomicile = equipeDomicile;
        this.equipeExterieure = equipeExterieure;
        this.competition = competition;
        this.stade = stade;
        this.dateHeure = dateHeure;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEquipeDomicile() { return equipeDomicile; }
    public void setEquipeDomicile(String equipeDomicile) { this.equipeDomicile = equipeDomicile; }
    public String getEquipeExterieure() { return equipeExterieure; }
    public void setEquipeExterieure(String equipeExterieure) { this.equipeExterieure = equipeExterieure; }
    public String getCompetition() { return competition; }
    public void setCompetition(String competition) { this.competition = competition; }
    public String getStade() { return stade; }
    public void setStade(String stade) { this.stade = stade; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public String getPhotoDomicileFichier() { return photoDomicileFichier; }
    public void setPhotoDomicileFichier(String photoDomicileFichier) { this.photoDomicileFichier = photoDomicileFichier; }
    public String getPhotoExterieureFichier() { return photoExterieureFichier; }
    public void setPhotoExterieureFichier(String photoExterieureFichier) { this.photoExterieureFichier = photoExterieureFichier; }
}
