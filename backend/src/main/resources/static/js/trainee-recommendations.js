auth.requireAuth();
renderTraineeLayout("competencies"); // reuse the "competencies" nav highlight since this flows from that page

async function loadRecommendations() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Building your personalized learning path...</p>`;

    try {
        const path = await api.get("/trainees/recommendations");
        renderPath(path);
    } catch (err) {
        content.innerHTML = `
            <h1 class="text-2xl font-bold text-slate-900 mb-1">Recommended Learning Path</h1>
            <div class="bg-white border border-slate-200 rounded-xl p-10 text-center mt-6">
                <p class="text-slate-500">${err.message}</p>
                <a href="trainee-competencies.html" class="text-indigo-600 text-sm font-medium hover:underline mt-2 inline-block">Go set a goal competency &rarr;</a>
            </div>
        `;
    }
}

function renderPath(path) {
    const content = document.getElementById("pageContent");

    if (path.recommendedCourses.length === 0) {
        content.innerHTML = `
            <h1 class="text-2xl font-bold text-slate-900 mb-1">Recommended Learning Path</h1>
            <p class="text-slate-500 mb-6">Goal: ${path.goalCompetencyName}</p>
            <div class="bg-green-50 border border-green-200 rounded-xl p-8 text-center">
                <p class="text-green-700 font-semibold">No detected gaps right now.</p>
                <p class="text-sm text-slate-600 mt-1">You're meeting the requirements for this competency. Nice work.</p>
            </div>
        `;
        return;
    }

    const gapTagsHtml = path.detectedGapSkillNames.map(name =>
        `<span class="px-3 py-1 bg-red-50 text-red-700 text-xs font-semibold rounded-full border border-red-200">${name}</span>`
    ).join("");

    const courseCardsHtml = path.recommendedCourses.map((c, i) => `
        <div class="bg-white border border-slate-200 rounded-xl p-6 mb-4">
            <div class="flex items-start justify-between flex-wrap gap-3 mb-3">
                <div>
                    <span class="text-xs font-bold text-indigo-600 uppercase tracking-wide">Step ${i + 1}</span>
                    <h3 class="font-bold text-lg text-slate-900">${c.courseTitle}</h3>
                    <p class="text-sm text-slate-500">by ${c.trainerName} &middot; ${c.difficulty} &middot; ${c.durationHours} hours</p>
                </div>
                <button onclick="enroll(${c.courseId}, this)"
                    class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700 whitespace-nowrap">
                    Enroll
                </button>
            </div>
            <div class="flex items-center gap-4 text-sm mb-3">
                <span class="text-slate-600">Skill gained: <strong>${c.skillGained}</strong></span>
                <span class="text-slate-600">Current gap: <strong>${c.currentGap}</strong></span>
                <span class="text-slate-600">Expected improvement: <strong>+${c.expectedImprovement}</strong></span>
            </div>
            <p class="text-sm text-slate-600 bg-slate-50 border border-slate-200 rounded-lg p-3">
                <strong>Recommended because:</strong> ${c.recommendedBecause}
            </p>
        </div>
    `).join("");

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">Recommended Learning Path</h1>
        <p class="text-slate-500 mb-4">Your Goal: <strong>${path.goalCompetencyName}</strong></p>
        <div class="flex flex-wrap gap-2 mb-6">
            <span class="text-sm text-slate-500 mr-1">Detected gaps:</span>
            ${gapTagsHtml}
        </div>
        ${courseCardsHtml}
    `;
}

async function enroll(courseId, btn) {
    btn.disabled = true;
    btn.textContent = "Enrolling...";

    try {
        await api.post(`/enrollments/${courseId}`, {});
        btn.textContent = "Enrolled";
        btn.classList.remove("bg-indigo-600", "hover:bg-indigo-700");
        btn.classList.add("bg-green-600");
    } catch (err) {
        alert("Failed to enroll: " + err.message);
        btn.disabled = false;
        btn.textContent = "Enroll";
    }
}

loadRecommendations();