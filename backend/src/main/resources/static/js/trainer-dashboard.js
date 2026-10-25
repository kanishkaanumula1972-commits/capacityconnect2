auth.requireAuth();
renderTrainerLayout("dashboard");

const user = auth.getUser();

async function loadDashboard() {
    const content = document.getElementById("pageContent");
    content.innerHTML = `<p class="text-slate-500">Loading your dashboard...</p>`;

    try {
            const [courses, myProfile, announcements] = await Promise.all([
            api.get("/courses/my"),
            api.get("/trainers/profile"),
            api.get("/public/announcements/latest")
        ]);
        const totalCourses = courses.length;
        const totalEnrollments = courses.reduce((sum, c) => sum + (c.enrolledCount || 0), 0);
        const avgRating = myProfile.averageRating || 0;

        const coursesTableHtml = courses.length > 0
            ? courses.map(c => `
                <tr class="border-b border-slate-100">
                    <td class="py-3 px-2 text-sm font-medium text-slate-800">${c.title}</td>
                    <td class="py-3 px-2 text-sm text-slate-500">${c.category}</td>
                    <td class="py-3 px-2 text-sm text-slate-500">${c.enrolledCount}</td>
                    <td class="py-3 px-2 text-sm text-slate-500">${c.averageRating > 0 ? c.averageRating + '/5' : 'No ratings'}</td>
                    <td class="py-3 px-2 text-sm">
                        <a href="trainer-course-edit.html?courseId=${c.id}" class="text-indigo-600 font-medium hover:underline">Manage &rarr;</a>
                    </td>
                </tr>
            `).join("")
            : `<tr><td colspan="5" class="py-6 text-center text-slate-400">No courses yet. <a href="trainer-courses.html" class="text-indigo-600 hover:underline">Create one</a></td></tr>`;

        content.innerHTML = `
            <h1 class="text-2xl font-bold text-slate-900 mb-1">Welcome back, ${user.fullName.split(" ")[0]}.</h1>
            <p class="text-slate-500 mb-8">Here's how your training is performing.</p>

            <div class="grid grid-cols-1 md:grid-cols-4 gap-5 mb-8">
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Total Courses</p>
                    <p class="text-3xl font-bold text-slate-900">${totalCourses}</p>
                </div>
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Total Enrollments</p>
                    <p class="text-3xl font-bold text-slate-900">${totalEnrollments}</p>
                </div>
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Average Rating</p>
                    <p class="text-3xl font-bold text-slate-900">${avgRating}/5</p>
                </div>
                <div class="bg-white border border-slate-200 rounded-xl p-5">
                    <p class="text-sm text-slate-500 mb-1">Total Reviews</p>
                    <p class="text-3xl font-bold text-slate-900">${myProfile.totalRatings || 0}</p>
                </div>
            </div>

            <div class="bg-white border border-slate-200 rounded-xl p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="font-bold text-lg text-slate-900">Your Courses</h2>
                    <a href="trainer-courses.html" class="text-sm text-indigo-600 font-medium hover:underline">Manage all &rarr;</a>
                </div>
                <table class="w-full">
                    <thead>
                        <tr class="border-b border-slate-200 text-left">
                            <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Course</th>
                            <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Category</th>
                            <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Enrolled</th>
                            <th class="py-2 px-2 text-xs font-semibold text-slate-500 uppercase">Rating</th>
                            <th class="py-2 px-2"></th>
                        </tr>
                    </thead>
                    <tbody>
                        ${coursesTableHtml}
                    </tbody>
                </table>
            </div>
            <div class="bg-white border border-slate-200 rounded-xl p-6 mt-6">
                <h2 class="font-bold text-lg text-slate-900 mb-4">Recent Announcements</h2>
                ${announcements.length > 0
                    ? announcements.map(a => `
                        <div class="px-4 py-3 border border-slate-200 rounded-lg mb-2">
                            <p class="text-sm font-medium text-slate-800">${a.title}</p>
                            <p class="text-xs text-slate-500 mt-1">${a.content}</p>
                        </div>
                    `).join("")
                    : `<p class="text-sm text-slate-400">No announcements yet.</p>`
                }
            </div>
        `;

    } catch (err) {
        content.innerHTML = `<p class="text-red-600">Failed to load dashboard: ${err.message}</p>`;
    }
}

loadDashboard();