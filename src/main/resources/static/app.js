const api = {
    stats: "/dashboard/stats",
    members: "/members",
    search: query => `/members/search?query=${encodeURIComponent(query)}`,
    attendanceRecent: "/attendance/recent",
    attendanceLeaderboard: "/attendance/leaderboard",
    checkInLookup: "/attendance/checkin-lookup",
    plans: "/plans",
    offers: "/offers",
    settings: "/settings",
    enrollment: "/enrollment",
    expiringSevenDays: "/expiring-members/7-days",
    revenueSummary: "/revenue/summary",
    profile: memberId => `/members/${memberId}/profile`,
    member: memberId => `/members/${memberId}`,
    receipt: paymentId => `/receipts/payment/${paymentId}`,
    renew: memberId => `/renewals/member/${memberId}`,
    payments: "/payments/add",
    pauseMembership: "/membership/pause"
};

const money = value => `Rs ${Number(value || 0).toLocaleString("en-IN")}`;

function formatOfferDiscount(item) {
    const percent = Number(item?.discountPercentage || 0);
    const amount = Number(item?.discountAmount || 0);

    const parts = [];

    if (percent > 0) {
        parts.push(`${percent}% off`);
    }

    if (amount > 0) {
        parts.push(`${money(amount)} off`);
    }

    return parts.length ? parts.join(" + ") : "No discount";
}
const byId = id => document.getElementById(id);
let plans = [];
let authToken = sessionStorage.getItem("mm_auth_token") || "";
let currentPaymentData = null;

function showToast(message) {
    const toast = byId("toast");
    toast.textContent = message;
    toast.classList.add("show");
    window.setTimeout(() => toast.classList.remove("show"), 3800);
}


function clearAuth() {
    authToken = "";
    sessionStorage.removeItem("mm_auth_token");
    document.body.classList.remove("authenticated");
}

async function login(username, password) {
    const response = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });
    if (!response.ok) {
        let message = "Login failed";
        try {
            const error = await response.json();
            message = error.message || message;
        } catch (ignored) {
            // Keep the generic login failure.
        }
        throw new Error(message);
    }
    const result = await response.json();
    authToken = result.token;
    sessionStorage.setItem("mm_auth_token", authToken);
    document.body.classList.add("authenticated");
    return result;
}

