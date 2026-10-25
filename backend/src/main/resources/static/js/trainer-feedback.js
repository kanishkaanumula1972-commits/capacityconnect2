auth.requireAuth();
renderTrainerLayout("feedback");

async function loadFeedback() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading feedback...</p>`;

    try {
        const myProfile = await api.get("/trainers/profile");
        const feedback = await api.get(`/feedback/trainer/${myProfile.id}`);
        renderPage(feedback, myProfile);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load feedback: ${err.message}</p>`;
    }
}

function renderPage(feedback, myProfile) {
    const content = document.getElementById("pageContent");

    const rowsHtml = feedback.length > 0
        ? feedback.map(f => `
            <div class="bg-white border border-slate-200 rounded-xl p-5 mb-3">
                <div class="flex items-center justify-between mb-2">
                    <div>
                        <p class="font-semibold text-slate-800">${f.traineeName}</p>
                        <p class="text-xs text-slate-500">${f.courseTitle}</p>
                    </div>
                    <span class="px-3 py-1 bg-amber-50 text-amber-700 text-sm font-bold rounded-full">${f.rating}/5</span>
                </div>
                ${f.comment ? `<p class="text-sm text-slate-600 mt-2">${f.comment}</p>` : ''}
                <div class="flex gap-4 text-xs text-slate-400 mt-3">
                    ${f.contentQuality ? `<span>Content: ${f.contentQuality}/5</span>` : ''}
                    ${f.trainerQuality ? `<span>Trainer: ${f.trainerQuality}/5</span>` : ''}
                    ${f.difficultyRating ? `<span>Difficulty: ${f.difficultyRating}/5</span>` : ''}
                </div>
            </div>
        `).join("")
        : `<p class="text-slate-400">No feedback received yet.</p>`;

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">Feedback</h1>
                <p class="text-slate-500">What trainees are saying about your courses.</p>
            </div>
            <div class="text-right">
                <p class="text-3xl font-bold text-slate-900">${myProfile.averageRating || 0}/5</p>
                <p class="text-xs text-slate-500">${myProfile.totalRatings || 0} total reviews</p>
            </div>
        </div>
        ${rowsHtml}
    `;
}

loadFeedback();