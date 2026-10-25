auth.requireAuth();
renderAdminLayout("dashboard");

const user = auth.getUser();

async function loadDashboard() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading dashboard...</p>`;

    try {
        const [summary, pendingUsers, announcements] = await Promise.all([
            api.get("/admin/dashboard"),
            api.get("/admin/users/pending"),
            api.get("/public/announcements/latest")
        ]);

        renderPage(summary, pendingUsers, announcements);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load dashboard: ${err.message}</p>`;
    }
}

function renderPage(summary, pendingUsers, announcements) {
    const content = document.getElementById("pageContent");

    const pendingHtml = pendingUsers.length > 0
        ? pendingUsers.slice(0, 5).map(u => `
            <div class="flex items-center justify-between px-4 py-3 border border-slate-200 rounded-lg mb-2">
                <div>
                    <p class="text-sm font-medium text-slate-800">${u.fullName}</p>
                    <p class="text-xs text-slate-500">${u.email} &middot; ${u.role}</p>
                </div>
                <a href="admin-users.html" class="text-xs text-indigo-600 font-medium hover:underline">Review &rarr;</a>
            </div>
        `).join("")
        : `<p class="text-sm text-slate-400">No pending approvals.</p>`;

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">Admin Dashboard</h1>
        <p class="text-slate-500 mb-8">Platform-wide overview across the organization.</p>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-5 mb-8">
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <p class="text-sm text-slate-500 mb-1">Total Trainees</p>
                <p class="text-3xl font-bold text-slate-900">${summary.totalTrainees}</p>
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <p class="text-sm text-slate-500 mb-1">Total Trainers</p>
                <p class="text-3xl font-bold text-slate-900">${summary.totalTrainers}</p>
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <p class="text-sm text-slate-500 mb-1">Active Courses</p>
                <p class="text-3xl font-bold text-slate-900">${summary.activeCourses}</p>
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <p class="text-sm text-slate-500 mb-1">Enrollments</p>
                <p class="text-3xl font-bold text-slate-900">${summary.totalEnrollments}</p>
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <p class="text-sm text-slate-500 mb-1">Completed Courses</p>
                <p class="text-3xl font-bold text-slate-900">${summary.completedEnrollments}</p>
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <p class="text-sm text-slate-500 mb-1">Certificates</p>
                <p class="text-3xl font-bold text-slate-900">${summary.totalCertificates}</p>
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <p class="text-sm text-slate-500 mb-1">Assessments</p>
                <p class="text-3xl font-bold text-slate-900">${summary.totalAssessments}</p>
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <p class="text-sm text-slate-500 mb-1">Avg. Assessment Score</p>
                <p class="text-3xl font-bold text-slate-900">${summary.averageAssessmentScore}%</p>
            </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="font-bold text-lg text-slate-900">Pending Approvals</h2>
                    <span class="px-2.5 py-0.5 bg-amber-100 text-amber-700 text-xs font-bold rounded-full">${summary.pendingApprovals}</span>
                </div>
                ${pendingHtml}
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-6">
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
        </div>

        <div class="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
            <a href="admin-heatmap.html" class="block bg-indigo-600 text-white rounded-xl p-5 hover:bg-indigo-700">
                <p class="font-bold">View Organizational Competency Heatmap &rarr;</p>
                <p class="text-sm text-indigo-100 mt-1">See org-wide skill health and top skill gaps.</p>
            </a>
            <a href="admin-trainer-performance.html" class="block bg-slate-800 text-white rounded-xl p-5 hover:bg-slate-700">
                <p class="font-bold">View Trainer Performance &rarr;</p>
                <p class="text-sm text-slate-300 mt-1">Compare trainers by rating, completion, and improvement.</p>
            </a>
        </div>
    `;
}

loadDashboard();