async function openReceipt(paymentId, paymentData = null) {
    const response = await fetch(api.receipt(paymentId), {
        headers: authToken ? { Authorization: `Bearer ${authToken}` } : {}
    });
    if (!response.ok) {
        throw new Error(response.status === 401 ? "Please login again" : "Unable to open receipt");
    }
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    
    // Store payment data for WhatsApp sharing
    currentPaymentData = paymentData;
    
    // Show modal with PDF
    const modal = byId("receiptModal");
    const frame = byId("receiptFrame");
    frame.src = url;
    modal.hidden = false;
    
    window.setTimeout(() => URL.revokeObjectURL(url), 60000);
}
async function request(path, options = {}) {
    const headers = {
        "Content-Type": "application/json",
        ...(authToken ? { Authorization: `Bearer ${authToken}` } : {}),
        ...(options.headers || {})
    };
    const response = await fetch(path, {
        headers,
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
        if (response.status === 401) { clearAuth(); }
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return response.json();
    }

    return response.text();
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
    byId("pendingCollections").textContent = money(stats.totalPendingCollections ?? 0);
    renderPendingDues(stats.membersWithPendingDues || []);
    renderActiveOffers(stats.activeOffers || []);
}

async function loadRevenueSummary() {
    const summary = await request(api.revenueSummary);
    byId("todaysRevenue").textContent = money(summary.todaysRevenue);
    byId("monthlyRevenue").textContent = money(summary.monthlyRevenue);
    byId("yearlyRevenue").textContent = money(summary.yearlyRevenue);
    byId("lifetimeRevenue").textContent = money(summary.lifetimeRevenue);
    byId("todaysCashRevenue").textContent = money(summary.todaysCashRevenue);
    byId("todaysUpiRevenue").textContent = money(summary.todaysUpiRevenue);
    byId("monthlyCashRevenue").textContent = money(summary.monthlyCashRevenue);
    byId("monthlyUpiRevenue").textContent = money(summary.monthlyUpiRevenue);
    renderRevenueByPlan(summary.revenueByPlan || []);
}

function renderMembers(members) {
    const container = byId("memberResults");
    if (!members.length) {
        container.innerHTML = `<div class="list-row"><strong>No members found</strong><span class="meta">The roster is waiting.</span></div>`;
        return;
    }

    container.innerHTML = members.map(member => `
        <article class="member-card">
            <div class="avatar">${member.photoUrl ? `<img src="${escapeHtml(member.photoUrl)}" alt="">` : initials(member.name)}</div>
            <div>
                <strong>${escapeHtml(member.name)}</strong>
                <div class="meta">
                    ID ${member.id} | ${escapeHtml(member.phone || "No phone")}<br>
                    Emergency: ${escapeHtml(member.emergencyContactName || "Not added")} ${escapeHtml(member.emergencyContactPhone || "")}
                </div>
            </div>
            <div class="member-actions">
                <span class="badge">${escapeHtml(member.memberCode || "NEW")}</span>
                <button type="button" class="ghost-button" data-profile-id="${member.id}">Profile</button>
                <button type="button" class="ghost-button" data-renew-toggle="${member.id}">Renew</button>
            </div>
            <form class="renew-form member-renew-form" data-renew-member-id="${member.id}" hidden>
                <select name="planId" required>
                    ${plans.map(plan => `<option value="${plan.id}">${escapeHtml(plan.name)} - ${money(plan.displayPrice)}</option>`).join("")}
                </select>
                <select class="renew-payment-mode" name="paymentMode" required>
                    <option>GPAY</option>
                    <option>PHONEPE</option>
                    <option>CASH</option>
                    <option>CARD</option>
                </select>
                <input class="renew-transaction-id" name="transactionId" placeholder="Transaction ID">
                <button type="submit">Renew Plan</button>
            </form>
        </article>
    `).join("");

    container.querySelectorAll("[data-profile-id]").forEach(button => {
        button.addEventListener("click", () => loadMemberProfile(Number(button.dataset.profileId)));
    });

    container.querySelectorAll("[data-renew-toggle]").forEach(button => {
        button.addEventListener("click", () => {
            const form = container.querySelector(`.member-renew-form[data-renew-member-id="${button.dataset.renewToggle}"]`);
            if (form) {
                form.hidden = !form.hidden;
            }
        });
    });

    container.querySelectorAll(".member-renew-form").forEach(form => {
        form.addEventListener("submit", renewFromExpiringCard);
        const paymentMode = form.querySelector(".renew-payment-mode");
        const transactionId = form.querySelector(".renew-transaction-id");
        if (paymentMode && transactionId) {
            paymentMode.addEventListener("change", () => {
                if (paymentMode.value.toUpperCase() === "CASH") {
                    transactionId.removeAttribute("required");
                    transactionId.placeholder = "Optional for cash payments";
                } else {
                    transactionId.setAttribute("required", "required");
                    transactionId.placeholder = "Transaction ID";
                }
            });
            // Initial validation
            if (paymentMode.value.toUpperCase() === "CASH") {
                transactionId.removeAttribute("required");
                transactionId.placeholder = "Optional for cash payments";
            }
        }
    });
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
            ${item.whatsappUrl ? `<a class="action-link" href="${escapeHtml(item.whatsappUrl)}" target="_blank" rel="noreferrer">WhatsApp renewal</a>` : ""}
            <form class="renew-form" data-renew-member-id="${item.member?.id || ""}">
                <select name="planId" required>
                    ${plans.map(plan => `<option value="${plan.id}">${escapeHtml(plan.name)} - ${money(plan.displayPrice)}</option>`).join("")}
                </select>
                <select class="renew-payment-mode" name="paymentMode" required>
                    <option>GPAY</option>
                    <option>PHONEPE</option>
                    <option>CASH</option>
                    <option>CARD</option>
                </select>
                <input class="renew-transaction-id" name="transactionId" placeholder="Transaction ID">
                <button type="submit">Renew</button>
            </form>
        </article>
    `).join("");

    container.querySelectorAll("[data-renew-member-id]").forEach(form => {
        form.addEventListener("submit", renewFromExpiringCard);
        const paymentMode = form.querySelector(".renew-payment-mode");
        const transactionId = form.querySelector(".renew-transaction-id");
        if (paymentMode && transactionId) {
            paymentMode.addEventListener("change", () => {
                if (paymentMode.value.toUpperCase() === "CASH") {
                    transactionId.removeAttribute("required");
                    transactionId.placeholder = "Optional for cash payments";
                } else {
                    transactionId.setAttribute("required", "required");
                    transactionId.placeholder = "Transaction ID";
                }
            });
            // Initial validation
            if (paymentMode.value.toUpperCase() === "CASH") {
                transactionId.removeAttribute("required");
                transactionId.placeholder = "Optional for cash payments";
            }
        }
    });
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
            <span class="meta">${escapeHtml(item.member?.memberCode || "")} | ${escapeHtml(item.attendanceDate || "")} | ${formatTime(item.checkInTime)}</span>
        </article>
    `).join("");
}

