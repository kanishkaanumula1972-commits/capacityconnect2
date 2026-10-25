auth.requireAuth();
renderTraineeLayout("dashboard");

const user = auth.getUser();

async function loadDashboard() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading your dashboard...</p>`;

    try {
                const [competencyProfile, enrollments, notifications, announcements] = await Promise.all([
            api.get("/trainees/competencies"),
            api.get("/enrollments/my"),
            api.get("/notifications"),
            api.get("/public/announcements/latest")
        ]);

        const inProgressCount = enrollments.filter(e => e.status === "IN_PROGRESS" || e.status === "ENROLLED").length;
        const completedCount = enrollments.filter(e => e.status === "COMPLETED").length;
        const unreadNotifications = notifications.filter(n => !n.isRead).length;

        const skillRows = competencyProfile.skills.length > 0
            ? competencyProfile.skills.map(s => `
                <div class="mb-3">
                    <div class="flex justify-between text-sm mb-1">
                        <span class="font-medium text-slate-700">${s.skillName}</span>
                        <span class="text-slate-500">${s.score}%</span>
                    </div>
                    <div class="w-full bg-slate-200 rounded-full h-2">
                        <div class="bg-indigo-600 h-2 rounded-full" style="width: ${s.score}%"></div>
                    </div>
                </div>
            `).join("")
            : `<p class="text-sm text-slate-400">No skills recorded yet. Add skills from your profile page.</p>`;

        content.innerHTML = `
            <h1 class="text-2xl font-bold text-slate-900 mb-1">Welcome back, ${user.fullName.split(" ")[0]}.</h1>
            <p class="text-slate-500 mb-8">Here's where you stand today.</p>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-5 mb-8">
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Courses In Progress</p>
                    <p class="text-3xl font-bold text-slate-900">${inProgressCount}</p>
                </div>
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Courses Completed</p>
                    <p class="text-3xl font-bold text-slate-900">${completedCount}</p>
                </div>
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Unread Notifications</p>
                    <p class="text-3xl font-bold text-slate-900">${unreadNotifications}</p>
                </div>
            </div>

            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <div class="flex items-center justify-between mb-5">
                    <h2 class="font-bold text-lg text-slate-900">Your Competency Profile</h2>
                    <a href="trainee-competencies.html" class="text-sm text-indigo-600 font-medium hover:underline">View gap analysis &rarr;</a>
                </div>
                ${skillRows}
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-6 mt-6">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Recent Announcements</h2>
                ${announcements.length > 0
                    ? announcements.map(a => `
                        <div class="px-4 py-3 border border-slate-200 rounded-lg mb-2">
                            <p class="text-sm font-medium text-slate-800">${a.title}</p>
                            <p class="text-xs text-slate-500 mt-1">${a.content}</p>
                        </div>
                    `).join("")
                    : `<p class="text-sm text-slate-400">No announcements yet.</p>`
                }
            </div>
        `;

    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load dashboard: ${err.message}</p>`;
    }
}

loadDashboard();