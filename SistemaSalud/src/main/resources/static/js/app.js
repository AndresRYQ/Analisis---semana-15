const sidebar = document.querySelector("[data-sidebar]");
const sidebarToggle = document.querySelector("[data-sidebar-toggle]");

if (sidebar && sidebarToggle) {
    sidebarToggle.addEventListener("click", () => {
        const isOpen = sidebar.classList.toggle("open");
        sidebarToggle.setAttribute("aria-expanded", String(isOpen));
    });

    sidebar.querySelectorAll("a").forEach((link) => {
        link.addEventListener("click", () => {
            sidebar.classList.remove("open");
            sidebarToggle.setAttribute("aria-expanded", "false");
        });
    });
}