function renderRevenueByPlan(items) {
    const container = byId("revenueByPlan");
    if (!items.length) {
        container.innerHTML = `<div class="list-row"><strong>No plan revenue yet</strong><span class="meta">Payments will group here.</span></div>`;
        return;
    }

    container.innerHTML = items.map(item => `
        <article class="list-row tight-row">
            <strong>${escapeHtml(item.planName || "Plan")}</strong>
            <span class="meta">${item.paymentCount || 0} payment(s)</span>
            <span class="meta">${money(item.totalRevenue)}</span>
        </article>
    `).join("");
}

function renderPendingDues(items) {
    const container = byId("pendingDuesList");
    if (!items.length) {
        container.innerHTML = `<div class="list-row"><strong>No pending dues</strong><span class="meta">Every membership looks settled right now.</span></div>`;
        return;
    }

    container.innerHTML = items.map(item => `
        <article class="list-row tight-row">
            <strong>${escapeHtml(item.memberName || "Member")}</strong>
            <span class="meta">${escapeHtml(item.memberCode || "")}</span>
            <span class="meta">${escapeHtml(item.planType || "Plan")}</span>
            <span class="meta danger">${money(item.balanceAmount || 0)} - ${escapeHtml(item.paymentStatus || "PENDING")}</span>
        </article>
    `).join("");
}

function renderActiveOffers(items) {
    const container = byId("activeOffers");
    if (!items.length) {
        container.innerHTML = `<div class="list-row"><strong>No active offers</strong><span class="meta">Create offers from the offer management flow.</span></div>`;
        return;
    }

    container.innerHTML = items.map(item => `
        <article class="offer-card">
            <span class="badge">Offer</span>
            <strong>${escapeHtml(item.offerName || "Offer")}</strong>
            <span class="meta">${formatOfferDiscount(item)}</span>
            <span class="meta">${escapeHtml(item.startDate || "-")} -> ${escapeHtml(item.endDate || "-")}</span>
        </article>
    `).join("");
}

function renderOffers(items) {
    const container = byId("offerList");
    if (!items.length) {
        container.innerHTML = `<div class="list-row"><strong>No offers yet</strong><span class="meta">Add a seasonal discount to start driving more signups.</span></div>`;
        return;
    }

    container.innerHTML = items.map(item => `
        <article class="list-row tight-row offer-row">
            <div>
                <strong>${escapeHtml(item.offerName || "Offer")}</strong>
                <span class="meta">${formatOfferDiscount(item)} | ${escapeHtml(item.startDate || "-")} -> ${escapeHtml(item.endDate || "-")}</span>
            </div>
            <div class="row-actions">
                <button type="button" class="small-button" data-offer-edit="${item.id}">Edit</button>
                <button type="button" class="ghost-button" data-offer-delete="${item.id}">Delete</button>
            </div>
        </article>
    `).join("");

    container.querySelectorAll("[data-offer-edit]").forEach(button => {
        button.addEventListener("click", () => preloadOfferForm(Number(button.dataset.offerEdit)));
    });

    container.querySelectorAll("[data-offer-delete]").forEach(button => {
        button.addEventListener("click", () => deleteOffer(Number(button.dataset.offerDelete)));
    });
}

