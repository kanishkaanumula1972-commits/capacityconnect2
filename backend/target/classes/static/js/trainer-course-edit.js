auth.requireAuth();
renderTrainerLayout("courses");

const urlParams = new URLSearchParams(window.location.search);
const courseId = urlParams.get("courseId");

let currentCourse = null;

async function loadCourse() {
    const content = document.getElementById("pageContent");

    if (!courseId) {
        content.innerHTML = `<p class="text-red-600">No course specified.</p>`;
        return;
    }

    content.innerHTML = `<p class="text-slate-500">Loading course...</p>`;

    try {
        const [course, resources, assessments] = await Promise.all([
            api.get(`/courses/${courseId}`),
            api.get(`/resources/course/${courseId}`),
            api.get(`/assessments/course/${courseId}`)
        ]);
        currentCourse = course;
        renderPage(course, resources, assessments);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load course: ${err.message}</p>`;
    }
}

function renderPage(course, resources, assessments) {
    const content = document.getElementById("pageContent");

    const skillsHtml = course.skillsCovered.length > 0
        ? course.skillsCovered.map(s => `<span class="px-2.5 py-1 bg-indigo-50 text-indigo-700 text-xs font-medium rounded-full mr-1 mb-1 inline-block">${s.skillName} (+${s.expectedImprovement})</span>`).join("")
        : `<p class="text-sm text-slate-400">No skills tagged yet.</p>`;

    const competenciesHtml = course.competencyNames.length > 0
        ? course.competencyNames.map(n => `<span class="px-2.5 py-1 bg-slate-100 text-slate-700 text-xs font-medium rounded-full mr-1 mb-1 inline-block">${n}</span>`).join("")
        : `<p class="text-sm text-slate-400">No competencies tagged yet.</p>`;

    const resourcesHtml = resources.length > 0
        ? resources.map(r => `
            <div class="flex items-center justify-between px-4 py-3 border border-slate-200 rounded-lg mb-2">
                <div>
                    <span class="text-xs font-semibold text-indigo-600 uppercase mr-2">${r.type}</span>
                    <span class="text-sm font-medium text-slate-800">${r.title}</span>
                </div>
                <button onclick="deleteResource(${r.id})" class="text-xs text-red-500 hover:underline">Delete</button>
            </div>
        `).join("")
        : `<p class="text-sm text-slate-400">No resources uploaded yet.</p>`;

    const assessmentsHtml = assessments.length > 0
        ? assessments.map(a => `
            <div class="flex items-center justify-between px-4 py-3 border border-slate-200 rounded-lg mb-2">
                <div>
                    <p class="text-sm font-medium text-slate-800">${a.title}</p>
                    <p class="text-xs text-slate-500">${a.questions.length} questions targeting ${a.targetSkillName || 'no specific skill'}</p>
                </div>
                <a href="trainer-assessments.html" class="text-xs text-indigo-600 hover:underline">Manage &rarr;</a>
            </div>
        `).join("")
        : `<p class="text-sm text-slate-400">No assessments yet. <a href="trainer-assessments.html" class="text-indigo-600 hover:underline">Create one</a></p>`;

    content.innerHTML = `
        <div class="mb-6">
            <a href="trainer-courses.html" class="text-sm text-indigo-600 hover:underline">&larr; Back to My Courses</a>
            <h1 class="text-2xl font-bold text-slate-900 mt-2">${course.title}</h1>
            <p class="text-slate-500">${course.category} &middot; ${course.difficulty} &middot; ${course.enrolledCount} enrolled</p>
        </div>

        <div class="bg-white border border-slate-200 rounded-xl p-6 mb-6">
            <h2 class="font-bold text-lg text-slate-900 mb-3">Skills Covered</h2>
            <div class="mb-4">${skillsHtml}</div>
            <div class="flex gap-2 mb-4">
                <input id="skillIdInput" type="number" placeholder="Skill ID" class="w-28 px-3 py-2 border border-slate-300 rounded-lg text-sm">
                <input id="improvementInput" type="number" placeholder="Expected improvement" class="w-40 px-3 py-2 border border-slate-300 rounded-lg text-sm">
                <button onclick="addSkillTag()" class="px-4 py-2 bg-slate-800 text-white text-sm font-semibold rounded-lg hover:bg-slate-700">Add Skill Tag</button>
            </div>
            <p id="skillRefList" class="text-xs text-slate-400">Loading skill list...</p>

            <h2 class="font-bold text-lg text-slate-900 mt-6 mb-3">Competencies Covered</h2>
            <div>${competenciesHtml}</div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="font-bold text-lg text-slate-900">Learning Resources</h2>
                    <button onclick="openResourceModal()" class="text-sm text-indigo-600 font-medium hover:underline">+ Upload</button>
                </div>
                ${resourcesHtml}
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Assessments</h2>
                ${assessmentsHtml}
            </div>
        </div>

        <div id="modalRoot"></div>
    `;

    loadSkillReference();
}

async function loadSkillReference() {
    try {
        const skills = await api.get("/public/skills");
        const text = "Skill IDs: " + skills.map(s => `${s.id} ${s.name}`).join(", ");
        const el = document.getElementById("skillRefList");
        if (el) el.textContent = text;
    } catch (e) { /* non-critical, ignore */ }
}

async function addSkillTag() {
    const skillId = parseInt(document.getElementById("skillIdInput").value);
    const improvement = parseFloat(document.getElementById("improvementInput").value) || 20;

    if (!skillId) {
        alert("Please enter a valid skill ID.");
        return;
    }

    const existingSkillIds = currentCourse.skillsCovered.map(s => s.skillId);
    if (existingSkillIds.includes(skillId)) {
        alert("This skill is already tagged on this course.");
        return;
    }

    const updatedSkills = currentCourse.skillsCovered.map(s => ({ skillId: s.skillId, expectedImprovement: s.expectedImprovement }));
    updatedSkills.push({ skillId, expectedImprovement: improvement });

    try {
        await api.put(`/courses/${courseId}`, {
            title: currentCourse.title,
            description: currentCourse.description,
            category: currentCourse.category,
            difficulty: currentCourse.difficulty,
            durationHours: currentCourse.durationHours,
            skillsCovered: updatedSkills,
            competencyIds: currentCourse.competencyIds
        });
        loadCourse();
    } catch (err) {
        alert("Failed to add skill tag: " + err.message);
    }
}

function openResourceModal() {
    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Upload Learning Resource</h2>
                <div class="space-y-3">
                    <input id="rTitle" type="text" placeholder="Resource title" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <select id="rType" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm bg-white">
                        <option value="RECORDED_LECTURE">Recorded Lecture</option>
                        <option value="VIDEO">Video</option>
                        <option value="PDF">PDF</option>
                        <option value="PPT">PPT</option>
                        <option value="DOCUMENT">Document</option>
                        <option value="ASSIGNMENT">Assignment</option>
                    </select>
                    <input id="rTopic" type="text" placeholder="Topic (optional)" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="rFile" type="file" class="w-full text-sm">
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="uploadResource()" id="uploadResBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Upload</button>
                </div>
            </div>
        </div>
    `;
}

async function uploadResource() {
    const title = document.getElementById("rTitle").value.trim();
    const type = document.getElementById("rType").value;
    const topic = document.getElementById("rTopic").value.trim();
    const fileInput = document.getElementById("rFile");

    if (!title || !fileInput.files[0]) {
        alert("Please provide a title and choose a file.");
        return;
    }

    const btn = document.getElementById("uploadResBtn");
    btn.disabled = true;
    btn.textContent = "Uploading...";

    const formData = new FormData();
    formData.append("courseId", courseId);
    formData.append("title", title);
    formData.append("type", type);
    if (topic) formData.append("topic", topic);
    formData.append("file", fileInput.files[0]);

    try {
        await api.postForm("/resources", formData);
        closeModal();
        loadCourse();
    } catch (err) {
        alert("Failed to upload resource: " + err.message);
        btn.disabled = false;
        btn.textContent = "Upload";
    }
}

async function deleteResource(id) {
    if (!confirm("Delete this resource?")) return;
    try {
        await api.delete(`/resources/${id}`);
        loadCourse();
    } catch (err) {
        alert("Failed to delete: " + err.message);
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadCourse();