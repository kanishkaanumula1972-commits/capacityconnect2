auth.requireAuth();
renderTraineeLayout("courses");

const urlParams = new URLSearchParams(window.location.search);
const courseId = urlParams.get("courseId");

async function loadCourseDetail() {
    const content = document.getElementById("pageContent");

    if (!courseId) {
        content.innerHTML = `<p class="text-red-600">No course specified.</p>`;
        return;
    }

    content.innerHTML = `<p class="text-slate-500">Loading course...</p>`;

    try {
        const [course, resources, assessments, enrollments] = await Promise.all([
            api.get(`/courses/${courseId}`),
            api.get(`/resources/course/${courseId}`),
            api.get(`/assessments/course/${courseId}`),
            api.get("/enrollments/my")
        ]);

        const myEnrollment = enrollments.find(e => e.courseId == courseId);
        renderPage(course, resources, assessments, myEnrollment);

    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load course: ${err.message}</p>`;
    }
}

function renderPage(course, resources, assessments, myEnrollment) {
    const content = document.getElementById("pageContent");

    const resourceIcons = {
        RECORDED_LECTURE: "Video",
        VIDEO: "Video",
        PDF: "PDF",
        PPT: "Slides",
        DOCUMENT: "Document",
        ASSIGNMENT: "Assignment"
    };

    const resourcesHtml = resources.length > 0
        ? resources.map(r => `
            <a href="http://localhost:8080${r.fileUrl}" target="_blank" class="flex items-center justify-between px-4 py-3 border border-slate-200 rounded-lg hover:border-indigo-400 mb-2">
                <div>
                    <span class="text-xs font-semibold text-indigo-600 uppercase mr-2">${resourceIcons[r.type] || r.type}</span>
                    <span class="text-sm font-medium text-slate-800">${r.title}</span>
                </div>
                <span class="text-xs text-slate-400">Open &rarr;</span>
            </a>
        `).join("")
        : `<p class="text-sm text-slate-400">No learning resources uploaded for this course yet.</p>`;

    const assessmentsHtml = assessments.length > 0
        ? assessments.map(a => `
            <div class="flex items-center justify-between px-4 py-3 border border-slate-200 rounded-lg mb-2">
                <div>
                    <p class="text-sm font-medium text-slate-800">${a.title}</p>
                    <p class="text-xs text-slate-500">${a.questions.length} questions &middot; ${a.durationMinutes} min &middot; ${a.totalMarks} marks</p>
                </div>
                <a href="trainee-assessment.html?assessmentId=${a.id}" class="px-3 py-1.5 bg-indigo-600 text-white text-xs font-semibold rounded-lg hover:bg-indigo-700">Start</a>
            </div>
        `).join("")
        : `<p class="text-sm text-slate-400">No assessments available for this course yet.</p>`;

    const enrollmentBanner = myEnrollment
        ? `
            <div class="bg-indigo-50 border border-indigo-200 rounded-xl p-4 mb-6 flex items-center justify-between flex-wrap gap-3">
                <span class="text-sm text-indigo-800">You're enrolled &middot; ${myEnrollment.progressPercent}% complete &middot; ${myEnrollment.status.replace('_',' ')}</span>
                <a href="trainee-enrollments.html" class="text-sm text-indigo-600 font-medium hover:underline">Update progress &rarr;</a>
            </div>
        `
        : `
            <div class="bg-slate-100 border border-slate-200 rounded-xl p-4 mb-6 flex items-center justify-between flex-wrap gap-3">
                <span class="text-sm text-slate-600">You're not enrolled in this course yet.</span>
                <button onclick="enrollNow(${course.id})" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Enroll Now</button>
            </div>
        `;

    content.innerHTML = `
        <div class="mb-6">
            <span class="text-xs font-semibold text-indigo-600 uppercase">${course.category}</span>
            <h1 class="text-2xl font-bold text-slate-900 mb-1">${course.title}</h1>
            <p class="text-slate-500">${course.description}</p>
            <div class="flex items-center gap-3 text-sm text-slate-500 mt-2">
                <span>by ${course.trainerName}</span>
                <span>&middot;</span>
                <span>${course.difficulty}</span>
                <span>&middot;</span>
                <span>${course.durationHours} hours</span>
                <span>&middot;</span>
                <span>${course.enrolledCount} enrolled</span>
                ${course.averageRating > 0 ? `<span>&middot; ${course.averageRating}/5 rating</span>` : ''}
            </div>
        </div>

        ${enrollmentBanner}

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Learning Resources</h2>
                ${resourcesHtml}
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Assessments</h2>
                ${assessmentsHtml}
            </div>
        </div>

        ${myEnrollment && myEnrollment.status === 'COMPLETED' ? `
            <div class="bg-white border border-slate-200 rounded-xl p-6 mt-6">
                <h2 class="font-bold text-lg text-slate-900 mb-3">Leave Feedback</h2>
                <div id="feedbackForm">
                    <div class="flex items-center gap-2 mb-3">
                        <span class="text-sm text-slate-600">Rating:</span>
                        <select id="ratingSelect" class="px-3 py-1.5 border border-slate-300 rounded-lg text-sm">
                            <option value="5">5 - Excellent</option>
                            <option value="4">4 - Good</option>
                            <option value="3">3 - Average</option>
                            <option value="2">2 - Below Average</option>
                            <option value="1">1 - Poor</option>
                        </select>
                    </div>
                    <textarea id="commentInput" placeholder="Share your thoughts on this course..." class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm mb-3" rows="3"></textarea>
                    <button onclick="submitFeedback(${course.id})" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Submit Feedback</button>
                </div>
            </div>
        ` : ''}
    `;
}

async function enrollNow(courseId) {
    try {
        await api.post(`/enrollments/${courseId}`, {});
        loadCourseDetail();
    } catch (err) {
        alert("Failed to enroll: " + err.message);
    }
}

async function submitFeedback(courseId) {
    const rating = document.getElementById("ratingSelect").value;
    const comment = document.getElementById("commentInput").value;

    try {
        await api.post("/feedback", { courseId: parseInt(courseId), rating: parseFloat(rating), comment });
        document.getElementById("feedbackForm").innerHTML = `<p class="text-green-600 text-sm">Thank you for your feedback.</p>`;
    } catch (err) {
        alert("Failed to submit feedback: " + err.message);
    }
}

loadCourseDetail();