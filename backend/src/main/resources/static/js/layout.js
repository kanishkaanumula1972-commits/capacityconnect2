// Builds the shared sidebar + topbar shell for trainee pages.
// Call renderTraineeLayout(activePage) at the top of each trainee page's script,
// passing the current page's identifier so the right nav item highlights.

function renderTraineeLayout(activePage) {
    const user = auth.getUser();
    if (!user) return;

    const navItems = [
        { id: "dashboard", label: "Dashboard", href: "trainee-dashboard.html" },
        { id: "profile", label: "My Profile", href: "trainee-profile.html" },
        { id: "competencies", label: "My Competencies", href: "trainee-competencies.html" },
        { id: "courses", label: "Browse Courses", href: "trainee-courses.html" },
        { id: "my-courses", label: "My Enrollments", href: "trainee-enrollments.html" },
        { id: "certificates", label: "Certificates", href: "trainee-certificates.html" },
        { id: "achievements", label: "Achievements", href: "trainee-achievements.html" },
        { id: "notifications", label: "Notifications", href: "trainee-notifications.html" }
    ];

    const navHtml = navItems.map(item => {
        const isActive = item.id === activePage;
        const activeClasses = isActive
            ? "bg-indigo-600 text-white"
            : "text-slate-300 hover:bg-slate-800 hover:text-white";
        return `<a href="${item.href}" class="block px-4 py-2.5 rounded-lg text-sm font-medium transition ${activeClasses}">${item.label}</a>`;
    }).join("");

    const shellHtml = `
        <div class="flex min-h-screen">
            <aside class="w-64 bg-slate-900 flex-shrink-0 flex flex-col">
                <div class="px-5 py-5 border-b border-slate-800">
                    <a href="../index.html" class="flex items-center gap-2">
                        <div class="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-sm">CC</div>
                        <span class="text-white font-bold text-sm">Capacity Connect</span>
                    </a>
                </div>
                <nav class="flex-1 px-3 py-4 space-y-1">
                    ${navHtml}
                </nav>
                <div class="px-4 py-4 border-t border-slate-800">
                    <p class="text-white text-sm font-medium truncate">${user.fullName}</p>
                    <p class="text-slate-400 text-xs mb-3">Trainee</p>
                    <button onclick="auth.logout()" class="w-full text-left text-sm text-slate-300 hover:text-white">Log out</button>
                </div>
            </aside>
            <main class="flex-1 bg-slate-50 min-h-screen">
                <div id="pageContent" class="max-w-6xl mx-auto px-8 py-8"></div>
            </main>
        </div>
    `;

    document.getElementById("appRoot").innerHTML = shellHtml;
}
function renderTrainerLayout(activePage) {
    const user = auth.getUser();
    if (!user) return;

    const navItems = [
        { id: "dashboard", label: "Dashboard", href: "trainer-dashboard.html" },
        { id: "profile", label: "My Profile", href: "trainer-profile.html" },
        { id: "courses", label: "My Courses", href: "trainer-courses.html" },
        { id: "resources", label: "Learning Resources", href: "trainer-resources.html" },
        { id: "assessments", label: "Assessments", href: "trainer-assessments.html" },
        { id: "feedback", label: "Feedback", href: "trainer-feedback.html" },
        { id: "notifications", label: "Notifications", href: "trainer-notifications.html" }
    ];

    const navHtml = navItems.map(item => {
        const isActive = item.id === activePage;
        const activeClasses = isActive
            ? "bg-indigo-600 text-white"
            : "text-slate-300 hover:bg-slate-800 hover:text-white";
        return `<a href="${item.href}" class="block px-4 py-2.5 rounded-lg text-sm font-medium transition ${activeClasses}">${item.label}</a>`;
    }).join("");

    const shellHtml = `
        <div class="flex min-h-screen">
            <aside class="w-64 bg-slate-900 flex-shrink-0 flex flex-col">
                <div class="px-5 py-5 border-b border-slate-800">
                    <a href="../index.html" class="flex items-center gap-2">
                        <div class="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-sm">CC</div>
                        <span class="text-white font-bold text-sm">Capacity Connect</span>
                    </a>
                </div>
                <nav class="flex-1 px-3 py-4 space-y-1">
                    ${navHtml}
                </nav>
                <div class="px-4 py-4 border-t border-slate-800">
                    <p class="text-white text-sm font-medium truncate">${user.fullName}</p>
                    <p class="text-slate-400 text-xs mb-3">Trainer</p>
                    <button onclick="auth.logout()" class="w-full text-left text-sm text-slate-300 hover:text-white">Log out</button>
                </div>
            </aside>
            <main class="flex-1 bg-slate-50 min-h-screen">
                <div id="pageContent" class="max-w-6xl mx-auto px-8 py-8"></div>
            </main>
        </div>
    `;

    document.getElementById("appRoot").innerHTML = shellHtml;
}

function renderAdminLayout(activePage) {
    const user = auth.getUser();
    if (!user) return;

    const navItems = [
        { id: "dashboard", label: "Dashboard", href: "admin-dashboard.html" },
        { id: "users", label: "Manage Users", href: "admin-users.html" },
        { id: "competencies", label: "Competencies", href: "admin-competencies.html" },
        { id: "skills", label: "Manage Skills", href: "admin-skills.html" },
        { id: "heatmap", label: "Competency Heatmap", href: "admin-heatmap.html" },
        { id: "trainer-performance", label: "Trainer Performance", href: "admin-trainer-performance.html" },
        { id: "achievements", label: "Achievements", href: "admin-achievements.html" },
        { id: "announcements", label: "Announcements", href: "admin-announcements.html" }
    ];

    const navHtml = navItems.map(item => {
        const isActive = item.id === activePage;
        const activeClasses = isActive
            ? "bg-indigo-600 text-white"
            : "text-slate-300 hover:bg-slate-800 hover:text-white";
        return `<a href="${item.href}" class="block px-4 py-2.5 rounded-lg text-sm font-medium transition ${activeClasses}">${item.label}</a>`;
    }).join("");

    const shellHtml = `
        <div class="flex min-h-screen">
            <aside class="w-64 bg-slate-900 flex-shrink-0 flex flex-col">
                <div class="px-5 py-5 border-b border-slate-800">
                    <a href="../index.html" class="flex items-center gap-2">
                        <div class="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-sm">CC</div>
                        <span class="text-white font-bold text-sm">Capacity Connect</span>
                    </a>
                </div>
                <nav class="flex-1 px-3 py-4 space-y-1">
                    ${navHtml}
                </nav>
                <div class="px-4 py-4 border-t border-slate-800">
                    <p class="text-white text-sm font-medium truncate">${user.fullName}</p>
                    <p class="text-slate-400 text-xs mb-3">Admin</p>
                    <button onclick="auth.logout()" class="w-full text-left text-sm text-slate-300 hover:text-white">Log out</button>
                </div>
            </aside>
            <main class="flex-1 bg-slate-50 min-h-screen">
                <div id="pageContent" class="max-w-6xl mx-auto px-8 py-8"></div>
            </main>
        </div>
    `;

    document.getElementById("appRoot").innerHTML = shellHtml;
}