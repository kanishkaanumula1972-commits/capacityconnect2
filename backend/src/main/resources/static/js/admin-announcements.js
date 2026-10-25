auth.requireAuth();
renderAdminLayout("announcements");

async function loadAnnouncements() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading announcements...</p>`;

    try {
        const announcements = await api.get("/public/announcements");
        renderPage(announcements);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load: ${err.message}</p>`;
    }
}

function renderPage(announcements) {
    const content = document.getElementById("pageContent");

    const rowsHtml = announcements.length > 0
        ? announcements.map(a => `
            <div class="bg-white border border-slate-200 rounded-xl p-5 mb-3">
                <div class="flex items-center justify-between mb-1">
                    <h3 class="font-bold text-slate-900">${a.title}</h3>
                    <span class="text-xs text-slate-400">${new Date(a.createdAt).toLocaleDateString()}</span>
                </div>
                <p class="text-sm text-slate-600">${a.content}</p>
                <p class="text-xs text-slate-400 mt-2">by ${a.publishedByName} &middot; ${a.type}</p>
            </div>
        `).join("")
        : `<p class="text-slate-400">No announcements published yet.</p>`;

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">Announcements</h1>
                <p class="text-slate-500">Publish platform-wide announcements visible to everyone.</p>
            </div>
            <button onclick="openCreateModal()" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Publish Announcement</button>
        </div>
        ${rowsHtml}
        <div id="modalRoot"></div>
    `;
}

function openCreateModal() {
    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Publish Announcement</h2>
                <div class="space-y-3">
                    <input id="anTitle" type="text" placeholder="Title" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <textarea id="anContent" placeholder="Content" rows="4" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"></textarea>
                    <select id="anType" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm bg-white">
                        <option value="GENERAL">General</option>
                        <option value="NEW_COURSE">New Course</option>
                        <option value="ACHIEVEMENT">Achievement</option>
                        <option value="TRAINING_PROGRAM">Training Program</option>
                        <option value="NEW_CONTENT">New Content</option>
                    </select>
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="publishAnnouncement()" id="publishAnBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Publish</button>
                </div>
            </div>
        </div>
    `;
}

async function publishAnnouncement() {
    const title = document.getElementById("anTitle").value.trim();
    const content = document.getElementById("anContent").value.trim();
    const type = document.getElementById("anType").value;

    if (!title || !content) {
        alert("Please provide a title and content.");
        return;
    }

    const btn = document.getElementById("publishAnBtn");
    btn.disabled = true;
    btn.textContent = "Publishing...";

    try {
        await api.post("/admin/announcements", { title, content, type });
        closeModal();
        loadAnnouncements();
    } catch (err) {
        alert("Failed to publish: " + err.message);
        btn.disabled = false;
        btn.textContent = "Publish";
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadAnnouncements();