document.getElementById("signupForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const errorBox = document.getElementById("errorBox");
    const successBox = document.getElementById("successBox");
    const submitBtn = document.getElementById("submitBtn");
    errorBox.classList.add("hidden");
    successBox.classList.add("hidden");

    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const role = document.getElementById("role").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {
        errorBox.textContent = "Passwords do not match.";
        errorBox.classList.remove("hidden");
        return;
    }

    submitBtn.disabled = true;
    submitBtn.textContent = "Creating account...";

    try {
        const response = await api.post("/auth/register", {
            fullName, email, password, confirmPassword, role
        });

        if (response.status === "PENDING") {
            successBox.textContent = "Account created. Your trainer account is pending admin approval - you'll be able to log in once approved.";
            successBox.classList.remove("hidden");
            document.getElementById("signupForm").reset();
            submitBtn.disabled = false;
            submitBtn.textContent = "Create Account";
            return;
        }

        // ACTIVE (trainee) - log them straight in
        auth.setSession(response);
        auth.redirectToDashboard(response.role);

    } catch (err) {
        errorBox.textContent = err.message;
        errorBox.classList.remove("hidden");
        submitBtn.disabled = false;
        submitBtn.textContent = "Create Account";
    }
});