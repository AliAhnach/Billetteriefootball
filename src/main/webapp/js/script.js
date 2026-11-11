const API_FOOTBALL_KEY = "716bc01a9cbcdc054d0eb1ab2c5a19a0"; // Clé API-Football

document.querySelectorAll(".password-toggle").forEach((button) => {
    button.addEventListener("click", () => {
        const input = document.getElementById(button.getAttribute("aria-controls"));
        if (!input) return;

        const showPassword = input.type === "password";
        input.type = showPassword ? "text" : "password";
        button.classList.toggle("is-visible", showPassword);
        button.setAttribute("aria-label", showPassword ? "Masquer le mot de passe" : "Afficher le mot de passe");
    });
});

const todayMatches = document.querySelector("[data-today-matches]");

if (todayMatches) {
    const status = todayMatches.querySelector("[data-today-status]");
    const details = todayMatches.querySelector("[data-today-details]");
    const retryButton = todayMatches.querySelector("[data-today-retry]");
    const counter = todayMatches.querySelector("[data-match-counter]");
    const slideInterval = 3000;
    let slideTimer;

    const fetchPage = async (date, page) => {
        const url = new URL("https://v3.football.api-sports.io/fixtures");
        url.searchParams.set("date", date);
        url.searchParams.set("timezone", "Africa/Casablanca");
        url.searchParams.set("page", String(page));
        const response = await fetch(url, { headers: { "x-apisports-key": API_FOOTBALL_KEY } });
        const data = await response.json().catch(() => ({}));
        const apiError = data.errors && Object.entries(data.errors)
            .map(([name, detail]) => `${name}: ${typeof detail === "string" ? detail : JSON.stringify(detail)}`)
            .join(" · ");
        if (!response.ok) throw new Error(data.message || apiError || `API-Football répond avec le statut ${response.status}.`);
        if (apiError) throw new Error(apiError.slice(0, 240));
        return data;
    };

    const friendlyApiError = (message) => {
        if (message.includes("API_FOOTBALL_KEY")) return "Ajoutez votre clé API-Football dans script.js pour afficher les matchs.";
        if (/suspended|access/i.test(message)) return "Le compte de données des matchs est suspendu. Vérifiez son statut dans le tableau de bord API-Football.";
        if (/quota|limit|requests/i.test(message)) return "La limite de requêtes API-Football est atteinte. Réessayez plus tard.";
        if (/token|api.?key|unauthor|invalid/i.test(message)) return "La clé API-Football n’est pas valide ou n’a pas accès à ces données.";
        return "Les matchs n’ont pas pu être chargés. Vérifiez la connexion et le compte API-Football, puis réessayez.";
    };

    const fetchAllFixtures = async (date) => {
        if (!API_FOOTBALL_KEY.trim()) throw new Error("Ajoutez votre clé API-Football dans la constante API_FOOTBALL_KEY de script.js.");

        const firstPage = await fetchPage(date, 1);
        const totalPages = Math.max(1, Number(firstPage.paging?.total) || 1);
        const events = [...(firstPage.response || [])];

        for (let page = 2; page <= totalPages; page++) {
            const pageData = await fetchPage(date, page);
            events.push(...(pageData.response || []));
        }
        return events;
    };

    const todayLocal = () => {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, "0");
        const day = String(now.getDate()).padStart(2, "0");
        return `${year}-${month}-${day}`;
    };

    const formatDate = (event) => {
        const dateTime = event.strTimestamp
            ? new Date(event.strTimestamp)
            : new Date(`${event.dateEvent}T${event.strTime || "12:00:00"}`);
        if (Number.isNaN(dateTime.getTime())) return event.dateEvent || "À confirmer";
        return new Intl.DateTimeFormat("fr-FR", {
            day: "numeric", month: "long", year: "numeric"
        }).format(dateTime);
    };

    const formatTime = (event) => {
        const time = event.strTime?.match(/^(\d{2}:\d{2})/);
        return time ? time[1] : "À confirmer";
    };

    const initials = (teamName) => teamName
        .split(/\s+/).filter(Boolean).slice(0, 2).map((word) => word[0]).join("").toUpperCase();

    const setTeam = (side, name, badgeUrl) => {
        const initialsElement = todayMatches.querySelector(`[data-${side}-initials]`);
        const badge = todayMatches.querySelector(`[data-${side}-logo]`);
        todayMatches.querySelector(`[data-${side}-name]`).textContent = name;
        initialsElement.textContent = initials(name);
        initialsElement.hidden = Boolean(badgeUrl);
        badge.hidden = !badgeUrl;
        if (badgeUrl) badge.src = badgeUrl;
    };

    const showMatch = (events, index) => {
        const event = events[index];
        todayMatches.querySelector("[data-match-competition]").textContent = event.league.name || "Football";
        setTeam("home", event.teams.home.name, event.teams.home.logo);
        setTeam("away", event.teams.away.name, event.teams.away.logo);
        todayMatches.querySelector("[data-match-date]").textContent = formatDate({ strTimestamp: event.fixture.date });
        todayMatches.querySelector("[data-match-time]").textContent = formatTime({ strTime: event.fixture.date.slice(11, 16) });
        todayMatches.querySelector("[data-match-venue]").textContent = event.fixture.venue?.name || "À confirmer";
        todayMatches.querySelector("[data-buy-ticket]").href = `reservation.html?event=${encodeURIComponent(event.fixture.id)}`;
        counter.textContent = `${index + 1} / ${events.length}`;
        counter.hidden = events.length < 2;

        details.classList.remove("match-slide-in");
        void details.offsetWidth;
        details.classList.add("match-slide-in");
    };

    const loadTodayMatches = async () => {
        window.clearInterval(slideTimer);
        retryButton.hidden = true;
        retryButton.disabled = true;
        details.hidden = true;
        counter.hidden = true;
        status.hidden = false;
        status.dataset.state = "loading";
        status.textContent = "Chargement des matchs du jour…";

        try {
            const events = (await fetchAllFixtures(todayLocal()))
                .filter((item) => item.fixture && item.teams && item.teams.home && item.teams.away)
                .sort((first, second) => new Date(first.fixture.date) - new Date(second.fixture.date));

            if (!events.length) {
                status.dataset.state = "empty";
                status.textContent = "Aucun match de football n’est disponible pour aujourd’hui.";
                retryButton.hidden = false;
                return;
            }

            let currentIndex = 0;
            showMatch(events, currentIndex);
            status.hidden = true;
            details.hidden = false;
            if (events.length > 1) {
                slideTimer = window.setInterval(() => {
                    currentIndex = (currentIndex + 1) % events.length;
                    showMatch(events, currentIndex);
                }, slideInterval);
            }
        } catch (error) {
            console.error("Impossible de charger les matchs du jour depuis API-Football.", error);
            status.dataset.state = "error";
            status.textContent = friendlyApiError(error.message);
            retryButton.hidden = false;
        } finally {
            retryButton.disabled = false;
        }
    };

    retryButton.addEventListener("click", loadTodayMatches);
    loadTodayMatches();
}

