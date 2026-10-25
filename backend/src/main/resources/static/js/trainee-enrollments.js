auth.requireAuth();
renderTraineeLayout("my-courses");

async function loadEnrollments() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading your enrollments...</p>`;

    try {
        const enrollments = await api.get("/enrollments/my");
        renderList(enrollments);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load enrollments: ${err.message}</p>`;
    }
}

function renderList(enrollments) {
    const content = document.getElementById("pageContent");

    if (enrollments.length === 0) {
        content.innerHTML = `
            <h1 class="text-2xl font-bold text-slate-900 mb-1">My Enrollments</h1>
            <div class="bg-white border border-slate-200 rounded-xl p-10 text-center mt-6">
                <p class="text-slate-500">You haven't enrolled in any courses yet.</p>
                <a href="trainee-courses.html" class="text-indigo-600 text-sm font-medium hover:underline mt-2 inline-block">Browse courses &rarr;</a>
            </div>
        `;
        return;
    }

    const statusStyles = {
        ENROLLED: "bg-slate-100 text-slate-700",
        IN_PROGRESS: "bg-blue-100 text-blue-700",
        COMPLETED: "bg-green-100 text-green-700",
        DROPPED: "bg-red-100 text-red-700"
    };

    const rowsHtml = enrollments.map(e => `
        <div class="bg-white border border-slate-200 rounded-xl p-5 mb-4">
            <div class="flex items-start justify-between flex-wrap gap-3 mb-3">
                <div>
                    <h3 class="font-bold text-slate-900">${e.courseTitle}</h3>
                    <span class="inline-block mt-1 px-2.5 py-0.5 text-xs font-semibold rounded-full ${statusStyles[e.status] || 'bg-slate-100 text-slate-700'}">${e.status.replace('_', ' ')}</span>
                </div>
                <a href="trainee-course-detail.html?courseId=${e.courseId}" class="text-sm text-indigo-600 font-medium hover:underline whitespace-nowrap">View Course &rarr;</a>
            </div>
            <div class="flex justify-between text-sm mb-1">
                <span class="text-slate-500">Progress</span>
                <span class="text-slate-700 font-medium">${e.progressPercent}%</span>
            </div>
            <div class="w-full bg-slate-200 rounded-full h-2">
                <div class="bg-indigo-600 h-2 rounded-full" style="width: ${e.progressPercent}%"></div>
            </div>
            ${e.status !== 'COMPLETED' ? `
                <div class="flex items-center gap-2 mt-3">
                    <input type="range" min="0" max="100" value="${e.progressPercent}" class="progress-slider flex-1" data-course-id="${e.courseId}">
                    <button onclick="saveProgress(${e.courseId}, this)" class="px-3 py-1.5 bg-slate-800 text-white text-xs font-semibold rounded-lg hover:bg-slate-700">Update</button>
                </div>
            ` : ''}
        </div>
    `).join("");

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">My Enrollments</h1>
        <p class="text-slate-500 mb-6">Track your progress across all enrolled courses.</p>
        ${rowsHtml}
    `;
}

async function saveProgress(courseId, btn) {
    const slider = document.querySelector(`.progress-slider[data-course-id="${courseId}"]`);
    const value = slider.value;

    btn.disabled = true;
    btn.textContent = "Saving...";

    try {
        await api.put(`/enrollments/${courseId}/progress?progressPercent=${value}`, {});
        loadEnrollments();
    } catch (err) {
        alert("Failed to update progress: " + err.message);
        btn.disabled = false;
        btn.textContent = "Update";
    }
}

loadEnrollments();