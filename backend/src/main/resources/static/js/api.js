// Centralized API communication layer for Capacity Connect.
// Every page includes this file and calls window.api.* methods.

const API_BASE_URL = "/api";

function getToken() {
    return localStorage.getItem("cc_token");
}

function getUser() {
    const raw = localStorage.getItem("cc_user");
    return raw ? JSON.parse(raw) : null;
}

function setSession(authResponse) {
    localStorage.setItem("cc_token", authResponse.token || "");
    localStorage.setItem("cc_user", JSON.stringify({
        userId: authResponse.userId,
        fullName: authResponse.fullName,
        email: authResponse.email,
        role: authResponse.role,
        status: authResponse.status
    }));
}

function clearSession() {
    localStorage.removeItem("cc_token");
    localStorage.removeItem("cc_user");
}

function logout() {
    clearSession();
    window.location.href = "login.html";
}

// Redirects to login if there's no token. Call this at the top of any
// protected page's script.
function requireAuth() {
    if (!getToken()) {
        window.location.href = "login.html";
    }
}

// Redirects to the correct dashboard based on the logged-in user's role.
// Call this from login/signup success handlers.
function redirectToDashboard(role) {
    if (role === "TRAINEE") {
        window.location.href = "trainee-dashboard.html";
    } else if (role === "TRAINER") {
        window.location.href = "trainer-dashboard.html";
    } else if (role === "ADMIN") {
        window.location.href = "admin-dashboard.html";
    } else {
        window.location.href = "../index.html";
    }
}

/**
 * Core request function. Automatically attaches the JWT (if present)
 * and parses JSON. Throws an Error with a readable message on failure,
 * so callers can just try/catch and show err.message to the user.
 */
async function apiRequest(method, path, body, isFormData = false) {
        const headers = {
        "ngrok-skip-browser-warning": "true"
    };
    const token = getToken();
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }

    const options = { method, headers };

    if (body !== undefined && body !== null) {
        if (isFormData) {
            options.body = body; // FormData sets its own Content-Type boundary
        } else {
            headers["Content-Type"] = "application/json";
            options.body = JSON.stringify(body);
        }
    }

    let response;
    try {
        response = await fetch(API_BASE_URL + path, options);
    } catch (networkErr) {
        throw new Error("Cannot reach the server. Please check your connection and that the backend is running.");
    }

    // No content responses (e.g. some DELETE calls)
    if (response.status === 204) {
        return null;
    }

    let data;
    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        data = await response.json();
    } else {
        data = await response.text();
    }

    if (!response.ok) {
        const message = (data && data.message) ? data.message : `Request failed (${response.status})`;
        throw new Error(message);
    }

    return data;
}

// Exposed as a single global object so pages can call e.g. api.get(...)
window.api = {
    get: (path) => apiRequest("GET", path),
    post: (path, body) => apiRequest("POST", path, body),
    postForm: (path, formData) => apiRequest("POST", path, formData, true),
    put: (path, body) => apiRequest("PUT", path, body),
    delete: (path) => apiRequest("DELETE", path),
};

window.auth = {
    getToken,
    getUser,
    setSession,
    clearSession,
    logout,
    requireAuth,
    redirectToDashboard
};