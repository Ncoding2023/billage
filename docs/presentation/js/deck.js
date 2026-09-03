(() => {
  const slides = [...document.querySelectorAll(".slide")];
  if (!slides.length) return;

  const progress = document.getElementById("progress");
  const pageLabel = document.getElementById("pageLabel");
  const sectionLabel = document.getElementById("sectionLabel");
  const cfg = window.DECK || {};
  const sectionNames = cfg.sectionNames || {};
  const sectionKeys = cfg.sectionKeys || [];

  let i = 0;

  function show(n) {
    i = Math.max(0, Math.min(slides.length - 1, n));
    slides.forEach((s, idx) => s.classList.toggle("active", idx === i));
    if (progress) progress.style.width = ((i + 1) / slides.length * 100) + "%";
    if (pageLabel) pageLabel.textContent = (i + 1) + " / " + slides.length;
    if (sectionLabel) {
      sectionLabel.textContent = sectionNames[slides[i].dataset.section] || "";
    }
  }

  function jumpSection(key) {
    const first = slides.findIndex((s) => s.dataset.section === key);
    if (first >= 0) show(first);
  }

  document.getElementById("prevBtn")?.addEventListener("click", () => show(i - 1));
  document.getElementById("nextBtn")?.addEventListener("click", () => show(i + 1));

  document.addEventListener("keydown", (e) => {
    if (["ArrowRight", "PageDown", " ", "Enter"].includes(e.key)) {
      e.preventDefault();
      show(i + 1);
    } else if (["ArrowLeft", "PageUp", "Backspace"].includes(e.key)) {
      e.preventDefault();
      show(i - 1);
    } else if (e.key === "Home") {
      show(0);
    } else if (e.key === "End") {
      show(slides.length - 1);
    } else if (e.key.toLowerCase() === "f") {
      if (!document.fullscreenElement) document.documentElement.requestFullscreen?.();
      else document.exitFullscreen?.();
    } else if (/^[1-9]$/.test(e.key)) {
      const key = sectionKeys[Number(e.key) - 1];
      if (key) jumpSection(key);
    }
  });

  show(0);
})();
