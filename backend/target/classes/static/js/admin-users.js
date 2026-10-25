auth.requireAuth();
renderAdminLayout("users");

let allUsers = [];

async function loadUsers() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading users...</p>`;

    try {
        allUsers = await api.get("/admin/users");
        renderPage("ALL", "");
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load users: ${err.message}</p>`;
    }
}

function renderPage(filter, searchTerm) {
    const content = document.getElementById("pageContent");
    searchTerm = (searchTerm || "").toLowerCase().trim();

    let filtered = filter === "ALL" ? allUsers : allUsers.filter(u => u.status === filter);

    if (searchTerm) {
        filtered = filtered.filter(u =>
            u.fullName.toLowerCase().includes(searchTerm) ||
            u.email.toLowerCase().includes(searchTerm)
        );
    }

    const statusStyles = {
        PENDING: "bg-amber-100 text-amber-700",
        ACTIVE: "bg-green-100 text-green-700",
        REJECTED: "bg-red-100 text-red-700",
        SUSPENDED: "bg-slate-200 text-slate-700"
    };

    const rowsHtml = filtered.length > 0
        ? filtered.map(u => `
            <tr class="border-b border-slate-100">
                <td class="py-3 px-2 text-sm font-medium text-slate-800">${u.fullName} <span class="text-xs text-slate-400">(ID: ${u.id})</span></td>
                <td class="py-3 px-2 text-sm text-slate-500">${u.email}</td>
                <td class="py-3 px-2 text-sm text-slate-500">${u.role}</td>
                <td class="py-3 px-2"><span class="px-2.5 py-0.5 text-xs font-semibold rounded-full ${statusStyles[u.status]}">${u.status}</span></td>
                <td class="py-3 px-2 text-sm">
                    ${u.status === 'PENDING' ? `
                        <button onclick="approveUser(${u.id})" class="text-green-600 font-medium hover:underline mr-3">Approve</button>
                        <button onclick="rejectUser(${u.id})" class="text-red-600 font-medium hover:underline">Reject</button>
                    ` : u.status === 'ACTIVE' ? `
                        <button onclick="suspendUser(${u.id})" class="text-slate-500 font-medium hover:underline">Suspend</button>
                    ` : u.status === 'SUSPENDED' ? `
                        <button onclick="reactivateUser(${u.id})" class="text-indigo-600 font-medium hover:underline">Reactivate</button>
                    ` : ''}
                </td>
            </tr>
        `).join("")
        : `<tr><td colspan="5" class="py-6 text-center text-slate-400">No users match.</td></tr>`;

    content.innerHTML = `
        <h1 class="text-2xl font-bold text-slate-900 mb-1">Manage Users</h1>
        <p class="text-slate-500 mb-6">Approve, reject, and manage user accounts.</p>

        <div class="flex items-center justify-between gap-4 mb-5 flex-wrap">
            <div class="flex gap-2" id="filterBar">
                ${["ALL", "PENDING", "ACTIVE", "REJECTED", "SUSPENDED"].map(f =>
                    `<button class="filter-btn px-3 py-1.5 text-sm font-medium rounded-lg border border-slate-300" data-filter="${f}">${f}</button>`
                ).join("")}
            </div>
            <input id="userSearchInput" type="text" placeholder="Search by name or email..." class="px-3 py-2 border border-slate-300 rounded-lg text-sm w-64">
        </div>

        <div class="bg-white border border-slate-200 rounded-xl p-6">
            <table class="w-full">
                <thead>
                    <tr class="border-b border-slate-200 text-left">
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Name</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Email</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Role</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Status</th>
                        <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Actions</th>
                    </tr>
                </thead>
                <tbody>${rowsHtml}</tbody>
            </table>
        </div>
    `;

    document.querySelectorAll(".filter-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            document.querySelectorAll(".filter-btn").forEach(b => b.classList.remove("bg-indigo-600", "text-white", "border-indigo-600"));
            btn.classList.add("bg-indigo-600", "text-white", "border-indigo-600");
            const currentSearch = document.getElementById("userSearchInput").value;
            renderPage(btn.dataset.filter, currentSearch);
        });
    });
    document.querySelector(`[data-filter="${filter}"]`).classList.add("bg-indigo-600", "text-white", "border-indigo-600");

    const searchInput = document.getElementById("userSearchInput");
    searchInput.value = searchTerm;
    searchInput.focus();
    searchInput.setSelectionRange(searchInput.value.length, searchInput.value.length);
    searchInput.addEventListener("input", () => {
        const activeFilter = document.querySelector(".filter-btn.bg-indigo-600")?.dataset.filter || "ALL";
        renderPage(activeFilter, searchInput.value);
    });
}

async function approveUser(id) {
    try {
        await api.put(`/admin/users/${id}/approve`, {});
        loadUsers();
    } catch (err) {
        alert("Failed: " + err.message);
    }
}

async function rejectUser(id) {
    if (!confirm("Reject this user's registration?")) return;
    try {
        await api.put(`/admin/users/${id}/reject`, {});
        loadUsers();
    } catch (err) {
        alert("Failed: " + err.message);
    }
}

async function suspendUser(id) {
    if (!confirm("Suspend this user?")) return;
    try {
        await api.put(`/admin/users/${id}/suspend`, {});
        loadUsers();
    } catch (err) {
        alert("Failed: " + err.message);
    }
}

async function reactivateUser(id) {
    try {
        await api.put(`/admin/users/${id}/reactivate`, {});
        loadUsers();
    } catch (err) {
        alert("Failed: " + err.message);
    }
}

loadUsers();