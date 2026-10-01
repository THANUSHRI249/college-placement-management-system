// Client-side JavaScript for College Placement Management System

const API_BASE = '/api';

// Global Data Caches
let state = {
    students: [],
    companies: [],
    jobs: [],
    applications: [],
    selectedStudentRegNo: null
};

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initForms();
    loadAllData();
});

// ==========================================
// NAVIGATION & TABS
// ==========================================
function initNavigation() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetTab = btn.getAttribute('data-tab');
            switchTab(targetTab);
        });
    });
}

function switchTab(tabId) {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

    const activeBtn = document.querySelector(`.tab-btn[data-tab="${tabId}"]`);
    const activeContent = document.getElementById(tabId);

    if (activeBtn) activeBtn.classList.add('active');
    if (activeContent) activeContent.classList.add('active');

    // Refresh specific tab views
    if (tabId === 'tab-home') loadDashboardStats();
    if (tabId === 'tab-student') populateStudentSelect();
    if (tabId === 'tab-analytics') loadPlacementAnalytics();
}

// ==========================================
// DATA LOADING
// ==========================================
async function loadAllData() {
    await Promise.all([
        fetchStudents(),
        fetchCompanies(),
        fetchJobs(),
        fetchApplications()
    ]);

    loadDashboardStats();
    populateStudentSelect();
    populateCompanySelect();
}

async function fetchStudents() {
    try {
        const res = await fetch(`${API_BASE}/students`);
        if (res.ok) {
            state.students = await res.json();
            renderStudentsTable();
            populateStudentSelect();
        }
    } catch (err) {
        console.error('Error fetching students:', err);
    }
}

async function fetchCompanies() {
    try {
        const res = await fetch(`${API_BASE}/companies`);
        if (res.ok) {
            state.companies = await res.json();
            renderCompaniesTable();
            populateCompanySelect();
        }
    } catch (err) {
        console.error('Error fetching companies:', err);
    }
}

async function fetchJobs() {
    try {
        const res = await fetch(`${API_BASE}/jobs`);
        if (res.ok) {
            state.jobs = await res.json();
            renderJobsTable();
        }
    } catch (err) {
        console.error('Error fetching jobs:', err);
    }
}

async function fetchApplications() {
    try {
        const res = await fetch(`${API_BASE}/applications`);
        if (res.ok) {
            state.applications = await res.json();
            renderApplicationsTable();
        }
    } catch (err) {
        console.error('Error fetching applications:', err);
    }
}

// ==========================================
// RENDERING FUNCTIONS
// ==========================================

function loadDashboardStats() {
    document.getElementById('home-stat-students').innerText = state.students.length;
    document.getElementById('home-stat-companies').innerText = state.companies.length;
    document.getElementById('home-stat-jobs').innerText = state.jobs.length;

    const selectedCount = state.applications.filter(a => a.status && a.status.toLowerCase() === 'selected').length;
    document.getElementById('home-stat-placements').innerText = selectedCount;
}

function renderStudentsTable() {
    const tbody = document.getElementById('students-table-body');
    if (!tbody) return;

    if (state.students.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;">No students registered yet.</td></tr>`;
        return;
    }

    tbody.innerHTML = state.students.map(s => `
        <tr>
            <td><strong>${escapeHtml(s.registerNumber)}</strong></td>
            <td>${escapeHtml(s.name)}</td>
            <td><span class="badge" style="background:#e0e7ff; color:#3730a3;">${escapeHtml(s.department)}</span></td>
            <td>${s.cgpa}</td>
            <td>${s.percentage}%</td>
            <td>${s.arrears}</td>
            <td><small>${escapeHtml(s.skills)}</small></td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="selectStudentForEligibility('${escapeHtml(s.registerNumber)}')">Check Eligibility</button>
            </td>
        </tr>
    `).join('');
}

function renderCompaniesTable() {
    const tbody = document.getElementById('companies-table-body');
    if (!tbody) return;

    if (state.companies.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;">No companies registered yet.</td></tr>`;
        return;
    }

    tbody.innerHTML = state.companies.map(c => `
        <tr>
            <td><strong>${escapeHtml(c.id)}</strong></td>
            <td>${escapeHtml(c.name)}</td>
            <td>${escapeHtml(c.industry || '-')}</td>
            <td>${escapeHtml(c.jobRole || '-')}</td>
        </tr>
    `).join('');
}

