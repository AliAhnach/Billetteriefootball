(function () {
    const cards = document.querySelectorAll(".category-card[data-league-id]");
    const section = document.getElementById("category-matches");

    if (!cards.length || !section) return;

    const nameEl = document.getElementById("category-matches-name");
    const statusEl = document.getElementById("category-matches-status");
    const listEl = document.getElementById("category-matches-list");
    const leagueCodes = { "2": "uefa.champions" };
    const escapeHtml = (value) => String(value || "").replace(/[&<>"']/g, (char) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", "\"": "&quot;", "'": "&#39;"
    })[char]);

    function formatDate(value) {
        const date = new Date(value);
        return {
            day: date.toLocaleDateString("fr-FR", { day: "2-digit" }),
            month: date.toLocaleDateString("fr-FR", { month: "short" }).replace(".", ""),
            weekday: date.toLocaleDateString("fr-FR", { weekday: "long" }),
            fullDate: date.toLocaleDateString("fr-FR", { day: "numeric", month: "long", year: "numeric" }),
            time: date.toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" })
        };
    }

    function renderMatch(event) {
        const competition = event.competitions && event.competitions[0];
        const competitors = competition && competition.competitors || [];
        const home = competitors.find((team) => team.homeAway === "home") || competitors[0];
        const away = competitors.find((team) => team.homeAway === "away") || competitors[1];
        if (!home || !away) return "";

        const teamHtml = (competitor) => `<span class="match-item-team">${competitor.team.logo ? `<img src="${escapeHtml(competitor.team.logo)}" alt="">` : ""}<span>${escapeHtml(competitor.team.displayName)}</span></span>`;
        const venueInfo = competition.venue || event.venue || {};
        const venue = venueInfo.fullName || venueInfo.displayName || venueInfo.name || "Stade à confirmer";
        const city = venueInfo.address && venueInfo.address.city;
        const kickoff = formatDate(event.date);
        const bookingParams = new URLSearchParams({
            event: event.id || "",
            home: home.team.displayName || "",
            away: away.team.displayName || "",
            homeLogo: home.team.logo || "",
            awayLogo: away.team.logo || "",
            date: kickoff.fullDate,
            time: kickoff.time,
            venue,
            city: city || ""
        });

        return `<article class="match-item">
            <div class="match-item-header">
                <span class="match-item-competition"><span class="match-item-competition-mark" aria-hidden="true">★</span> Ligue des champions</span>
                <span class="match-item-status">À venir</span>
            </div>
            <div class="match-item-main">
                <div class="match-item-date" aria-label="${escapeHtml(kickoff.fullDate)} à ${escapeHtml(kickoff.time)}">
                    <strong>${escapeHtml(kickoff.day)}</strong><span>${escapeHtml(kickoff.month)}</span>
                </div>
                <div class="match-item-teams">${teamHtml(home)}<span class="match-item-score">VS</span>${teamHtml(away)}</div>
            </div>
            <div class="match-item-details">
                <span class="match-item-detail"><span class="match-item-detail-icon" aria-hidden="true">◷</span><span><strong>${escapeHtml(kickoff.time)}</strong><small>${escapeHtml(kickoff.weekday)} ${escapeHtml(kickoff.fullDate)}</small></span></span>
                <span class="match-item-detail"><span class="match-item-detail-icon" aria-hidden="true">⌖</span><span><strong>${escapeHtml(venue)}</strong><small>${escapeHtml(city || "Lieu du match")}</small></span></span>
            </div>
            <a class="match-item-book" href="reservation.html?${bookingParams.toString()}">Réserver une place <span aria-hidden="true">→</span></a>
        </article>`;
    }

    async function loadMatches(card) {
        const league = leagueCodes[card.dataset.leagueId];
        if (!league) throw new Error("Cette compétition n’est pas encore disponible.");

        const dates = [];
        const start = new Date();
        start.setUTCHours(0, 0, 0, 0);
        for (let offset = 1; offset <= 60; offset += 1) {
            const date = new Date(start);
            date.setUTCDate(date.getUTCDate() + offset);
            if (date.getUTCDay() === 2 || date.getUTCDay() === 3) {
                dates.push(date.toISOString().slice(0, 10).replaceAll("-", ""));
            }
        }

        const results = await Promise.allSettled(dates.map(async (date) => {
            const url = `https://site.api.espn.com/apis/site/v2/sports/soccer/${league}/scoreboard?dates=${date}&limit=100`;
            const response = await fetch(url);
            if (!response.ok) throw new Error("HTTP " + response.status);
            return response.json();
        }));
        const dataSets = results
            .filter((result) => result.status === "fulfilled")
            .map((result) => result.value);
        if (!dataSets.length) throw new Error("Le calendrier ESPN est inaccessible pour le moment.");

        const now = Date.now();
        const events = dataSets.flatMap((data) => data.events || [])
            .filter((event) => new Date(event.date).getTime() >= now)
            .filter((event, index, all) => all.findIndex((item) => item.id === event.id) === index)
            .sort((a, b) => new Date(a.date) - new Date(b.date));

        const upcoming = events.slice(0, 5);
        const realMadridMatch = events.find((event) => {
            const competition = event.competitions && event.competitions[0];
            const teams = competition && competition.competitors || [];
            return teams.some((competitor) => /real madrid/i.test(competitor.team && competitor.team.displayName || ""));
        });
        if (realMadridMatch && !upcoming.some((event) => event.id === realMadridMatch.id)) {
            upcoming.push(realMadridMatch);
        }

        listEl.innerHTML = upcoming.map(renderMatch).join("");
        statusEl.textContent = upcoming.length
            ? "Prochains matchs — calendrier ESPN"
            : "Aucun prochain match annoncé pour cette compétition.";
    }

    cards.forEach((card) => {
        card.addEventListener("click", async (event) => {
            event.preventDefault();
            cards.forEach((item) => item.classList.toggle("active", item === card));
            nameEl.textContent = card.dataset.leagueName || "";
            listEl.replaceChildren();
            statusEl.textContent = "Chargement des matchs…";
            statusEl.classList.remove("error");
            section.hidden = false;
            section.scrollIntoView({ behavior: "smooth", block: "start" });

            try {
                await loadMatches(card);
            } catch (error) {
                statusEl.textContent = error.message || "Impossible de charger le calendrier pour le moment.";
                statusEl.classList.add("error");
            }
        });
    });
})();