function renderLeaderboard(items) {
    const container = byId("attendanceLeaderboard");
    if (!items.length) {
        container.innerHTML = `<div class="list-row"><strong>No visits yet</strong><span class="meta">Attendance ranking starts after check-ins.</span></div>`;
        return;
    }

    container.innerHTML = items.slice(0, 8).map((item, index) => `
        <article class="list-row leaderboard-row">
            <span class="rank">#${index + 1}</span>
            <div>
                <strong>${escapeHtml(item.memberName || "Member")}</strong>
                <span class="meta">${escapeHtml(item.memberCode || "")}</span>
            </div>
            <strong>${item.visits || 0}</strong>
        </article>
    `).join("");
}

function renderProfile(profile) {
    const container = byId("memberProfile");
    const member = profile.member || {};
    const current = profile.currentMembership;
    const totalAmount = Number(current?.totalAmount || current?.planPrice || 0);
    const amountPaid = Number(current?.amountPaid || 0);
    const balanceAmount = Number(current?.balanceAmount || Math.max(totalAmount - amountPaid, 0));
    const paymentStatus = current?.paymentStatus || "PENDING";
    const pauseHistory = Array.isArray(profile.pauseHistory) ? profile.pauseHistory : [];

    container.innerHTML = `
        <article class="profile-card">
            <div class="profile-top">
                <div class="avatar large">${member.photoUrl ? `<img src="${escapeHtml(member.photoUrl)}" alt="">` : initials(member.name)}</div>
                <div>
                    <span class="panel-kicker">Member profile</span>
                    <h3>${escapeHtml(member.name || "Member")}</h3>
                    <span class="meta">${escapeHtml(member.memberCode || "")} | ${escapeHtml(member.phone || "")}</span>
                </div>
                <button type="button" class="ghost-button danger-button" data-delete-member-id="${member.id || ""}" data-delete-member-name="${escapeHtml(member.name || "this member")}">Remove</button>
            </div>
            <div class="profile-metrics">
                <div><span>Total visits</span><strong>${profile.totalVisits || 0}</strong></div>
                <div><span>Revenue</span><strong>${money(profile.revenueGenerated)}</strong></div>
                <div><span>Expiry</span><strong>${escapeHtml(profile.upcomingExpiry || "-")}</strong></div>
            </div>
            <div class="list-row">
                <strong>${current ? escapeHtml(current.planType || "Current plan") : "No active plan found"}</strong>
                <span class="meta">${current ? `${escapeHtml(current.status || "")} | ${escapeHtml(current.joinDate || "")} to ${escapeHtml(current.expiryDate || "")}` : "Enroll or renew this member to activate a plan."}</span>
            </div>
            ${current ? `
                <div class="profile-metrics">
                    <div><span>Total Amount</span><strong>${money(totalAmount)}</strong></div>
                    <div><span>Paid</span><strong>${money(amountPaid)}</strong></div>
                    <div><span>Balance</span><strong>${money(balanceAmount)}</strong></div>
                </div>
                <div class="list-row">
                    <strong>Payment status</strong>
                    <span class="meta">${escapeHtml(paymentStatus)}</span>
                </div>
                ${balanceAmount > 0 ? `
                    <form class="payment-form" data-payment-form data-member-id="${member.id || ""}" data-membership-id="${current.id || ""}">
                        <label>Complete Remaining Balance
                            <input name="amount" type="number" min="1" step="1" value="${Math.round(balanceAmount)}" readonly required>
                        </label>
                        <label>Payment Mode
                            <select class="profile-payment-mode" name="paymentMode" required>
                                <option>GPAY</option>
                                <option>PHONEPE</option>
                                <option>CASH</option>
                                <option>CARD</option>
                            </select>
                        </label>
                        <label>Transaction ID
                            <input class="profile-transaction-id" name="transactionId" placeholder="Reference ID">
                        </label>
                        <button type="submit">Complete Payment</button>
                    </form>
                ` : ""}
                <div class="list-row">
                    <strong>Pause membership</strong>
                    <span class="meta">Extend the expiry date by pausing the current plan without overlap.</span>
                </div>
                <form class="pause-form" data-pause-form data-member-id="${member.id || ""}" data-membership-id="${current.id || ""}">
                    <label>Pause Start Date
                        <input name="pauseStartDate" type="date" required>
                    </label>
                    <label>Pause End Date
                        <input name="pauseEndDate" type="date" required>
                    </label>
                    <label>Reason
                        <input name="reason" placeholder="Vacation / medical leave" required>
                    </label>
                    <button type="submit">Pause Membership</button>
                </form>
                <div class="list-row">
                    <strong>Pause history</strong>
                    ${pauseHistory.length ? pauseHistory.map(item => `
                        <span class="meta">${escapeHtml(item.pauseStartDate || "-")} -> ${escapeHtml(item.pauseEndDate || "-")} | ${item.pauseDays || 0} day(s) | ${escapeHtml(item.reason || "-")} | new expiry ${escapeHtml(item.newExpiryDate || "-")}</span>
                    `).join("") : `<span class="meta">No pauses recorded yet.</span>`}
                </div>
            ` : ""}
        </article>
    `;

    container.querySelectorAll("[data-payment-form]").forEach(form => {
        form.addEventListener("submit", recordMembershipPayment);
        const paymentMode = form.querySelector(".profile-payment-mode");
        const transactionId = form.querySelector(".profile-transaction-id");
        if (paymentMode && transactionId) {
            paymentMode.addEventListener("change", () => {
                if (paymentMode.value.toUpperCase() === "CASH") {
                    transactionId.removeAttribute("required");
                    transactionId.placeholder = "Optional for cash payments";
                } else {
                    transactionId.setAttribute("required", "required");
                    transactionId.placeholder = "Reference ID";
                }
            });
            // Initial validation
            if (paymentMode.value.toUpperCase() === "CASH") {
                transactionId.removeAttribute("required");
                transactionId.placeholder = "Optional for cash payments";
            }
        }
    });

    container.querySelectorAll("[data-pause-form]").forEach(form => {
        form.addEventListener("submit", recordMembershipPause);
    });

    container.querySelectorAll("[data-delete-member-id]").forEach(button => {
        button.addEventListener("click", () => deleteMember(Number(button.dataset.deleteMemberId), button.dataset.deleteMemberName));
    });
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
    const amountPaidInput = byId("amountPaid");
    if (amountPaidInput) {
        amountPaidInput.value = plan.displayPrice;
    }

    document.querySelectorAll(".plan-card").forEach(card => {
        card.classList.toggle("selected", Number(card.dataset.planId) === plan.id);
    });
}

