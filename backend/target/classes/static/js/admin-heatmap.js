auth.requireAuth();
renderAdminLayout("heatmap");

let myCourses = []; // used for training program creation

async function loadHeatmap() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading organizational heatmap...</p>`;

    try {
        const heatmap = await api.get("/admin/competency-heatmap?topGapsLimit=6");
        renderPage(heatmap);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load heatmap: ${err.message}</p>`;
    }
}

function renderPage(heatmap) {
    const content = document.getElementById("pageContent");

    // Sort skill health descending by score for a clean bar-chart feel
    const sortedHealth = [...heatmap.skillHealth].sort((a, b) => b.averageScore - a.averageScore);

    const healthRowsHtml = sortedHealth.map(s => {
        const barColor = s.averageScore >= 70 ? "bg-green-500" : s.averageScore >= 45 ? "bg-amber-500" : "bg-red-500";
        return `
            <div class="mb-3">
                <div class="flex justify-between text-sm mb-1">
                    <span class="font-medium text-slate-700">${s.skillName}</span>
                    <span class="text-slate-500">${s.averageScore}% &middot; ${s.traineesTracked} tracked</span>
                </div>
                <div class="w-full bg-slate-200 rounded-full h-2.5">
                    <div class="${barColor} h-2.5 rounded-full" style="width: ${s.averageScore}%"></div>
                </div>
            </div>
        `;
    }).join("");

    const gapsHtml = heatmap.topSkillGaps.map((g, i) => `
        <div class="bg-white border border-red-200 rounded-xl p-5 cursor-pointer hover:shadow-md transition" onclick="drillDown(${g.skillId}, '${g.skillName.replace(/'/g, "\\'")}')">
            <div class="flex items-center justify-between mb-2">
                <span class="font-bold text-slate-900">${i + 1}. ${g.skillName}</span>
                <span class="px-2.5 py-1 bg-red-100 text-red-700 text-xs font-bold rounded-full">${g.gap} gap</span>
            </div>
            <p class="text-sm text-slate-500">${g.traineesAffected} trainees affected</p>
            <p class="text-xs text-slate-400 mt-1">Average ${g.averageScore}% &middot; Required ${g.requiredScore}%</p>
        </div>
    `).join("");

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">Organizational Competency Heatmap</h1>
        <p class="text-slate-500 mb-8">Real-time skill health across the entire organization.</p>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Skill Health</h2>
                ${healthRowsHtml}
            </div>
            <div>
                <h2 class="font-bold text-lg text-slate-900 mb-4">Top Skill Gaps</h2>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    ${gapsHtml}
                </div>
            </div>
        </div>

        <div id="drilldownRoot"></div>
        <div id="modalRoot"></div>
    `;
}

async function drillDown(skillId, skillName) {
    const box = document.getElementById("drilldownRoot");
    box.innerHTML = `<p class="text-slate-500">Loading detail for ${skillName}...</p>`;

    try {
        const detail = await api.get(`/admin/competency-gaps/${skillId}`);

        box.innerHTML = `
            <div class="bg-indigo-50 border border-indigo-200 rounded-xl p-6">
                <h2 class="font-bold text-lg text-indigo-900 mb-4">${detail.skillName}</h2>
                <div class="grid grid-cols-3 gap-4 mb-5">
                    <div>
                        <p class="text-xs text-slate-500 mb-1">Trainees Affected</p>
                        <p class="text-2xl font-bold text-slate-900">${detail.traineesAffected}</p>
                    </div>
                    <div>
                        <p class="text-xs text-slate-500 mb-1">Average</p>
                        <p class="text-2xl font-bold text-slate-900">${detail.averageScore}%</p>
                    </div>
                    <div>
                        <p class="text-xs text-slate-500 mb-1">Required</p>
                        <p class="text-2xl font-bold text-slate-900">${detail.requiredScore}%</p>
                    </div>
                </div>
                <button onclick="openTrainingProgramModal(${detail.skillId}, '${detail.skillName.replace(/'/g, "\\'")}')" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Create Training Program</button>
            </div>
        `;
    } catch (err) {
        box.innerHTML = `<p class="text-red-600">Failed to load detail: ${err.message}</p>`;
    }
}

async function openTrainingProgramModal(skillId, skillName) {
    if (myCourses.length === 0) {
        try {
            myCourses = await api.get("/courses");
        } catch (e) {
            alert("Failed to load courses: " + e.message);
            return;
        }
    }

    const courseOptionsHtml = myCourses.map(c => `<option value="${c.id}">${c.title}</option>`).join("");

    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50 p-4">
            <div class="bg-white rounded-xl p-6 w-full max-w-lg">
                <h2 class="font-bold text-lg text-slate-900 mb-1">Create Training Program</h2>
                <p class="text-sm text-slate-500 mb-4">Targeting the ${skillName} skill gap</p>

                <div class="space-y-3">
                    <select id="tpCourse" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm bg-white">
                        <option value="">Select a course...</option>
                        ${courseOptionsHtml}
                    </select>
                    <input id="tpTraineeIds" type="text" placeholder="Trainee user IDs, comma-separated (e.g. 6,7,8)" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <p class="text-xs text-slate-400">Enter the trainee user IDs to notify. See Manage Users for IDs.</p>
                    <input id="tpDeadline" type="date" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <textarea id="tpMessage" placeholder="Custom message (optional)" rows="2" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"></textarea>
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="publishTrainingProgram()" id="publishBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Publish</button>
                </div>
            </div>
        </div>
    `;
}

async function publishTrainingProgram() {
    const courseId = document.getElementById("tpCourse").value;
    const traineeIdsRaw = document.getElementById("tpTraineeIds").value.trim();
    const deadline = document.getElementById("tpDeadline").value;
    const customMessage = document.getElementById("tpMessage").value.trim();

    if (!courseId || !traineeIdsRaw) {
        alert("Please select a course and enter at least one trainee ID.");
        return;
    }

    const targetTraineeUserIds = traineeIdsRaw.split(",").map(s => parseInt(s.trim())).filter(n => !isNaN(n));

    const btn = document.getElementById("publishBtn");
    btn.disabled = true;
    btn.textContent = "Publishing...";

    try {
        await api.post("/admin/training-programs", {
            courseId: parseInt(courseId),
            targetTraineeUserIds,
            deadline: deadline ? deadline + "T23:59:59" : null,
            customMessage: customMessage || null
        });
        alert("Training program published and notifications sent.");
        closeModal();
    } catch (err) {
        alert("Failed to publish: " + err.message);
        btn.disabled = false;
        btn.textContent = "Publish";
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadHeatmap();