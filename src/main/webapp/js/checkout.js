(function () {
    let reservation;
    try {
        reservation = JSON.parse(localStorage.getItem("foottickets-current-reservation") || "null");
    } catch (_) {
        reservation = null;
    }

    if (!reservation) {
        window.location.replace("categories.html");
        return;
    }

    const setText = (field, value) => {
        const element = document.querySelector(`[data-field="${field}"]`);
        if (element) element.textContent = value || "À confirmer";
    };

    setText("home", reservation.home || "Équipe à confirmer");
    setText("away", reservation.away || "Équipe à confirmer");
    [["homeLogo", reservation.homeLogo], ["awayLogo", reservation.awayLogo]].forEach(([field, url]) => {
        const image = document.querySelector(`[data-field="${field}"]`);
        if (image && /^https:\/\//i.test(url || "")) {
            image.src = url;
            image.alt = "Logo " + (field === "homeLogo" ? reservation.home : reservation.away);
            image.closest(".checkout-team").classList.add("has-logo");
        }
    });
    setText("datetime", [reservation.date, reservation.time].filter(Boolean).join(" · "));
    setText("venue", reservation.venue);
    setText("city", reservation.city);
    setText("category", reservation.category || "Catégorie à confirmer");
    const quantity = Number(reservation.quantity) || 1;
    setText("quantity", `${quantity} ${quantity > 1 ? "places" : "place"}`);
})();