async function loadMembers() {
    renderMembers(await request(api.members));
}

async function loadExpiringSoon() {
    renderExpiring(await request(api.expiringSevenDays));
}

async function loadAttendance() {
    renderAttendance(await request(api.attendanceRecent));
}

async function loadLeaderboard() {
    renderLeaderboard(await request(api.attendanceLeaderboard));
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

async function loadOffers() {
    const offers = await request(api.offers);
    window.offers = offers || [];
    renderOffers(window.offers);
}

async function refreshAll() {
    await loadPlans();
    await Promise.all([
        loadStats(),
        loadRevenueSummary(),
        loadMembers(),
        loadExpiringSoon(),
        loadAttendance(),
        loadLeaderboard(),
        loadSettings(),
        loadOffers()
    ]);
}

async function loadMemberProfile(memberId) {
    try {
        renderProfile(await request(api.profile(memberId)));
    } catch (error) {
        showToast(error.message);
    }
}

async function recordMembershipPayment(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = formData(form);
    payload.membershipId = Number(form.dataset.membershipId);
    payload.amount = Number(payload.amount);
    payload.memberId = Number(form.dataset.memberId);

    try {
        const payment = await request(api.payments, {
            method: "POST",
            body: JSON.stringify(payload)
        });
        showToast(`Payment recorded for ${payment.memberName || "member"}`);
        if (payment.id) {
            await openReceipt(payment.id, {
                memberName: payment.memberName,
                amount: payment.amount,
                paymentMode: payment.paymentMode,
                paymentDate: payment.paymentDate
            });
        }
        await loadMemberProfile(payload.memberId);
        await refreshAll();
    } catch (error) {
        showToast(error.message);
    }
}

async function recordMembershipPause(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = formData(form);
    payload.membershipId = Number(form.dataset.membershipId);
    payload.memberId = Number(form.dataset.memberId);

    try {
        const pause = await request(api.pauseMembership, {
            method: "POST",
            body: JSON.stringify(payload)
        });
        showToast(`Membership paused for ${pause.pauseDays || 0} day(s)`);
        await loadMemberProfile(payload.memberId);
        await refreshAll();
    } catch (error) {
        showToast(error.message);
    }
}

async function renewFromExpiringCard(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const memberId = Number(form.dataset.renewMemberId);
    const payload = formData(form);
    payload.planId = Number(payload.planId);

    try {
        const renewal = await request(api.renew(memberId), {
            method: "POST",
            body: JSON.stringify(payload)
        });
        showToast(`Renewed ${renewal.member.name} until ${renewal.membership.expiryDate}`);
        if (renewal.payment?.id) {
            await openReceipt(renewal.payment.id, {
                memberName: renewal.member.name,
                amount: renewal.payment.amount,
                paymentMode: renewal.payment.paymentMode,
                paymentDate: renewal.payment.paymentDate
            });
        }
        await refreshAll();
    } catch (error) {
        showToast(error.message);
    }
}

async function deleteMember(memberId, memberName) {
    if (!memberId) return;

    const confirmed = window.confirm(`Remove ${memberName || "this member"} and all related data? This cannot be undone.`);
    if (!confirmed) return;

    try {
        const message = await request(api.member(memberId), {
            method: "DELETE"
        });
        showToast(message || "Member removed");
        byId("memberProfile").innerHTML = "";
        await refreshAll();
    } catch (error) {
        showToast(error.message);
    }
}

async function deleteOffer(offerId) {
    try {
        const message = await request(`${api.offers}/${offerId}`, {
            method: "DELETE"
        });
        showToast(message || "Offer removed");
        await loadOffers();
        await loadStats();
    } catch (error) {
        showToast(error.message);
    }
}

function preloadOfferForm(offerId) {
    const offer = (window.offers || []).find(item => Number(item.id) === Number(offerId));
    if (!offer) return;

    byId("offerId").value = offer.id;
    byId("offerName").value = offer.offerName || "";
    byId("discountPercentage").value = offer.discountPercentage || "";
    byId("discountAmount").value = offer.discountAmount || "";
    byId("startDate").value = offer.startDate || "";
    byId("endDate").value = offer.endDate || "";
    byId("offerActive").checked = Boolean(offer.active);
    byId("offerSubmitButton").textContent = "Update Offer";
}

function resetOfferForm() {
    byId("offerForm").reset();
    byId("offerId").value = "";
    byId("offerSubmitButton").textContent = "Add Offer";
}

function formData(form) {
    return Object.fromEntries(new FormData(form).entries());
}

function wireForms() {
    // Conditional validation for transaction ID based on payment mode
    const setupPaymentModeValidation = (paymentModeSelect, transactionIdInput) => {
        if (!paymentModeSelect || !transactionIdInput) return;
        
        const validateTransactionId = () => {
            const paymentMode = paymentModeSelect.value.toUpperCase();
            if (paymentMode === "CASH") {
                transactionIdInput.removeAttribute("required");
                transactionIdInput.placeholder = "Optional for cash payments";
            } else {
                transactionIdInput.setAttribute("required", "required");
                transactionIdInput.placeholder = "Reference ID after payment";
            }
        };
        
        paymentModeSelect.addEventListener("change", validateTransactionId);
        validateTransactionId(); // Initial validation
    };
    
    setupPaymentModeValidation(byId("enrollmentPaymentMode"), byId("enrollmentTransactionId"));

    byId("enrollmentForm").addEventListener("submit", async event => {
        event.preventDefault();
        const form = event.currentTarget;
        const payload = formData(form);
        payload.planId = Number(payload.planId);
        if (!payload.joinDate) {
            delete payload.joinDate;
        }

        try {
            const enrollment = await request(api.enrollment, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            showToast(`Enrolled ${enrollment.member.name} as ${enrollment.member.memberCode}`);
            if (enrollment.payment?.id) {
                await openReceipt(enrollment.payment.id, {
                    memberName: enrollment.member.name,
                    amount: enrollment.payment.amount,
                    paymentMode: enrollment.payment.paymentMode,
                    paymentDate: enrollment.payment.paymentDate
                });
            }
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

    byId("clearOfferButton").addEventListener("click", resetOfferForm);

    byId("offerForm").addEventListener("submit", async event => {
        event.preventDefault();
        const form = event.currentTarget;
        const payload = formData(form);
        payload.discountPercentage = Number(payload.discountPercentage || 0);
        payload.discountAmount = Number(payload.discountAmount || 0);
        if (payload.discountPercentage <= 0 && payload.discountAmount <= 0) {
            showToast("Enter discount % or cash off amount");
            return;
        }
        payload.active = byId("offerActive").checked;

        try {
            const method = payload.offerId ? "PUT" : "POST";
            const url = payload.offerId ? `${api.offers}/${payload.offerId}` : api.offers;
            const offer = await request(url, {
                method,
                body: JSON.stringify(payload)
            });
            showToast(`Offer ${offer.offerName || "saved"} updated`);
            resetOfferForm();
            await loadOffers();
            await loadStats();
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

function initials(value) {
    return String(value || "MM")
        .split(/\s+/)
        .filter(Boolean)
        .slice(0, 2)
        .map(part => part[0].toUpperCase())
        .join("");
}

function setupModalListeners() {
    const modal = byId("receiptModal");
    const closeButton = byId("closeReceiptModal");
    const shareButton = byId("shareWhatsApp");
    const downloadButton = byId("downloadReceipt");

    if (closeButton) {
        closeButton.addEventListener("click", () => {
            modal.hidden = true;
            byId("receiptFrame").src = "";
        });
    }

    if (downloadButton) {
        downloadButton.addEventListener("click", () => {
            const frame = byId("receiptFrame");
            if (frame.src) {
                const link = document.createElement("a");
                link.href = frame.src;
                link.download = "receipt.pdf";
                link.click();
            }
        });
    }

    if (shareButton) {
        shareButton.addEventListener("click", shareViaWhatsApp);
    }

    // Close modal when clicking outside
    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            modal.hidden = true;
            byId("receiptFrame").src = "";
        }
    });
}

function shareViaWhatsApp() {
    const frame = byId("receiptFrame");
    if (!frame.src) {
        showToast("No receipt available");
        return;
    }

    // Download the PDF first
    const link = document.createElement("a");
    link.href = frame.src;
    link.download = "receipt.pdf";
    link.click();

    // Show instructions
    showToast("PDF downloaded! Open WhatsApp and attach the receipt to share it.");
    
    // Optionally open WhatsApp after a short delay
    setTimeout(() => {
        const whatsappUrl = "https://wa.me/";
        window.open(whatsappUrl, "_blank");
    }, 1500);
}

async function init() {
    const loginForm = byId("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async event => {
            event.preventDefault();
            const data = formData(loginForm);
            try {
                const result = await login(data.username, data.password);
                showToast(`Logged in as ${result.username}`);
                await refreshAll();
            } catch (error) {
                showToast(error.message);
            }
        });
    }
    if (authToken) {
        document.body.classList.add("authenticated");
    }
    setClock();
    window.setInterval(setClock, 30000);
    wireForms();
    setupModalListeners();

    if (!authToken) {
        showToast("Login required");
        return;
    }

    try {
        const offerList = await request(api.offers);
        window.offers = offerList || [];
        await refreshAll();
    } catch (error) {
        showToast(error.message);
    }
}

init();







