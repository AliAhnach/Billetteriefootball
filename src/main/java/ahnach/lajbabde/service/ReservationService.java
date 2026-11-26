package ahnach.lajbabde.service;

import ahnach.lajbabde.dao.IReservationDao;
import ahnach.lajbabde.dao.IPlaceDao;
import ahnach.lajbabde.model.Place;
import ahnach.lajbabde.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Règles de réservation et de libération des places. */
public class ReservationService {
    private final IReservationDao reservationDao;
    private final IPlaceDao placeDao;

    public ReservationService(IReservationDao reservationDao, IPlaceDao placeDao) {
        this.reservationDao = reservationDao; this.placeDao = placeDao;
    }

    public List<Reservation> reservationsDuClient(long clientId) { return reservationDao.findByClientId(clientId); }
    public Reservation reservationPourClient(long clientId, long reservationId) {
        return reservationDuClient(clientId, reservationId);
    }
    public List<Reservation> toutesLesReservations() { return reservationDao.findAll(); }
    public void traiterParAdmin(long reservationId, String action, String categorie) {
        Reservation reservation = reservationDao.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));
        if ("attribuer".equals(action)) {
            if (categorie == null || !(categorie.equals("Tribune centrale") || categorie.equals("Tribune latérale") || categorie.equals("Tribune populaire")))
                throw new IllegalArgumentException("Catégorie de tribune invalide");
            reservation.setCategorie(categorie);
        } else if ("confirmer".equals(action)) {
            if ("ANNULEE".equals(reservation.getStatut())) throw new IllegalStateException("Une réservation annulée ne peut pas être confirmée");
            reservation.setStatut("VALIDEE");
        } else if ("annuler".equals(action)) {
            if ("ANNULEE".equals(reservation.getStatut())) throw new IllegalStateException("Cette réservation est déjà annulée");
            if ("VALIDEE".equals(reservation.getStatut())) throw new IllegalStateException("Une réservation confirmée ne peut plus être annulée");
            reservation.setStatut("ANNULEE");
            libererPlace(reservation);
        } else throw new IllegalArgumentException("Action inconnue");
        reservationDao.save(reservation);
    }
    public Reservation reserver(long clientId, long matchId, long placeId) {
        Place place = placeDao.findById(placeId).orElseThrow(() -> new IllegalArgumentException("Place introuvable"));
        if (!place.isDisponible()) throw new IllegalStateException("Cette place n'est plus disponible");
        place.setDisponible(false);
        placeDao.save(place);
        return reservationDao.save(new Reservation(null, LocalDate.now(), "EN_ATTENTE", clientId, matchId, placeId));
    }

    public Reservation reserver(long clientId, long matchId, String categorie, int nombrePlaces) {
        if (categorie == null || categorie.trim().isEmpty()) {
            throw new IllegalArgumentException("Veuillez choisir une catégorie de tribune");
        }
        if (nombrePlaces < 1 || nombrePlaces > 10) {
            throw new IllegalArgumentException("Le nombre de places doit être compris entre 1 et 10");
        }
        return reservationDao.save(new Reservation(null, LocalDate.now(), "EN_ATTENTE", clientId, matchId,
                categorie.trim(), nombrePlaces));
    }

    public void annuler(long reservationId) {
        Reservation reservation = reservationDao.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));
        reservation.setStatut("ANNULEE");
        reservationDao.save(reservation);
        libererPlace(reservation);
    }

    private void libererPlace(Reservation reservation) {
        if (reservation.getPlaceId() == null) return;
        placeDao.findById(reservation.getPlaceId()).ifPresent(place -> {
            place.setDisponible(true);
            placeDao.save(place);
        });
    }

    public void annulerPourClient(long clientId, long reservationId) {
        Reservation reservation = reservationDuClient(clientId, reservationId);
        if ("ANNULEE".equals(reservation.getStatut())) {
            throw new IllegalStateException("Cette réservation est déjà annulée");
        }
        if ("VALIDEE".equals(reservation.getStatut())) {
            throw new IllegalStateException("Une réservation confirmée ne peut plus être annulée");
        }
        annuler(reservationId);
    }

    public void supprimerPourClient(long clientId, long reservationId) {
        Reservation reservation = reservationDuClient(clientId, reservationId);
        reservationDao.delete(reservation);
    }

    private Reservation reservationDuClient(long clientId, long reservationId) {
        Reservation reservation = reservationDao.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));
        if (!Objects.equals(reservation.getClientId(), clientId)) {
            throw new IllegalArgumentException("Vous ne pouvez modifier que vos propres réservations");
        }
        return reservation;
    }
}
