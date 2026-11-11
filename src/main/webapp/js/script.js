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