function renderJobsTable() {
    const tbody = document.getElementById('jobs-table-body');
    if (!tbody) return;

    if (state.jobs.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">No job openings published yet.</td></tr>`;
        return;
    }

    tbody.innerHTML = state.jobs.map(j => `
        <tr>
            <td><strong>${escapeHtml(j.title)}</strong></td>
            <td>${escapeHtml(j.companyName)}</td>
            <td><span class="badge" style="background:#f1f5f9; color:#334155;">${escapeHtml(j.requiredBranch)}</span></td>
            <td>${j.minCgpa}</td>
            <td>${j.minPercentage}%</td>
            <td>${j.maxArrears}</td>
            <td><small>${escapeHtml(j.requiredSkills || 'None')}</small></td>
        </tr>
    `).join('');
}

function populateStudentSelect() {
    const select = document.getElementById('eligibility-student-select');
    if (!select) return;

    const currentVal = select.value;
    select.innerHTML = '<option value="">-- Choose Student --</option>' +
        state.students.map(s => `<option value="${escapeHtml(s.registerNumber)}">${escapeHtml(s.name)} (${escapeHtml(s.registerNumber)} - ${escapeHtml(s.department)})</option>`).join('');

    if (currentVal) select.value = currentVal;
}

function populateCompanySelect() {
    const select = document.getElementById('j-company');
    if (!select) return;

    select.innerHTML = '<option value="">Select Company</option>' +
        state.companies.map(c => `<option value="${escapeHtml(c.name)}">${escapeHtml(c.name)}</option>`).join('');
}

// ==========================================
// AUTOMATED ELIGIBILITY MATCHING LOGIC
// ==========================================

function selectStudentForEligibility(regNo) {
    switchTab('tab-student');
    const select = document.getElementById('eligibility-student-select');
    if (select) {
        select.value = regNo;
        loadStudentEligibility(regNo);
    }
}

document.getElementById('eligibility-student-select')?.addEventListener('change', (e) => {
    const regNo = e.target.value;
    if (regNo) {
        loadStudentEligibility(regNo);
    } else {
        document.getElementById('student-selected-banner').classList.add('hidden');
        document.getElementById('eligibility-results-grid').innerHTML = `
            <div class="empty-state">
                Please select a student from the dropdown above to view eligible job opportunities.
            </div>
        `;
    }
});

async function loadStudentEligibility(regNo) {
    const student = state.students.find(s => s.registerNumber === regNo);
    const banner = document.getElementById('student-selected-banner');
    const container = document.getElementById('eligibility-results-grid');

    if (student && banner) {
        banner.classList.remove('hidden');
        banner.innerHTML = `
            <strong>Active Student Profile:</strong> ${escapeHtml(student.name)} | Reg No: <strong>${escapeHtml(student.registerNumber)}</strong> | Dept: <strong>${escapeHtml(student.department)}</strong> | CGPA: <strong>${student.cgpa}</strong> | Percentage: <strong>${student.percentage}%</strong> | Arrears: <strong>${student.arrears}</strong> | Skills: <strong>${escapeHtml(student.skills)}</strong>
        `;
    }

    try {
        const res = await fetch(`${API_BASE}/eligibility?studentRegNo=${encodeURIComponent(regNo)}`);
        if (!res.ok) throw new Error('Failed to evaluate eligibility');

        const results = await res.json();
        renderEligibilityCards(results, student);

    } catch (err) {
        console.error('Error fetching eligibility:', err);
        container.innerHTML = `<div class="empty-state">Error evaluating eligibility.</div>`;
    }
}

