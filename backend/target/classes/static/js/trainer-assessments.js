auth.requireAuth();
renderTrainerLayout("assessments");

let myCourses = [];
let questionCount = 0;

async function loadAssessments() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading assessments...</p>`;

    try {
        const [assessments, courses] = await Promise.all([
            api.get("/assessments/my-created"),
            api.get("/courses/my")
        ]);
        myCourses = courses;
        renderPage(assessments);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load assessments: ${err.message}</p>`;
    }
}

function renderPage(assessments) {
    const content = document.getElementById("pageContent");

    const listHtml = assessments.length > 0
        ? assessments.map(a => `
            <div class="bg-white border border-slate-200 rounded-xl p-5 mb-3">
                <div class="flex items-center justify-between flex-wrap gap-3">
                    <div>
                        <h3 class="font-bold text-slate-900">${a.title}</h3>
                        <p class="text-sm text-slate-500">${a.courseTitle || 'No course linked'} &middot; targets ${a.targetSkillName || 'no specific skill'} &middot; ${a.questions.length} questions &middot; ${a.durationMinutes} min</p>
                    </div>
                    <button onclick="deleteAssessment(${a.id})" class="text-sm text-red-500 font-medium hover:underline">Delete</button>
                </div>
            </div>
        `).join("")
        : `<p class="text-slate-400">You haven't created any assessments yet.</p>`;

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">Assessments</h1>
                <p class="text-slate-500">Create MCQ assessments tied to your courses and skills.</p>
            </div>
            <button onclick="openCreateModal()" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Create Assessment</button>
        </div>
        ${listHtml}
        <div id="modalRoot"></div>
    `;
}

function openCreateModal() {
    questionCount = 0;

    const courseOptionsHtml = myCourses.map(c => `<option value="${c.id}">${c.title}</option>`).join("");

    document.getElementById("modalRoot").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50 p-4">
            <div class="bg-white rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Create Assessment</h2>

                <div class="space-y-3 mb-5">
                    <input id="aTitle" type="text" placeholder="Assessment title" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <textarea id="aDescription" placeholder="Description" rows="2" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"></textarea>
                    <div class="grid grid-cols-3 gap-3">
                        <select id="aCourse" class="px-3 py-2 border border-slate-300 rounded-lg text-sm bg-white">
                            <option value="">No course</option>
                            ${courseOptionsHtml}
                        </select>
                        <input id="aTargetSkillId" type="number" placeholder="Target Skill ID" class="px-3 py-2 border border-slate-300 rounded-lg text-sm">
                        <input id="aDuration" type="number" placeholder="Duration (min)" class="px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    </div>
                    <p id="skillRefList" class="text-xs text-slate-400">Loading skill list...</p>
                </div>

                <div class="flex items-center justify-between mb-3">
                    <h3 class="font-bold text-slate-900">Questions</h3>
                    <button type="button" onclick="addQuestionBlock()" class="text-sm text-indigo-600 font-medium hover:underline">+ Add Question</button>
                </div>
                <div id="questionsContainer"></div>

                <div class="flex gap-2 mt-5 sticky bottom-0 bg-white pt-3">
                    <button onclick="closeModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="submitAssessment()" id="submitAssessBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Create Assessment</button>
                </div>
            </div>
        </div>
    `;
        api.get("/public/skills").then(skills => {
        const text = "Skill IDs: " + skills.map(s => `${s.id} ${s.name}`).join(", ");
        const el = document.getElementById("skillRefList");
        if (el) el.textContent = text;
    }).catch(() => {});

    addQuestionBlock();
    addQuestionBlock();
    addQuestionBlock();
}

