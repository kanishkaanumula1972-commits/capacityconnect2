auth.requireAuth();
renderAdminLayout("competencies");

async function loadCompetencies() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading competencies...</p>`;

    try {
        const competencies = await api.get("/admin/competencies");
        renderPage(competencies);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load: ${err.message}</p>`;
    }
}

function renderPage(competencies) {
    const content = document.getElementById("pageContent");

    const cardsHtml = competencies.map(c => `
        <div class="bg-white border border-slate-200 rounded-xl p-5">
            <div class="flex items-center justify-between mb-1">
                <h3 class="font-bold text-slate-900">${c.name}</h3>
                <button onclick="deleteCompetency(${c.id})" class="text-xs text-red-500 hover:underline">Delete</button>
            </div>
            <p class="text-xs text-slate-500 mb-3">${c.category}</p>
            <p class="text-sm text-slate-600 mb-3">${c.description || ''}</p>
            <div class="flex flex-wrap gap-1.5">
                ${c.requiredSkills.map(s => `<span class="px-2 py-0.5 bg-indigo-50 text-indigo-700 text-xs font-medium rounded-full">${s.skillName}: ${s.requiredScore}%</span>`).join("")}
            </div>
        </div>
    `).join("");

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">Competencies</h1>
                <p class="text-slate-500">Manage the target competencies trainees can pursue.</p>
            </div>
            <button onclick="openCreateModal()" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Create Competency</button>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            ${cardsHtml}
        </div>
        <div id="modalRoot"></div>
    `;
}

let skillRows = 0;

function openCreateModal() {
    skillRows = 0;
    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50 p-4">
            <div class="bg-white rounded-xl p-6 w-full max-w-lg max-h-[85vh] overflow-y-auto">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Create Competency</h2>
                <div class="space-y-3 mb-4">
                    <input id="cName" type="text" placeholder="Competency name (e.g. Backend Developer)" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="cCategory" type="text" placeholder="Category" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <textarea id="cDescription" placeholder="Description" rows="2" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"></textarea>
                </div>
                <div class="flex items-center justify-between mb-2">
                    <h3 class="font-semibold text-sm text-slate-700">Required Skills</h3>
                    <button type="button" onclick="addSkillRow()" class="text-sm text-indigo-600 font-medium hover:underline">+ Add Skill</button>
                </div>
                                <p id="skillRefList" class="text-xs text-slate-400 mb-2">Loading skill list...</p>
                <div id="skillRowsContainer"></div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="createCompetency()" id="createCompBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Create</button>
                </div>
            </div>
        </div>
    `;
        api.get("/public/skills").then(skills => {
        const text = "Skill IDs: " + skills.map(s => `${s.id} ${s.name}`).join(", ");
        const el = document.getElementById("skillRefList");
        if (el) el.textContent = text;
    }).catch(() => {});
    addSkillRow();
    addSkillRow();
}

function addSkillRow() {
    skillRows++;
    const id = skillRows;
    const container = document.getElementById("skillRowsContainer");
    const row = document.createElement("div");
    row.className = "flex gap-2 mb-2";
    row.id = `skillRow-${id}`;
    row.innerHTML = `
        <input type="number" placeholder="Skill ID" class="skill-id-input w-24 px-3 py-2 border border-slate-300 rounded-lg text-sm">
        <input type="number" placeholder="Required %" class="skill-score-input flex-1 px-3 py-2 border border-slate-300 rounded-lg text-sm">
        <button type="button" onclick="document.getElementById('skillRow-${id}').remove()" class="px-3 text-red-500 text-sm">Remove</button>
    `;
    container.appendChild(row);
}

async function createCompetency() {
    const name = document.getElementById("cName").value.trim();
    const category = document.getElementById("cCategory").value.trim();
    const description = document.getElementById("cDescription").value.trim();

    const rows = document.querySelectorAll("#skillRowsContainer > div");
    const requiredSkills = [];
    rows.forEach(row => {
        const skillId = parseInt(row.querySelector(".skill-id-input").value);
        const requiredScore = parseFloat(row.querySelector(".skill-score-input").value);
        if (skillId && !isNaN(requiredScore)) {
            requiredSkills.push({ skillId, requiredScore });
        }
    });

    if (!name || requiredSkills.length === 0) {
        alert("Please provide a name and at least one required skill.");
        return;
    }

    const btn = document.getElementById("createCompBtn");
    btn.disabled = true;
    btn.textContent = "Creating...";

    try {
        await api.post("/admin/competencies", { name, category, description, requiredSkills });
        closeModal();
        loadCompetencies();
    } catch (err) {
        alert("Failed to create: " + err.message);
        btn.disabled = false;
        btn.textContent = "Create";
    }
}

async function deleteCompetency(id) {
    if (!confirm("Delete this competency?")) return;
    try {
        await api.delete(`/admin/competencies/${id}`);
        loadCompetencies();
    } catch (err) {
        alert("Failed to delete: " + err.message);
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadCompetencies();