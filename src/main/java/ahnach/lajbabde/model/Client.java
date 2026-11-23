package ahnach.lajbabde.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** Compte client de la billetterie. */
@Entity
@DiscriminatorValue("CLIENT")
public class Client extends Utilisateur {
    public Client() { }
}