const stadiumPanorama = document.querySelector("[data-stadium-panorama]");

if (stadiumPanorama) {
    const track = stadiumPanorama.querySelector("[data-stadium-track]");
    const slides = Array.from(track.children);
    const dotsContainer = stadiumPanorama.querySelector("[data-stadium-dots]");
    const counter = stadiumPanorama.querySelector("[data-stadium-counter]");
    const previousButton = stadiumPanorama.querySelector("[data-stadium-previous]");
    const nextButton = stadiumPanorama.querySelector("[data-stadium-next]");
    let currentSlide = 0;

    const dots = slides.map((slide, index) => {
        const dot = document.createElement("button");
        dot.className = "stadium-dot";
        dot.type = "button";
        dot.setAttribute("aria-label", `Afficher la vue ${index + 1} du stade`);
        dot.addEventListener("click", () => showStadiumSlide(index));
        dotsContainer.appendChild(dot);
        return dot;
    });

    function showStadiumSlide(index) {
        currentSlide = (index + slides.length) % slides.length;
        track.style.transform = `translateX(-${currentSlide * 100}%)`;
        slides.forEach((slide, slideIndex) => {
            const active = slideIndex === currentSlide;
            slide.setAttribute("aria-hidden", String(!active));
            slide.inert = !active;
            dots[slideIndex].setAttribute("aria-current", String(active));
        });
        counter.textContent = `${String(currentSlide + 1).padStart(2, "0")} / ${String(slides.length).padStart(2, "0")}`;
    }

    previousButton.addEventListener("click", () => showStadiumSlide(currentSlide - 1));
    nextButton.addEventListener("click", () => showStadiumSlide(currentSlide + 1));
    showStadiumSlide(0);

    if (!window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
        window.setInterval(() => {
            if (!stadiumPanorama.matches(":hover") && !stadiumPanorama.matches(":focus-within") && !document.hidden) {
                showStadiumSlide(currentSlide + 1);
            }
        }, 5000);
    }
}

const competitionCarousel = document.querySelector("[data-competition-carousel]");

if (competitionCarousel) {
    const track = competitionCarousel.querySelector("[data-carousel-track]");
    const slides = Array.from(track.children);
    const dotsContainer = competitionCarousel.querySelector("[data-carousel-dots]");
    const counter = competitionCarousel.querySelector("[data-carousel-counter]");
    const previousButton = competitionCarousel.querySelector("[data-carousel-previous]");
    const nextButton = competitionCarousel.querySelector("[data-carousel-next]");
    let currentSlide = 0;

    const dots = slides.map((slide, index) => {
        const dot = document.createElement("button");
        dot.className = "competition-dot";
        dot.type = "button";
        dot.setAttribute("aria-label", `Afficher la carte ${index + 1} : ${slide.querySelector(".competition-card-title").textContent}`);
        dot.addEventListener("click", () => showCompetitionSlide(index));
        dotsContainer.appendChild(dot);
        return dot;
    });

    function showCompetitionSlide(index) {
        currentSlide = (index + slides.length) % slides.length;
        track.style.transform = `translateX(-${currentSlide * 100}%)`;
        slides.forEach((slide, slideIndex) => {
            const active = slideIndex === currentSlide;
            slide.setAttribute("aria-hidden", String(!active));
            slide.inert = !active;
            dots[slideIndex].setAttribute("aria-current", String(active));
        });
        counter.textContent = `${String(currentSlide + 1).padStart(2, "0")} / ${String(slides.length).padStart(2, "0")}`;
    }

    previousButton.addEventListener("click", () => showCompetitionSlide(currentSlide - 1));
    nextButton.addEventListener("click", () => showCompetitionSlide(currentSlide + 1));
    showCompetitionSlide(0);

    if (!window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
        window.setInterval(() => {
            if (!competitionCarousel.matches(":hover") && !competitionCarousel.matches(":focus-within") && !document.hidden) {
                showCompetitionSlide(currentSlide + 1);
            }
        }, 5000);
    }
}

const revealTargets = document.querySelectorAll(".upcoming-match, .platform-heading, .feature-card, .competition-heading, .competition-carousel");
if (revealTargets.length && "IntersectionObserver" in window && !window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
    document.documentElement.classList.add("motion-ready");
    const revealObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add("is-visible");
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.12 });

    revealTargets.forEach((element) => {
        element.classList.add("reveal-on-scroll");
        revealObserver.observe(element);
    });
}
