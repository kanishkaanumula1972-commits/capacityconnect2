auth.requireAuth();
renderTraineeLayout("notifications");

async function loadNotifications() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading notifications...</p>`;

    try {
        const notifications = await api.get("/notifications");
        renderPage(notifications);
    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load notifications: ${err.message}</p>`;
    }
}

function renderPage(notifications) {
    const content = document.getElementById("pageContent");

    const rowsHtml = notifications.length > 0
        ? notifications.map(n => `
            <div class="flex items-start justify-between gap-4 px-5 py-4 border ${n.isRead ? 'border-slate-200 bg-white' : 'border-indigo-200 bg-indigo-50'} rounded-lg mb-2">
                <div>
                    <p class="text-sm font-semibold text-slate-800">${n.title}</p>
                    <p class="text-sm text-slate-600">${n.message}</p>
                    <p class="text-xs text-slate-400 mt-1">${new Date(n.createdAt).toLocaleString()}</p>
                </div>
                ${!n.isRead ? `<button onclick="markRead(${n.id})" class="text-xs text-indigo-600 font-medium hover:underline whitespace-nowrap">Mark read</button>` : ''}
            </div>
        `).join("")
        : `<p class="text-slate-400">No notifications yet.</p>`;

    content.innerHTML = `
        <div class="flex items-center justify-between mb-6">
            <h1 class="text-2xl font-bold text-slate-900">Notifications</h1>
            <button onclick="markAllRead()" class="text-sm text-indigo-600 font-medium hover:underline">Mark all as read</button>
        </div>
        ${rowsHtml}
    `;
}

async function markRead(id) {
    await api.put(`/notifications/${id}/read`, {});
    loadNotifications();
}

async function markAllRead() {
    await api.put("/notifications/read-all", {});
    loadNotifications();
}

loadNotifications();