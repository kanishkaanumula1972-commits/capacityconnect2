auth.requireAuth();
renderAdminLayout("skills");

async function loadSkills() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading skills...</p>`;

    try {
        const skills = await api.get("/public/skills");
        renderPage(skills);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load skills: ${err.message}</p>`;
    }
}

function renderPage(skills) {
    const content = document.getElementById("pageContent");

    const rowsHtml = skills.map(s => `
        <tr class="border-b border-slate-100">
            <td class="py-3 px-2 text-sm text-slate-500">${s.id}</td>
            <td class="py-3 px-2 text-sm font-medium text-slate-800">${s.name}</td>
            <td class="py-3 px-2 text-sm text-slate-500">${s.category || ''}</td>
            <td class="py-3 px-2 text-sm">
                <button onclick="deleteSkill(${s.id})" class="text-red-500 font-medium hover:underline">Delete</button>
            </td>
        </tr>
    `).join("");

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">Manage Skills</h1>
                <p class="text-slate-500">The master skill catalog used across competencies, courses, and profiles.</p>
            </div>
            <button onclick="openCreateModal()" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Add Skill</button>
        </div>
        <div class="bg-white border border-slate-200 rounded-xl p-6">
            <table class="w-full">
                <thead>
                    <tr class="border-b border-slate-200 text-left">
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">ID</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Name</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Category</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Actions</th>
                    </tr>
                </thead>
                <tbody>${rowsHtml}</tbody>
            </table>
        </div>
        <div id="modalRoot"></div>
    `;
}

function openCreateModal() {
    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Add New Skill</h2>
                <div class="space-y-3">
                    <input id="skName" type="text" placeholder="Skill name (e.g. Problem Solving)" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="skCategory" type="text" placeholder="Category (e.g. Soft Skill)" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="createSkill()" id="createSkBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Add</button>
                </div>
            </div>
        </div>
    `;
}

async function createSkill() {
    const name = document.getElementById("skName").value.trim();
    const category = document.getElementById("skCategory").value.trim();

    if (!name) {
        alert("Please enter a skill name.");
        return;
    }

    const btn = document.getElementById("createSkBtn");
    btn.disabled = true;
    btn.textContent = "Adding...";

    try {
        await api.post("/admin/skills", { name, category });
        closeModal();
        loadSkills();
    } catch (err) {
        alert("Failed to add skill: " + err.message);
        btn.disabled = false;
        btn.textContent = "Add";
    }
}

async function deleteSkill(id) {
    if (!confirm("Delete this skill? This may affect competencies/courses referencing it.")) return;
    try {
        await api.delete(`/admin/skills/${id}`);
        loadSkills();
    } catch (err) {
        alert("Failed to delete: " + err.message);
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadSkills();