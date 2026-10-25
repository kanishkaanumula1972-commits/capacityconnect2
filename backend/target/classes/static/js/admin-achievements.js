auth.requireAuth();
renderAdminLayout("achievements");

async function loadAchievements() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading achievements...</p>`;

    try {
        const achievements = await api.get("/public/achievements");
        renderPage(achievements);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load: ${err.message}</p>`;
    }
}

function renderPage(achievements) {
    const content = document.getElementById("pageContent");

    const cardsHtml = achievements.map(a => `
        <div class="bg-white border border-slate-200 rounded-xl p-5">
            <div class="flex items-center justify-between mb-1">
                <h3 class="font-bold text-slate-900">${a.name}</h3>
                <button onclick="deleteAchievement(${a.id})" class="text-xs text-red-500 hover:underline">Delete</button>
            </div>
            <p class="text-sm text-slate-500 mb-2">${a.description}</p>
            <span class="px-2 py-0.5 bg-slate-100 text-slate-600 text-xs font-medium rounded-full">${a.criteria.replace(/_/g, ' ')} &ge; ${a.criteriaThreshold}</span>
        </div>
    `).join("");

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">Achievements</h1>
                <p class="text-slate-500">Manage the badge catalog trainees can earn.</p>
            </div>
            <button onclick="openCreateModal()" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Create Achievement</button>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
            ${cardsHtml}
        </div>
        <div id="modalRoot"></div>
    `;
}

function openCreateModal() {
    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Create Achievement</h2>
                <div class="space-y-3">
                    <input id="aName" type="text" placeholder="Achievement name" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <textarea id="aDescription" placeholder="Description" rows="2" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"></textarea>
                    <select id="aCriteria" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm bg-white">
                        <option value="SKILL_MASTERY">Skill Mastery (score threshold)</option>
                        <option value="ASSESSMENT_HIGH_SCORE">Assessment High Score</option>
                        <option value="COURSE_COMPLETION_COUNT">Course Completion Count</option>
                        <option value="SKILL_IMPROVEMENT">Skill Improvement Amount</option>
                    </select>
                    <input id="aThreshold" type="number" placeholder="Threshold value" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="createAchievement()" id="createAchBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Create</button>
                </div>
            </div>
        </div>
    `;
}

async function createAchievement() {
    const name = document.getElementById("aName").value.trim();
    const description = document.getElementById("aDescription").value.trim();
    const criteria = document.getElementById("aCriteria").value;
    const criteriaThreshold = parseFloat(document.getElementById("aThreshold").value);

    if (!name || isNaN(criteriaThreshold)) {
        alert("Please provide a name and threshold value.");
        return;
    }

    const btn = document.getElementById("createAchBtn");
    btn.disabled = true;
    btn.textContent = "Creating...";

    try {
        await api.post("/admin/achievements", { name, description, criteria, criteriaThreshold, icon: "badge-custom" });
        closeModal();
        loadAchievements();
    } catch (err) {
        alert("Failed to create: " + err.message);
        btn.disabled = false;
        btn.textContent = "Create";
    }
}

async function deleteAchievement(id) {
    if (!confirm("Delete this achievement?")) return;
    try {
        await api.delete(`/admin/achievements/${id}`);
        loadAchievements();
    } catch (err) {
        alert("Failed to delete: " + err.message);
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadAchievements();