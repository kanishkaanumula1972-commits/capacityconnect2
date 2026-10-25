auth.requireAuth();
renderTraineeLayout("courses");

let allCourses = [];
let myEnrolledCourseIds = new Set();

async function loadCourses() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading courses...</p>`;

    try {
        const [courses, enrollments] = await Promise.all([
            api.get("/courses"),
            api.get("/enrollments/my")
        ]);

        allCourses = courses;
        myEnrolledCourseIds = new Set(enrollments.map(e => e.courseId));

        renderPage();
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load courses: ${err.message}</p>`;
    }
}

function renderPage() {
    const content = document.getElementById("pageContent");

    const categories = ["All", ...new Set(allCourses.map(c => c.category))];
    const filterHtml = categories.map(cat =>
        `<button class="filter-btn px-3 py-1.5 text-sm font-medium rounded-lg border border-slate-300 hover:border-indigo-400" data-category="${cat}">${cat}</button>`
    ).join("");

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">Browse Courses</h1>
        <p class="text-slate-500 mb-5">Explore all available training programs.</p>
        <div class="flex flex-wrap gap-2 mb-6" id="filterBar">${filterHtml}</div>
        <div id="courseGrid" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5"></div>
    `;

    document.querySelectorAll(".filter-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            document.querySelectorAll(".filter-btn").forEach(b => b.classList.remove("bg-indigo-600", "text-white", "border-indigo-600"));
            btn.classList.add("bg-indigo-600", "text-white", "border-indigo-600");
            renderCourseGrid(btn.dataset.category);
        });
    });

    document.querySelector(".filter-btn").classList.add("bg-indigo-600", "text-white", "border-indigo-600");
    renderCourseGrid("All");
}

function renderCourseGrid(category) {
    const grid = document.getElementById("courseGrid");
    const filtered = category === "All" ? allCourses : allCourses.filter(c => c.category === category);

    if (filtered.length === 0) {
        grid.innerHTML = `<p class="text-slate-400 col-span-3">No courses in this category.</p>`;
        return;
    }

    grid.innerHTML = filtered.map(c => {
        const isEnrolled = myEnrolledCourseIds.has(c.id);
        return `
            <div class="bg-white border border-slate-200 rounded-xl p-5 flex flex-col">
                <span class="text-xs font-semibold text-indigo-600 uppercase mb-1">${c.category}</span>
                <h3 class="font-bold text-slate-900 mb-1">${c.title}</h3>
                <p class="text-sm text-slate-500 mb-3 flex-1">${c.description}</p>
                <div class="flex items-center gap-3 text-xs text-slate-500 mb-3">
                    <span>${c.difficulty}</span>
                    <span>&middot;</span>
                    <span>${c.durationHours}h</span>
                    <span>&middot;</span>
                    <span>${c.enrolledCount} enrolled</span>
                </div>
                <p class="text-xs text-slate-400 mb-3">by ${c.trainerName}</p>
                ${isEnrolled
                    ? `<button disabled class="w-full py-2 bg-slate-100 text-slate-500 text-sm font-semibold rounded-lg">Already Enrolled</button>`
                    : `<button onclick="enrollInCourse(${c.id}, this)" class="w-full py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Enroll</button>`
                }
            </div>
        `;
    }).join("");
}

async function enrollInCourse(courseId, btn) {
    btn.disabled = true;
    btn.textContent = "Enrolling...";

    try {
        await api.post(`/enrollments/${courseId}`, {});
        myEnrolledCourseIds.add(courseId);
        btn.textContent = "Enrolled";
        btn.classList.remove("bg-indigo-600", "hover:bg-indigo-700");
        btn.classList.add("bg-green-600");
    } catch (err) {
        alert("Failed to enroll: " + err.message);
        btn.disabled = false;
        btn.textContent = "Enroll";
    }
}

loadCourses();