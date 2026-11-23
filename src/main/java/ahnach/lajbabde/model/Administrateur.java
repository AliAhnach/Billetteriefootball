package ahnach.lajbabde.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** Compte administrateur de la billetterie. */
@Entity
@DiscriminatorValue("ADMIN")
public class Administrateur extends Utilisateur {
    public Administrateur() { }
}
