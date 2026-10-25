auth.requireAuth();
renderTraineeLayout("competencies");

async function loadCompetencies() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading your competency data...</p>`;

    try {
        // All available competencies, so the trainee can pick a goal if they
        // don't already have one, or switch goals.
        const allCompetencies = await api.get("/public/competencies");

        let gapReport = null;
        let gapError = null;
        try {
            gapReport = await api.get("/trainees/gaps");
        } catch (err) {
            gapError = err.message;
        }

        renderPage(allCompetencies, gapReport, gapError);

    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load competencies: ${err.message}</p>`;
    }
}

function renderPage(allCompetencies, gapReport, gapError) {
    const content = document.getElementById("pageContent");

    const goalSelectorHtml = `
        <div class="bg-white border border-slate-200 rounded-xl p-6 mb-6">
            <div class="flex items-center justify-between flex-wrap gap-3">
                <div>
                    <h2 class="font-bold text-lg text-slate-900">Your Goal</h2>
                    <p class="text-sm text-slate-500">${gapReport ? gapReport.competencyName : "No goal competency set yet"}</p>
                </div>
                <div class="flex items-center gap-2">
                    <select id="goalSelect" class="px-3 py-2 border border-slate-300 rounded-lg text-sm">
                        <option value="">Select a target competency...</option>
                        ${allCompetencies.map(c => `<option value="${c.id}">${c.name}</option>`).join("")}
                    </select>
                    <button id="setGoalBtn" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Set Goal</button>
                </div>
            </div>
        </div>
    `;

    let gapContentHtml;

    if (gapError) {
        gapContentHtml = `
            <div class="bg-white border border-slate-200 rounded-xl p-10 text-center">
                <p class="text-slate-500">${gapError}</p>
                <p class="text-sm text-slate-400 mt-2">Pick a target competency above to see your skill gap analysis.</p>
            </div>
        `;
    } else {
        const biggestGap = gapReport.biggestGap;

        const gapRowsHtml = gapReport.skillGaps.map(g => {
            const barColor = g.hasGap ? "bg-red-500" : "bg-green-500";
            const isBiggest = biggestGap && biggestGap.skillId === g.skillId && g.hasGap;
            return `
                <div class="mb-4 ${isBiggest ? 'p-3 bg-red-50 rounded-lg border border-red-100' : ''}">
                    <div class="flex justify-between items-center text-sm mb-1">
                        <span class="font-medium text-slate-700">
                            ${isBiggest ? '<span class="text-red-600 font-bold">[Biggest Gap] </span>' : ''}
                            ${g.skillName}
                        </span>
                        <span class="text-slate-500">${g.currentScore}% / ${g.requiredScore}% required</span>
                    </div>
                    <div class="w-full bg-slate-200 rounded-full h-2.5 relative">
                        <div class="${barColor} h-2.5 rounded-full" style="width: ${g.currentScore}%"></div>
                    </div>
                    ${g.hasGap ? `<p class="text-xs text-red-600 mt-1">Gap: ${g.gap} points</p>` : `<p class="text-xs text-green-600 mt-1">Requirement met</p>`}
                </div>
            `;
        }).join("");

        gapContentHtml = `
            <div class="grid grid-cols-1 md:grid-cols-3 gap-5 mb-6">
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Overall Current</p>
                    <p class="text-3xl font-bold text-slate-900">${gapReport.overallCurrentScore}%</p>
                </div>
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Overall Required</p>
                    <p class="text-3xl font-bold text-slate-900">${gapReport.overallRequiredScore}%</p>
                </div>
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Overall Gap</p>
                    <p class="text-3xl font-bold ${gapReport.overallGap > 0 ? 'text-red-600' : 'text-green-600'}">${gapReport.overallGap}%</p>
                </div>
            </div>

            ${biggestGap && biggestGap.hasGap ? `
                <div class="bg-red-50 border border-red-200 rounded-xl p-5 mb-6 flex items-center justify-between flex-wrap gap-3">
                    <div>
                        <p class="text-sm font-semibold text-red-700">Skill Gap Detected</p>
                        <p class="text-slate-700">Your biggest competency gap is <strong>${biggestGap.skillName}</strong> (${biggestGap.gap} points below target).</p>
                    </div>
                    <div class="flex gap-2">
                        <a href="trainee-recommendations.html" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700 whitespace-nowrap">Improve This Skill</a>
                        <button onclick="viewTrainers(${biggestGap.skillId}, '${biggestGap.skillName.replace(/'/g, "\\'")}')"
                            class="px-4 py-2 bg-white border border-slate-300 text-slate-700 text-sm font-semibold rounded-lg hover:border-indigo-400 whitespace-nowrap">
                            Find a Trainer
                        </button>
                    </div>
                </div>
            ` : ''}

            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <h2 class="font-bold text-lg text-slate-900 mb-5">Skill-by-Skill Breakdown</h2>
                ${gapRowsHtml}
            </div>

            <div id="trainerResults" class="mt-6"></div>
        `;
    }

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">Competency Gap Analysis</h1>
        <p class="text-slate-500 mb-6">See exactly where you stand against your target role.</p>
        ${goalSelectorHtml}
        ${gapContentHtml}
    `;

    document.getElementById("setGoalBtn").addEventListener("click", async () => {
        const competencyId = document.getElementById("goalSelect").value;
        if (!competencyId) return;

        try {
            await api.post(`/trainees/goal/${competencyId}`, {});
            loadCompetencies();
        } catch (err) {
            alert("Failed to set goal: " + err.message);
        }
    });
}

async function viewTrainers(skillId, skillName) {
    const resultsBox = document.getElementById("trainerResults");
    resultsBox.innerHTML = `<p class="text-slate-500">Finding trainers for ${skillName}...</p>`;

    try {
        const trainers = await api.get(`/trainees/trainers/recommended/${skillId}?limit=3`);

        if (trainers.length === 0) {
            resultsBox.innerHTML = `<p class="text-slate-500">No matching trainers found for ${skillName} yet.</p>`;
            return;
        }

        resultsBox.innerHTML = `
            <h2 class="font-bold text-lg text-slate-900 mb-4">Recommended Trainers for ${skillName}</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                ${trainers.map((t, i) => `
                    <div class="bg-white border border-slate-200 rounded-xl p-5">
                        <div class="flex items-center justify-between mb-3">
                            <span class="font-bold text-slate-900">${t.trainerName}</span>
                            <span class="px-2.5 py-1 bg-indigo-100 text-indigo-700 text-xs font-bold rounded-full">${t.matchScore}% Match</span>
                        </div>
                        <p class="text-sm text-slate-600 mb-1">${t.skillMatched}: ${t.expertiseLevel}</p>
                        <p class="text-sm text-slate-600 mb-1">${t.yearsOfExperience} years experience</p>
                        <p class="text-sm text-slate-600 mb-3">Rating: ${t.averageRating}/5 (${t.totalRatings} reviews)</p>
                        <p class="text-xs text-slate-500 italic">${t.recommendedBecause}</p>
                    </div>
                `).join("")}
            </div>
        `;
    } catch (err) {
        resultsBox.innerHTML = `<p class="text-red-600">Failed to load trainers: ${err.message}</p>`;
    }
}

loadCompetencies();