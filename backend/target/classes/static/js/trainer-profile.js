auth.requireAuth();
renderTrainerLayout("profile");

let profileData = null;

async function loadProfile() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading your profile...</p>`;

    try {
        profileData = await api.get("/trainers/profile");
        renderPage();
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load profile: ${err.message}</p>`;
    }
}

function renderPage() {
    const content = document.getElementById("pageContent");
    const p = profileData;

    const qualificationsHtml = p.qualifications.length > 0
        ? p.qualifications.map(q => `
            <div class="flex items-center justify-between px-4 py-3 border border-slate-200 rounded-lg mb-2">
                <div>
                    <p class="text-sm font-medium text-slate-800">${q.degree} &middot; ${q.institution}</p>
                    <p class="text-xs text-slate-500">${q.specialization || ''} ${q.year ? '(' + q.year + ')' : ''}</p>
                </div>
                <button onclick="deleteQualification(${q.id})" class="text-xs text-red-500 hover:underline">Remove</button>
            </div>
        `).join("")
        : `<p class="text-sm text-slate-400">No qualifications added yet.</p>`;

    const experienceHtml = p.workExperiences.length > 0
        ? p.workExperiences.map(w => `
            <div class="flex items-center justify-between px-4 py-3 border border-slate-200 rounded-lg mb-2">
                <div>
                    <p class="text-sm font-medium text-slate-800">${w.role} &middot; ${w.organization}</p>
                    <p class="text-xs text-slate-500">${w.yearsOfExperience || 0} years</p>
                </div>
                <button onclick="deleteExperience(${w.id})" class="text-xs text-red-500 hover:underline">Remove</button>
            </div>
        `).join("")
        : `<p class="text-sm text-slate-400">No work experience added yet.</p>`;

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">My Profile</h1>
        <p class="text-slate-500 mb-6">${p.fullName} &middot; ${p.email} &middot; ${p.averageRating || 0}/5 rating (${p.totalRatings || 0} reviews)</p>

        <div class="bg-white border border-slate-200 rounded-xl p-6 mb-6">
            <h2 class="font-bold text-lg text-slate-900 mb-4">Personal Information</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">Phone</label>
                    <input id="phoneInput" type="text" value="${p.phone || ''}" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">Location</label>
                    <input id="locationInput" type="text" value="${p.location || ''}" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">Years of Experience</label>
                    <input id="yearsInput" type="number" value="${p.yearsOfExperience || ''}" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">Subjects (comma-separated)</label>
                    <input id="subjectsInput" type="text" value="${p.subjects.join(', ')}" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                </div>
            </div>
            <div class="mb-4">
                <label class="block text-sm font-medium text-slate-700 mb-1">Bio</label>
                <textarea id="bioInput" rows="3" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">${p.bio || ''}</textarea>
            </div>
            <button onclick="saveBasicInfo()" id="saveBtn" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Save Changes</button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="font-bold text-lg text-slate-900">Qualifications</h2>
                    <button onclick="openQualModal()" class="text-sm text-indigo-600 font-medium hover:underline">+ Add</button>
                </div>
                ${qualificationsHtml}
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="font-bold text-lg text-slate-900">Work Experience</h2>
                    <button onclick="openExpModal()" class="text-sm text-indigo-600 font-medium hover:underline">+ Add</button>
                </div>
                ${experienceHtml}
            </div>
        </div>

        <div id="modalRoot"></div>
    `;
}

async function saveBasicInfo() {
    const btn = document.getElementById("saveBtn");
    btn.disabled = true;
    btn.textContent = "Saving...";

    const subjects = document.getElementById("subjectsInput").value
        .split(",").map(s => s.trim()).filter(s => s.length > 0);

    try {
        await api.put("/trainers/profile", {
            phone: document.getElementById("phoneInput").value,
            location: document.getElementById("locationInput").value,
            bio: document.getElementById("bioInput").value,
            yearsOfExperience: parseInt(document.getElementById("yearsInput").value) || null,
            subjects
        });
        btn.textContent = "Saved";
        setTimeout(() => { btn.disabled = false; btn.textContent = "Save Changes"; }, 1500);
    } catch (err) {
        alert("Failed to save: " + err.message);
        btn.disabled = false;
        btn.textContent = "Save Changes";
    }
}

function openQualModal() {
    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Add Qualification</h2>
                <div class="space-y-3">
                    <input id="qDegree" type="text" placeholder="Degree" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="qInstitution" type="text" placeholder="Institution" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="qYear" type="number" placeholder="Year" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="qSpecialization" type="text" placeholder="Specialization" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="saveQualification()" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Save</button>
                </div>
            </div>
        </div>
    `;
}

async function saveQualification() {
    try {
        await api.post("/trainers/profile/qualifications", {
            degree: document.getElementById("qDegree").value,
            institution: document.getElementById("qInstitution").value,
            year: parseInt(document.getElementById("qYear").value) || null,
            specialization: document.getElementById("qSpecialization").value
        });
        closeModal();
        loadProfile();
    } catch (err) {
        alert("Failed to add qualification: " + err.message);
    }
}

async function deleteQualification(id) {
    if (!confirm("Remove this qualification?")) return;
    await api.delete(`/trainers/profile/qualifications/${id}`);
    loadProfile();
}

function openExpModal() {
    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Add Work Experience</h2>
                <div class="space-y-3">
                    <input id="wOrg" type="text" placeholder="Organization" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="wRole" type="text" placeholder="Role" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="wYears" type="number" step="0.5" placeholder="Years of experience" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <textarea id="wResponsibilities" placeholder="Responsibilities" rows="3" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"></textarea>
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="saveExperience()" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Save</button>
                </div>
            </div>
        </div>
    `;
}

async function saveExperience() {
    try {
        await api.post("/trainers/profile/experience", {
            organization: document.getElementById("wOrg").value,
            role: document.getElementById("wRole").value,
            yearsOfExperience: parseFloat(document.getElementById("wYears").value) || null,
            responsibilities: document.getElementById("wResponsibilities").value
        });
        closeModal();
        loadProfile();
    } catch (err) {
        alert("Failed to add work experience: " + err.message);
    }
}

async function deleteExperience(id) {
    if (!confirm("Remove this work experience?")) return;
    await api.delete(`/trainers/profile/experience/${id}`);
    loadProfile();
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadProfile();