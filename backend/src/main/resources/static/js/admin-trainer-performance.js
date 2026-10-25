auth.requireAuth();
renderAdminLayout("trainer-performance");

async function loadPerformance() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading trainer performance...</p>`;

    try {
        const performance = await api.get("/admin/trainer-performance");
        renderPage(performance);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load: ${err.message}</p>`;
    }
}

function renderPage(performance) {
    const content = document.getElementById("pageContent");

    const rowsHtml = performance.length > 0
        ? performance.map(t => `
            <tr class="border-b border-slate-100">
                <td class="py-3 px-2 text-sm font-medium text-slate-800">${t.trainerName}</td>
                <td class="py-3 px-2 text-sm text-slate-500">${t.averageRating}/5 (${t.totalRatings})</td>
                <td class="py-3 px-2 text-sm ${t.averageImprovement > 0 ? 'text-green-600' : 'text-slate-500'}">${t.averageImprovement > 0 ? '+' : ''}${t.averageImprovement}%</td>
                <td class="py-3 px-2 text-sm text-slate-500">${t.completionRate}%</td>
                <td class="py-3 px-2 text-sm text-slate-500">${t.courseCount}</td>
                <td class="py-3 px-2 text-sm text-slate-500">${t.traineeCount}</td>
            </tr>
        `).join("")
        : `<tr><td colspan="6" class="py-6 text-center text-slate-400">No trainer data available.</td></tr>`;

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">Trainer Performance</h1>
        <p class="text-slate-500 mb-6">Compare trainers by rating, improvement impact, and completion rate.</p>

        <div class="bg-white border border-slate-200 rounded-xl p-6">
            <table class="w-full">
                <thead>
                    <tr class="border-b border-slate-200 text-left">
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Trainer</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Rating</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Avg Improvement</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Completion</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Courses</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Trainees</th>
                    </tr>
                </thead>
                <tbody>${rowsHtml}</tbody>
            </table>
        </div>
    `;
}

loadPerformance();