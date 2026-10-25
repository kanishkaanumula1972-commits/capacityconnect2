auth.requireAuth();
renderTraineeLayout("achievements");

async function loadAchievements() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading achievements...</p>`;

    try {
        const [earned, all] = await Promise.all([
            api.get("/trainees/achievements"),
            api.get("/public/achievements")
        ]);
        renderPage(earned, all);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load achievements: ${err.message}</p>`;
    }
}

function renderPage(earned, all) {
    const content = document.getElementById("pageContent");
    const earnedIds = new Set(earned.map(a => a.id));

    const cardsHtml = all.map(a => {
        const isEarned = earnedIds.has(a.id);
        const earnedRecord = earned.find(e => e.id === a.id);
        return `
            <div class="bg-white border ${isEarned ? 'border-indigo-300' : 'border-slate-200'} rounded-xl p-5 ${isEarned ? '' : 'opacity-50'}">
                <div class="w-12 h-12 ${isEarned ? 'bg-indigo-100' : 'bg-slate-100'} rounded-lg flex items-center justify-center mb-3">
                    <span class="text-xs font-bold ${isEarned ? 'text-indigo-600' : 'text-slate-400'}">${isEarned ? 'EARNED' : 'LOCKED'}</span>
                </div>
                <h3 class="font-bold text-slate-900 mb-1">${a.name}</h3>
                <p class="text-sm text-slate-500">${a.description}</p>
                ${isEarned && earnedRecord.earnedAt ? `<p class="text-xs text-indigo-500 mt-2">Earned ${new Date(earnedRecord.earnedAt).toLocaleDateString()}</p>` : ''}
            </div>
        `;
    }).join("");

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">Achievements</h1>
        <p class="text-slate-500 mb-6">${earned.length} of ${all.length} badges earned</p>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
            ${cardsHtml}
        </div>
    `;
}

loadAchievements();