function renderEligibilityCards(evaluations, student) {
    const container = document.getElementById('eligibility-results-grid');
    if (!container) return;

    if (evaluations.length === 0) {
        container.innerHTML = `<div class="empty-state">No job opportunities found in system.</div>`;
        return;
    }

    container.innerHTML = evaluations.map(ev => {
        const isEligible = ev.eligible;
        const cardClass = isEligible ? 'eligible' : 'not-eligible';
        const badgeClass = isEligible ? 'badge-eligible' : 'badge-not-eligible';
        const statusText = isEligible ? 'Eligible' : 'Not Eligible';

        // Check if student already applied to this job
        const existingApp = state.applications.find(a => 
            a.studentRegNo === student.registerNumber && 
            a.jobTitle === ev.jobTitle && 
            a.companyName === ev.companyName
        );

        let actionButtonHtml = '';
        if (isEligible) {
            if (existingApp) {
                actionButtonHtml = `<span class="badge badge-applied">Status: ${escapeHtml(existingApp.status)}</span>`;
            } else {
                actionButtonHtml = `<button class="btn btn-accent btn-sm" onclick="applyStudentForJob('${escapeHtml(student.registerNumber)}', '${escapeHtml(ev.jobId)}')">Apply for Job</button>`;
            }
        } else {
            actionButtonHtml = `<span style="font-size:0.8rem; color:#94a3b8;">Not Eligible to Apply</span>`;
        }

        let reasonsHtml = '';
        if (!isEligible && ev.reasons && ev.reasons.length > 0) {
            reasonsHtml = `
                <div class="reasons-box">
                    <strong>Eligibility Issues:</strong>
                    <ul>
                        ${ev.reasons.map(r => `<li>• ${escapeHtml(r)}</li>`).join('')}
                    </ul>
                </div>
            `;
        }

        return `
            <div class="job-eval-card ${cardClass}">
                <div>
                    <div class="job-eval-header">
                        <div>
                            <div class="job-eval-title">${escapeHtml(ev.jobTitle)}</div>
                            <div class="job-eval-company">${escapeHtml(ev.companyName)}</div>
                        </div>
                        <span class="badge ${badgeClass}">${statusText}</span>
                    </div>

                    <ul class="criteria-list">
                        <li><strong>Req. Branch:</strong> ${escapeHtml(ev.requiredBranch)}</li>
                        <li><strong>Min CGPA:</strong> ${ev.minCgpa} | <strong>Min %:</strong> ${ev.minPercentage}%</li>
                        <li><strong>Max Arrears:</strong> ${ev.maxArrears}</li>
                        <li><strong>Req. Skills:</strong> ${escapeHtml(ev.requiredSkills || 'None')}</li>
                    </ul>

                    ${reasonsHtml}
                </div>

                <div style="margin-top: 12px; display: flex; justify-content: flex-end; align-items: center;">
                    ${actionButtonHtml}
                </div>
            </div>
        `;
    }).join('');
}

// Apply for Job
async function applyStudentForJob(studentRegNo, jobId) {
    try {
        const res = await fetch(`${API_BASE}/applications`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'apply', studentRegNo, jobId })
        });

        const data = await res.json();
        if (res.ok) {
            showToast('Application submitted successfully!');
            await fetchApplications();
            loadStudentEligibility(studentRegNo);
        } else {
            showToast(data.error || 'Failed to submit application', true);
        }
    } catch (err) {
        console.error('Error applying for job:', err);
        showToast('Server error while submitting application', true);
    }
}

// ==========================================
// FORM HANDLING
// ==========================================

function initForms() {
    // Student Form Submit
    document.getElementById('student-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            name: document.getElementById('s-name').value,
            registerNumber: document.getElementById('s-reg').value,
            department: document.getElementById('s-dept').value,
            cgpa: document.getElementById('s-cgpa').value,
            percentage: document.getElementById('s-pct').value,
            arrears: document.getElementById('s-arrears').value,
            skills: document.getElementById('s-skills').value,
            contactInfo: document.getElementById('s-contact').value
        };

        try {
            const res = await fetch(`${API_BASE}/students`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                showToast('Student profile saved successfully!');
                document.getElementById('student-form').reset();
                await fetchStudents();
                selectStudentForEligibility(payload.registerNumber);
            } else {
                const err = await res.json();
                showToast(err.error || 'Failed to save student', true);
            }
        } catch (err) {
            console.error(err);
            showToast('Network error while saving student', true);
        }
    });

    // Company Form Submit
    document.getElementById('company-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            name: document.getElementById('c-name').value,
            industry: document.getElementById('c-industry').value,
            jobRole: document.getElementById('c-role').value
        };

        try {
            const res = await fetch(`${API_BASE}/companies`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                showToast('Company added successfully!');
                document.getElementById('company-form').reset();
                await fetchCompanies();
            } else {
                const err = await res.json();
                showToast(err.error || 'Failed to add company', true);
            }
        } catch (err) {
            console.error(err);
            showToast('Network error while adding company', true);
        }
    });

    // Job Form Submit
    document.getElementById('job-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            title: document.getElementById('j-title').value,
            companyName: document.getElementById('j-company').value,
            requiredBranch: document.getElementById('j-branch').value,
            minCgpa: document.getElementById('j-cgpa').value,
            minPercentage: document.getElementById('j-pct').value,
            maxArrears: document.getElementById('j-arrears').value,
            requiredSkills: document.getElementById('j-skills').value
        };

        try {
            const res = await fetch(`${API_BASE}/jobs`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                showToast('Job opportunity published successfully!');
                document.getElementById('job-form').reset();
                await fetchJobs();
                if (state.selectedStudentRegNo) {
                    loadStudentEligibility(state.selectedStudentRegNo);
                }
            } else {
                const err = await res.json();
                showToast(err.error || 'Failed to publish job', true);
            }
        } catch (err) {
            console.error(err);
            showToast('Network error while publishing job', true);
        }
    });

    // Generate Report Button
    document.getElementById('btn-generate-report')?.addEventListener('click', loadPlacementAnalytics);
}

