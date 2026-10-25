auth.requireAuth();
renderTraineeLayout("certificates");

let allSkills = [];

async function loadCertificates() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading certificates...</p>`;

    try {
        const certificates = await api.get("/trainees/certificates");
        renderPage(certificates);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load certificates: ${err.message}</p>`;
    }
}

function renderPage(certificates) {
    const content = document.getElementById("pageContent");

    const certsHtml = certificates.length > 0
        ? certificates.map(c => `
            <div class="bg-white border border-slate-200 rounded-xl p-5">
                <h3 class="font-bold text-slate-900">${c.name}</h3>
                <p class="text-sm text-slate-500 mb-2">${c.issuingOrganization}</p>
                <p class="text-xs text-slate-400 mb-2">Issued: ${c.issueDate || 'N/A'} &middot; Credential: ${c.credentialId || 'N/A'}</p>
                ${c.relatedSkillNames.length > 0 ? `
                    <div class="flex flex-wrap gap-1.5 mb-3">
                        ${c.relatedSkillNames.map(s => `<span class="px-2 py-0.5 bg-indigo-50 text-indigo-700 text-xs font-medium rounded-full">${s}</span>`).join("")}
                    </div>
                ` : ''}
                <div class="flex gap-2">
                    <a href="http://localhost:8080${c.fileUrl}" target="_blank" class="text-xs text-indigo-600 font-medium hover:underline">View File</a>
                    <button onclick="deleteCertificate(${c.id})" class="text-xs text-red-500 font-medium hover:underline">Delete</button>
                </div>
            </div>
        `).join("")
        : `<p class="text-slate-400 col-span-3">No certificates uploaded yet.</p>`;

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="text-2xl font-bold text-slate-900 mb-1">Certificates</h1>
                <p class="text-slate-500">Upload and manage your professional certifications.</p>
            </div>
            <button onclick="openUploadModal()" class="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">Upload Certificate</button>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
            ${certsHtml}
        </div>
        <div id="uploadModal"></div>
    `;
}

async function openUploadModal() {
    if (allSkills.length === 0) {
        try {
            allSkills = await api.get("/public/competencies").then(async () => {
                // Skills aren't exposed via a dedicated public endpoint yet,
                // so related-skill tagging is done as free text for now.
                return [];
            });
        } catch (e) { /* ignore, fall back to no skill list */ }
    }

    document.getElementById("uploadModal").innerHTML = `
        <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Upload Certificate</h2>
                <div class="space-y-3">
                    <input id="certName" type="text" placeholder="Certificate name" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="certOrg" type="text" placeholder="Issuing organization" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="certDate" type="date" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="certCredId" type="text" placeholder="Credential ID (optional)" class="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm">
                    <input id="certFile" type="file" accept=".pdf,.jpg,.jpeg,.png" class="w-full text-sm">
                </div>
                <div class="flex gap-2 mt-5">
                    <button onclick="closeUploadModal()" class="flex-1 py-2 border border-slate-300 rounded-lg text-sm font-semibold">Cancel</button>
                    <button onclick="uploadCertificate()" id="uploadBtn" class="flex-1 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold">Upload</button>
                </div>
            </div>
        </div>
    `;
}

function closeUploadModal() {
    document.getElementById("uploadModal").innerHTML = "";
}

async function uploadCertificate() {
    const name = document.getElementById("certName").value.trim();
    const org = document.getElementById("certOrg").value.trim();
    const date = document.getElementById("certDate").value;
    const credId = document.getElementById("certCredId").value.trim();
    const fileInput = document.getElementById("certFile");

    if (!name || !org || !fileInput.files[0]) {
        alert("Please fill in the certificate name, organization, and choose a file.");
        return;
    }

    const btn = document.getElementById("uploadBtn");
    btn.disabled = true;
    btn.textContent = "Uploading...";

    const formData = new FormData();
    formData.append("name", name);
    formData.append("issuingOrganization", org);
    if (date) formData.append("issueDate", date);
    if (credId) formData.append("credentialId", credId);
    formData.append("file", fileInput.files[0]);

    try {
        await api.postForm("/trainees/certificates", formData);
        closeUploadModal();
        loadCertificates();
    } catch (err) {
        alert("Failed to upload certificate: " + err.message);
        btn.disabled = false;
        btn.textContent = "Upload";
    }
}

async function deleteCertificate(id) {
    if (!confirm("Delete this certificate?")) return;
    try {
        await api.delete(`/trainees/certificates/${id}`);
        loadCertificates();
    } catch (err) {
        alert("Failed to delete: " + err.message);
    }
}

loadCertificates();