function addQuestionBlock() {
    questionCount++;
    const qId = questionCount;
    const container = document.getElementById("questionsContainer");

    const block = document.createElement("div");
    block.className = "border border-slate-200 rounded-lg p-4 mb-3";
    block.id = `question-${qId}`;
    block.innerHTML = `
        <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-bold text-slate-500">QUESTION ${qId}</span>
            <button type="button" onclick="removeQuestionBlock(${qId})" class="text-xs text-red-500 hover:underline">Remove</button>
        </div>
        <input type="text" placeholder="Question text" class="q-text w-full px-3 py-2 border border-slate-300 rounded-lg text-sm mb-2">
        <div class="grid grid-cols-2 gap-2 mb-2">
            <input type="text" placeholder="Option A" class="q-optA px-3 py-2 border border-slate-300 rounded-lg text-sm">
            <input type="text" placeholder="Option B" class="q-optB px-3 py-2 border border-slate-300 rounded-lg text-sm">
            <input type="text" placeholder="Option C" class="q-optC px-3 py-2 border border-slate-300 rounded-lg text-sm">
            <input type="text" placeholder="Option D" class="q-optD px-3 py-2 border border-slate-300 rounded-lg text-sm">
        </div>
        <div class="flex items-center gap-2">
            <label class="text-xs text-slate-500">Correct answer:</label>
            <select class="q-correct px-2 py-1 border border-slate-300 rounded-lg text-sm bg-white">
                <option value="A">A</option>
                <option value="B">B</option>
                <option value="C">C</option>
                <option value="D">D</option>
            </select>
            <label class="text-xs text-slate-500 ml-3">Marks:</label>
            <input type="number" class="q-marks w-16 px-2 py-1 border border-slate-300 rounded-lg text-sm" value="1">
        </div>
    `;
    container.appendChild(block);
}

function removeQuestionBlock(qId) {
    const block = document.getElementById(`question-${qId}`);
    if (block) block.remove();
}

async function submitAssessment() {
    const title = document.getElementById("aTitle").value.trim();
    const description = document.getElementById("aDescription").value.trim();
    const courseId = document.getElementById("aCourse").value;
    const targetSkillId = document.getElementById("aTargetSkillId").value;
    const durationMinutes = document.getElementById("aDuration").value;

    const questionBlocks = document.querySelectorAll("#questionsContainer > div");
    const questions = [];

    for (const block of questionBlocks) {
        const questionText = block.querySelector(".q-text").value.trim();
        const optionA = block.querySelector(".q-optA").value.trim();
        const optionB = block.querySelector(".q-optB").value.trim();
        const optionC = block.querySelector(".q-optC").value.trim();
        const optionD = block.querySelector(".q-optD").value.trim();
        const correctOption = block.querySelector(".q-correct").value;
        const marks = parseInt(block.querySelector(".q-marks").value) || 1;

        if (!questionText || !optionA || !optionB || !optionC || !optionD) {
            alert("Please fill in all fields for every question.");
            return;
        }

        questions.push({ questionText, optionA, optionB, optionC, optionD, correctOption, marks });
    }

    if (!title || questions.length === 0) {
        alert("Please provide a title and at least one complete question.");
        return;
    }

    const btn = document.getElementById("submitAssessBtn");
    btn.disabled = true;
    btn.textContent = "Creating...";

    try {
        await api.post("/assessments", {
            title,
            description,
            courseId: courseId ? parseInt(courseId) : null,
            targetSkillId: targetSkillId ? parseInt(targetSkillId) : null,
            durationMinutes: durationMinutes ? parseInt(durationMinutes) : null,
            questions
        });
        closeModal();
        loadAssessments();
    } catch (err) {
        alert("Failed to create assessment: " + err.message);
        btn.disabled = false;
        btn.textContent = "Create Assessment";
    }
}

async function deleteAssessment(id) {
    if (!confirm("Delete this assessment? All attempts will remain in history but the assessment will no longer be available.")) return;
    try {
        await api.delete(`/assessments/${id}`);
        loadAssessments();
    } catch (err) {
        alert("Failed to delete: " + err.message);
    }
}

function closeModal() {
    document.getElementById("modalRoot").innerHTML = "";
}

loadAssessments();