auth.requireAuth();
renderTrainerLayout("resources");

let myCourses = [];

async function loadResources() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading resources...</p>`;

    try {
        const courses = await api.get("/courses/my");
        myCourses = courses;

        // Pull resources for each of the trainer's courses and flatten into one list
        const resourceLists = await Promise.all(
            courses.map(c => api.get(`/resources/course/${c.id}`))
        );
        const allResources = resourceLists.flat();

        renderPage(allResources);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load resources: ${err.message}</p>`;
    }
}

function renderPage(resources) {
    const content = document.getElementById("pageContent");

    const courseOptionsHtml = myCourses.map(c => `<option value="${c.id}">${c.title}</option>`).join("");

    const rowsHtml = resources.length > 0
        ? resources.map(r => `
            <div class="flex items-center justify-between px-5 py-4 border border-slate-200 rounded-lg mb-2 bg-white">
                <div>
                    <span class="text-xs font-semibold text-indigo-600 uppercase mr-2">${r.type}</span>
                    <span class="text-sm font-medium text-slate-800">${r.title}</span>
                    <p class="text-xs text-slate-500 mt-0.5">${r.courseTitle} ${r.topic ? '&middot; ' + r.topic : ''}</p>
                </div>
                <div class="flex items-center gap-3">
                    <a href="http://localhost:8080${r.fileUrl}" target="_blank" class="text-xs text-indigo-600 font-medium hover:underline">View</a>
                    <button onclick="deleteResource(${r.id})" class="text-xs text-red-500 font-medium hover:underline">Delete</button>
                </div>
            </div>
        `).join("")
        : `<p class="text-slate-400">No learning resources uploaded yet.</p>`;

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">Learning Resources</h1>
                <p class="text-slate-500">All resources across your courses.</p>
            </div>
            <button onclick="openUploadModal()" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Upload Resource</button>
        </div>
        ${rowsHtml}
        <div id="modalRoot"></div>
    `;
}

function openUploadModal() {
    if (myCourses.length === 0) {
        alert("You need to create a course first before uploading resources.");
        return;
    }

    const courseOptionsHtml = myCourses.map(c => `<option value="${c.id}">${c.title}</option>`).join("");

    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Upload Learning Resource</h2>
                <div class="space-y-3">
                    <select id="rCourse" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm bg-white">
                        ${courseOptionsHtml}
                    </select>
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
    const courseId = document.getElementById("rCourse").value;
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
        loadResources();
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
        loadResources();
    } catch (err) {
        alert("Failed to delete: " + err.message);
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadResources();