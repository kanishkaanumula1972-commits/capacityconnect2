auth.requireAuth();
renderTrainerLayout("courses");

async function loadCourses() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading your courses...</p>`;

    try {
        const courses = await api.get("/courses/my");
        renderPage(courses);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load courses: ${err.message}</p>`;
    }
}

function renderPage(courses) {
    const content = document.getElementById("pageContent");

    const cardsHtml = courses.length > 0
        ? courses.map(c => `
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <span class="text-xs font-semibold text-indigo-600 uppercase">${c.category}</span>
                <h3 class="font-bold text-slate-900 mb-1">${c.title}</h3>
                <p class="text-sm text-slate-500 mb-3">${c.description}</p>
                <div class="flex items-center gap-3 text-xs text-slate-500 mb-4">
                    <span>${c.difficulty}</span>
                    <span>&middot;</span>
                    <span>${c.durationHours}h</span>
                    <span>&middot;</span>
                    <span>${c.enrolledCount} enrolled</span>
                    <span>&middot;</span>
                    <span>${c.averageRating > 0 ? c.averageRating + '/5' : 'No ratings'}</span>
                </div>
                <div class="flex gap-2">
                    <a href="trainer-course-edit.html?courseId=${c.id}" class="flex-1 text-center py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Manage</a>
                    <button onclick="deleteCourse(${c.id})" class="px-3 py-2 border border-red-200 text-red-600 text-sm font-semibold rounded-lg hover:bg-red-50">Delete</button>
                </div>
            </div>
        `).join("")
        : `<p class="text-slate-400 col-span-3">You haven't created any courses yet.</p>`;

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">My Courses</h1>
                <p class="text-slate-500">Manage your training programs.</p>
            </div>
            <button onclick="openCreateModal()" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Create Course</button>
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
            <div class="bg-white rounded-xl p-6 w-full max-w-lg">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Create New Course</h2>
                <div class="space-y-3">
                    <input id="cTitle" type="text" placeholder="Course title" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <textarea id="cDescription" placeholder="Description" rows="3" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"></textarea>
                    <div class="grid grid-cols-2 gap-3">
                        <input id="cCategory" type="text" placeholder="Category" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                        <select id="cDifficulty" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm bg-white">
                            <option value="BEGINNER">Beginner</option>
                            <option value="INTERMEDIATE">Intermediate</option>
                            <option value="ADVANCED">Advanced</option>
                        </select>
                    </div>
                    <input id="cDuration" type="number" step="0.5" placeholder="Duration (hours)" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="createCourse()" id="createBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Create</button>
                </div>
            </div>
        </div>
    `;
}

async function createCourse() {
    const btn = document.getElementById("createBtn");
    btn.disabled = true;
    btn.textContent = "Creating...";

    try {
        const course = await api.post("/courses", {
            title: document.getElementById("cTitle").value,
            description: document.getElementById("cDescription").value,
            category: document.getElementById("cCategory").value,
            difficulty: document.getElementById("cDifficulty").value,
            durationHours: parseFloat(document.getElementById("cDuration").value) || null
        });
        window.location.href = `trainer-course-edit.html?courseId=${course.id}`;
    } catch (err) {
        alert("Failed to create course: " + err.message);
        btn.disabled = false;
        btn.textContent = "Create";
    }
}

async function deleteCourse(id) {
    if (!confirm("Delete this course? This cannot be undone.")) return;
    try {
        await api.delete(`/courses/${id}`);
        loadCourses();
    } catch (err) {
        alert("Failed to delete: " + err.message);
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadCourses();