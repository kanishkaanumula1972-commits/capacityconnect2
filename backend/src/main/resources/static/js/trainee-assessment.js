auth.requireAuth();
renderTraineeLayout("courses");

const urlParams = new URLSearchParams(window.location.search);
const assessmentId = urlParams.get("assessmentId");

let currentAssessment = null;
const selectedAnswers = {}; // questionId -> "A"/"B"/"C"/"D"

async function loadAssessment() {
    const content = document.getElementById("pageContent");

    if (!assessmentId) {
        content.innerHTML = `<p class="text-red-600">No assessment specified.</p>`;
        return;
    }

    content.innerHTML = `<p class="text-slate-500">Loading assessment...</p>`;

    try {
        currentAssessment = await api.get(`/assessments/${assessmentId}`);
        renderAssessment();
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load assessment: ${err.message}</p>`;
    }
}

function renderAssessment() {
    const content = document.getElementById("pageContent");
    const a = currentAssessment;

    const questionsHtml = a.questions.map((q, i) => `
        <div class="bg-white border border-slate-200 rounded-xl p-6 mb-4">
            <p class="font-semibold text-slate-900 mb-4">${i + 1}. ${q.questionText}</p>
            <div class="space-y-2">
                ${["A", "B", "C", "D"].map(opt => {
                    const optionText = q["option" + opt];
                    return `
                        <label class="flex items-center gap-3 px-4 py-3 border border-slate-200 rounded-lg cursor-pointer hover:border-indigo-400 option-label" data-question="${q.id}" data-option="${opt}">
                            <input type="radio" name="q_${q.id}" value="${opt}" onchange="selectAnswer(${q.id}, '${opt}')" class="w-4 h-4 text-indigo-600">
                            <span class="text-sm text-slate-700">${optionText}</span>
                        </label>
                    `;
                }).join("")}
            </div>
        </div>
    `).join("");

    content.innerHTML = `
        <div class="mb-6">
            <h1 class="text-2xl font-bold text-slate-900 mb-1">${a.title}</h1>
            <p class="text-slate-500">${a.description || ''}</p>
            <p class="text-sm text-slate-400 mt-1">${a.questions.length} questions &middot; ${a.durationMinutes} minutes &middot; ${a.totalMarks} marks</p>
        </div>
        ${questionsHtml}
        <button onclick="submitAssessment()" id="submitAssessmentBtn"
            class="w-full py-3 bg-indigo-600 text-white font-semibold rounded-lg hover:bg-indigo-700 transition">
            Submit Assessment
        </button>
    `;
}

function selectAnswer(questionId, option) {
    selectedAnswers[questionId] = option;
}

async function submitAssessment() {
    const btn = document.getElementById("submitAssessmentBtn");
    btn.disabled = true;
    btn.textContent = "Submitting...";

    try {
        const result = await api.post(`/assessments/${assessmentId}/attempt`, { answers: selectedAnswers });
        renderResult(result);
    } catch (err) {
        alert("Failed to submit assessment: " + err.message);
        btn.disabled = false;
        btn.textContent = "Submit Assessment";
    }
}

function renderResult(result) {
    const content = document.getElementById("pageContent");

    const scoreColor = result.percentageScore >= 70 ? "text-green-600" : result.percentageScore >= 40 ? "text-amber-600" : "text-red-600";

    const questionReviewHtml = result.questionResults.map((q, i) => `
        <div class="border ${q.isCorrect ? 'border-green-200 bg-green-50' : 'border-red-200 bg-red-50'} rounded-lg p-4 mb-3">
            <p class="text-sm font-medium text-slate-800 mb-1">${i + 1}. ${q.questionText}</p>
            <p class="text-xs text-slate-600">Your answer: <strong>${q.selectedOption || 'Not answered'}</strong>
                ${q.isCorrect ? '<span class="text-green-700 font-semibold">Correct</span>' : `<span class="text-red-700 font-semibold">Incorrect (correct: ${q.correctOption})</span>`}
            </p>
        </div>
    `).join("");

    let competencyUpdateHtml = "";
    if (result.competencyUpdate) {
        const cu = result.competencyUpdate;
        const improved = cu.improvement > 0;
        competencyUpdateHtml = `
            <div class="bg-indigo-50 border border-indigo-200 rounded-xl p-6 mb-6">
                <h2 class="font-bold text-lg text-indigo-900 mb-4">Competency Updated: ${cu.skillName}</h2>
                <div class="grid grid-cols-3 gap-4 text-center">
                    <div>
                        <p class="text-xs text-slate-500 mb-1">Before</p>
                        <p class="text-2xl font-bold text-slate-700">${cu.scoreBefore}%</p>
                    </div>
                    <div>
                        <p class="text-xs text-slate-500 mb-1">After</p>
                        <p class="text-2xl font-bold text-indigo-700">${cu.scoreAfter}%</p>
                    </div>
                    <div>
                        <p class="text-xs text-slate-500 mb-1">Improvement</p>
                        <p class="text-2xl font-bold ${improved ? 'text-green-600' : 'text-slate-500'}">${improved ? '+' : ''}${cu.improvement}%</p>
                    </div>
                </div>
            </div>
        `;
    }

    content.innerHTML = `
        <div class="text-center mb-6">
            <p class="text-sm text-slate-500 mb-1">Your Score</p>
            <p class="text-5xl font-extrabold ${scoreColor}">${result.percentageScore}%</p>
            <p class="text-slate-500 mt-1">${result.marksObtained} / ${result.totalMarks} marks</p>
        </div>

        ${competencyUpdateHtml}

        <div class="bg-white border border-slate-200 rounded-xl p-6">
            <h2 class="font-bold text-lg text-slate-900 mb-4">Answer Review</h2>
            ${questionReviewHtml}
        </div>

        <div class="flex gap-3 mt-6">
            <a href="trainee-competencies.html" class="flex-1 text-center py-3 bg-indigo-600 text-white font-semibold rounded-lg hover:bg-indigo-700">View Updated Competency Profile</a>
            <a href="trainee-dashboard.html" class="flex-1 text-center py-3 bg-white border border-slate-300 text-slate-700 font-semibold rounded-lg hover:border-indigo-400">Back to Dashboard</a>
        </div>
    `;
}

loadAssessment();