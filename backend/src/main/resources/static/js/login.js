document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const errorBox = document.getElementById("errorBox");
    const submitBtn = document.getElementById("submitBtn");
    errorBox.classList.add("hidden");

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    submitBtn.disabled = true;
    submitBtn.textContent = "Logging in...";

    try {
        const response = await api.post("/auth/login", { email, password });
        auth.setSession(response);
        auth.redirectToDashboard(response.role);
    } catch (err) {
        errorBox.textContent = err.message;
        errorBox.classList.remove("hidden");
        submitBtn.disabled = false;
        submitBtn.textContent = "Log In";
    }
});