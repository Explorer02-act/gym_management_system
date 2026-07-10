const api = {
    stats: "/dashboard/stats",
    members: "/members",
    search: query => `/members/search?query=${encodeURIComponent(query)}`,
    expiringSoon: "/members/expiring-soon",
    attendanceToday: "/attendance/today",
    checkInLookup: "/attendance/checkin-lookup",
    memberships: "/memberships",
    plans: "/plans",
    settings: "/settings",
    enrollment: "/enrollment"
};

const money = value => `Rs ${Number(value || 0).toLocaleString("en-IN")}`;
const byId = id => document.getElementById(id);
let plans = [];

function showToast(message) {
    const toast = byId("toast");
    toast.textContent = message;
    toast.classList.add("show");
    window.setTimeout(() => toast.classList.remove("show"), 2800);
}

async function request(path, options = {}) {
    const response = await fetch(path, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {
        let message = `Request failed with status ${response.status}`;
        try {
            const error = await response.json();
            message = error.message || message;
            if (error.fieldErrors) {
                message = Object.values(error.fieldErrors).join(", ");
            }
        } catch (ignored) {
            // Keep the status-based message when the server did not send JSON.
        }
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

function setClock() {
    byId("clock").textContent = new Date().toLocaleTimeString("en-IN", {
        hour: "2-digit",
        minute: "2-digit"
    });
}

async function loadStats() {
    const stats = await request(api.stats);
    byId("totalMembers").textContent = stats.totalMembers ?? 0;
    byId("activeMembers").textContent = stats.activeMembers ?? 0;
    byId("expiredMembers").textContent = stats.expiredMembers ?? 0;
    byId("membershipsExpiringSoon").textContent = stats.membershipsExpiringSoon ?? 0;
    byId("todaysCheckIns").textContent = stats.todaysCheckIns ?? 0;
    byId("todaysRevenue").textContent = money(stats.todaysRevenue);
    byId("monthlyRevenue").textContent = money(stats.monthlyRevenue);
    byId("yearlyRevenue").textContent = money(stats.yearlyRevenue);
}

function renderMembers(members) {
    const container = byId("memberResults");
    if (!members.length) {
        container.innerHTML = `<div class="list-row"><strong>No members found</strong><span class="meta">The roster is waiting.</span></div>`;
        return;
    }

    container.innerHTML = members.map(member => `
        <article class="member-card">
            <div>
                <strong>${escapeHtml(member.name)}</strong>
                <div class="meta">
                    ID ${member.id} | ${escapeHtml(member.phone || "No phone")}<br>
                    Emergency: ${escapeHtml(member.emergencyContactName || "Not added")} ${escapeHtml(member.emergencyContactPhone || "")}
                </div>
            </div>
            <span class="badge">${escapeHtml(member.memberCode || "NEW")}</span>
        </article>
    `).join("");
}

function renderExpiring(items) {
    const container = byId("expiringList");
    if (!items.length) {
        container.innerHTML = `<div class="list-row"><strong>Clear queue</strong><span class="meta">No memberships expire in the next 7 days.</span></div>`;
        return;
    }

    container.innerHTML = items.map(item => `
        <article class="list-row">
            <strong>${escapeHtml(item.member?.name || "Member")}</strong>
            <span class="meta">${escapeHtml(item.member?.memberCode || "")} | ${escapeHtml(item.member?.phone || "")}</span>
            <span class="meta danger">${item.daysRemaining} day(s) left | expires ${escapeHtml(item.expiryDate || "")}</span>
        </article>
    `).join("");
}

function renderAttendance(items) {
    const container = byId("todayAttendance");
    if (!items.length) {
        container.innerHTML = `<div class="list-row"><strong>No check-ins yet</strong><span class="meta">First entry will appear here.</span></div>`;
        return;
    }

    container.innerHTML = items.map(item => `
        <article class="list-row">
            <strong>${escapeHtml(item.member?.name || "Member")}</strong>
            <span class="meta">${escapeHtml(item.member?.memberCode || "")} | ${formatTime(item.checkInTime)}</span>
        </article>
    `).join("");
}

function renderPlans(items) {
    const container = byId("planCards");
    if (!items.length) {
        container.innerHTML = `<div class="list-row"><strong>No plans found</strong><span class="meta">Create a plan from /plans first.</span></div>`;
        return;
    }

    container.innerHTML = items.map(plan => `
        <button class="plan-card" type="button" data-plan-id="${plan.id}">
            <span class="badge">${escapeHtml(plan.badge || "Plan")}</span>
            <h3>${escapeHtml(plan.name)}</h3>
            <div>
                <del>${money(plan.actualPrice)}</del>
                <strong>${money(plan.displayPrice)}</strong>
            </div>
            <span>${escapeHtml(plan.description || `${plan.durationMonths} month plan`)}</span>
        </button>
    `).join("");

    container.querySelectorAll(".plan-card").forEach(card => {
        card.addEventListener("click", () => selectPlan(Number(card.dataset.planId)));
    });

    selectPlan(items[0].id);
}

function selectPlan(planId) {
    const plan = plans.find(item => item.id === planId);
    if (!plan) return;

    byId("selectedPlanId").value = plan.id;
    byId("paymentAmount").textContent = money(plan.displayPrice);

    document.querySelectorAll(".plan-card").forEach(card => {
        card.classList.toggle("selected", Number(card.dataset.planId) === plan.id);
    });
}

async function loadMembers() {
    renderMembers(await request(api.members));
}

async function loadExpiringSoon() {
    renderExpiring(await request(api.expiringSoon));
}

async function loadAttendance() {
    renderAttendance(await request(api.attendanceToday));
}

async function loadPlans() {
    plans = await request(api.plans);
    renderPlans(plans);
}

async function loadSettings() {
    const settings = await request(api.settings);
    byId("gpayNumber").textContent = settings.gpayNumber ? `GPay: ${settings.gpayNumber}` : "";

    const qr = byId("qrPreview");
    if (settings.qrImageUrl) {
        qr.innerHTML = `<img src="${escapeHtml(settings.qrImageUrl)}" alt="Payment QR">`;
    } else {
        qr.textContent = "QR";
    }
}

async function refreshAll() {
    await Promise.all([
        loadStats(),
        loadMembers(),
        loadExpiringSoon(),
        loadAttendance(),
        loadPlans(),
        loadSettings()
    ]);
}

function formData(form) {
    return Object.fromEntries(new FormData(form).entries());
}

function wireForms() {
    byId("enrollmentForm").addEventListener("submit", async event => {
        event.preventDefault();
        const form = event.currentTarget;
        const payload = formData(form);
        payload.planId = Number(payload.planId);

        try {
            const enrollment = await request(api.enrollment, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            showToast(`Enrolled ${enrollment.member.name} as ${enrollment.member.memberCode}`);
            form.reset();
            if (plans.length) {
                selectPlan(plans[0].id);
            }
            await refreshAll();
        } catch (error) {
            showToast(error.message);
        }
    });

    byId("quickCheckInForm").addEventListener("submit", async event => {
        event.preventDefault();
        const form = event.currentTarget;
        try {
            const attendance = await request(api.checkInLookup, {
                method: "POST",
                body: JSON.stringify(formData(form))
            });
            showToast(`${attendance.member.name} checked in`);
            form.reset();
            await refreshAll();
        } catch (error) {
            showToast(error.message);
        }
    });

    byId("searchForm").addEventListener("submit", async event => {
        event.preventDefault();
        const query = byId("searchQuery").value.trim();
        if (!query) return;
        try {
            renderMembers(await request(api.search(query)));
        } catch (error) {
            showToast(error.message);
        }
    });

    byId("refreshButton").addEventListener("click", async () => {
        try {
            await refreshAll();
            showToast("Dashboard refreshed");
        } catch (error) {
            showToast(error.message);
        }
    });
}

function formatTime(value) {
    if (!value) return "";
    return value.slice(0, 5);
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function init() {
    setClock();
    window.setInterval(setClock, 30000);
    wireForms();

    try {
        await refreshAll();
    } catch (error) {
        showToast(error.message);
    }
}

init();