// ==========================================
// PLACEMENT ANALYTICS & REPORTING
// ==========================================

async function loadPlacementAnalytics() {
    try {
        const res = await fetch(`${API_BASE}/reports`);
        if (!res.ok) throw new Error('Failed to generate report');

        const report = await res.json();

        // Update Summary Cards
        document.getElementById('rep-total-students').innerText = report.totalStudents;
        document.getElementById('rep-total-apps').innerText = report.totalApplications;
        document.getElementById('rep-shortlisted').innerText = report.totalShortlisted;
        document.getElementById('rep-selected').innerText = report.totalSelected;
        document.getElementById('rep-rejected').innerText = report.totalRejected;

        state.applications = report.applications || [];
        renderApplicationsTable();

        showToast('Placement report refreshed successfully!');
    } catch (err) {
        console.error('Error loading analytics:', err);
        showToast('Error loading placement report', true);
    }
}

function renderApplicationsTable() {
    const tbody = document.getElementById('applications-table-body');
    if (!tbody) return;

    if (state.applications.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">No job applications submitted yet.</td></tr>`;
        return;
    }

    tbody.innerHTML = state.applications.map(a => {
        let badgeClass = 'badge-applied';
        const st = (a.status || 'Applied').toLowerCase();
        if (st === 'shortlisted') badgeClass = 'badge-shortlisted';
        if (st === 'selected') badgeClass = 'badge-selected';
        if (st === 'rejected') badgeClass = 'badge-rejected';

        return `
            <tr>
                <td><strong>${escapeHtml(a.id)}</strong></td>
                <td>${escapeHtml(a.studentName)}</td>
                <td>${escapeHtml(a.studentRegNo)}</td>
                <td>${escapeHtml(a.companyName)}</td>
                <td>${escapeHtml(a.jobTitle)}</td>
                <td><span class="badge ${badgeClass}">${escapeHtml(a.status)}</span></td>
                <td>
                    <select class="status-select" onchange="updateAppStatus('${escapeHtml(a.id)}', this.value)">
                        <option value="Applied" ${a.status === 'Applied' ? 'selected' : ''}>Applied</option>
                        <option value="Shortlisted" ${a.status === 'Shortlisted' ? 'selected' : ''}>Shortlisted</option>
                        <option value="Selected" ${a.status === 'Selected' ? 'selected' : ''}>Selected</option>
                        <option value="Rejected" ${a.status === 'Rejected' ? 'selected' : ''}>Rejected</option>
                    </select>
                </td>
            </tr>
        `;
    }).join('');
}

async function updateAppStatus(appId, newStatus) {
    try {
        const res = await fetch(`${API_BASE}/applications`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'updateStatus', applicationId: appId, status: newStatus })
        });

        if (res.ok) {
            showToast(`Status updated to ${newStatus}`);
            await fetchApplications();
            loadDashboardStats();
        } else {
            showToast('Failed to update status', true);
        }
    } catch (err) {
        console.error(err);
        showToast('Error updating status', true);
    }
}

// Helper utilities
function showToast(msg, isError = false) {
    const toast = document.getElementById('toast-message');
    if (!toast) return;

    toast.innerText = msg;
    toast.style.backgroundColor = isError ? '#dc2626' : '#1e293b';
    toast.classList.remove('hidden');

    setTimeout(() => {
        toast.classList.add('hidden');
    }, 3000);
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
