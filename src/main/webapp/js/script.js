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

document.querySelectorAll(".image-slider").forEach((slider) => {
    const slides = Array.from(slider.querySelectorAll(".slide"));
    const indicators = slider.querySelector(".slider-indicators");
    if (slides.length < 2 || !indicators) return;

    let current = 0;
    slides.forEach((_, index) => {
        const indicator = document.createElement("span");
        if (index === 0) indicator.classList.add("active");
        indicators.appendChild(indicator);
    });
    const bars = Array.from(indicators.children);

    window.setInterval(() => {
        const previous = current;
        current = (current + 1) % slides.length;
        slides[previous].classList.remove("active");
        slides[previous].classList.add("leaving");
        slides[current].classList.add("active");
        bars[previous].classList.remove("active");
        bars[current].classList.add("active");
        window.setTimeout(() => slides[previous].classList.remove("leaving"), 800);
    }, 3000);
});
