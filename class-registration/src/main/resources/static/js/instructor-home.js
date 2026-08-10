// ── Print Roster ──────────────────────────────────────
function printStudents(courseId, courseName) {
    fetch(`/instructor/course/${courseId}/students`)
        .then(res => res.json())
        .then(registrations => {
            const active = registrations.filter(r => r.status === 'ACTIVE');

            let html = `<p style="color:#666; margin-bottom:1rem;">
                            ${active.length} active student(s) enrolled
                        </p>`;

            if (active.length === 0) {
                html += `<p style="color:#888; text-align:center; padding:2rem">
                             No students enrolled yet.
                         </p>`;
            } else {
                html += `<table class="roster-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>`;

                active.forEach((reg, i) => {
                    html += `<tr>
                        <td>${i + 1}</td>
                        <td>${reg.firstName} ${reg.lastName}</td>
                        <td>${reg.email}</td>
                        <td>${reg.phone}</td>
                        <td>${reg.status}</td>
                    </tr>`;
                });

                html += `</tbody></table>`;
            }

            document.getElementById('modalTitle').textContent =
                `${courseName} — Roster`;
            document.getElementById('rosterContent').innerHTML = html;
            document.getElementById('printModal').classList.remove('hidden');
        })
        .catch(() => {
            alert('Could not load roster. Please try again.');
        });
}

function closeModal() {
    document.getElementById('printModal').classList.add('hidden');
}

// ── Confirm Delete ────────────────────────────────────
function confirmDelete(courseName, numEnrolled) {
    if (numEnrolled > 0) {
        alert(
            `Cannot remove "${courseName}" — there are ${numEnrolled} ` +
            `student(s) currently enrolled. All students must drop the ` +
            `course before it can be removed.`
        );
        return false;
    }
    return confirm(
        `Are you sure you want to remove "${courseName}"? ` +
        `This action cannot be undone.`
    );
}

// ── Auto-dismiss banners after 4 seconds ──────────────
document.addEventListener('DOMContentLoaded', () => {
    const banners = document.querySelectorAll(
        '.success-banner, .error-banner'
    );
    banners.forEach(banner => {
        setTimeout(() => {
            banner.style.transition = 'opacity 0.5s';
            banner.style.opacity = '0';
            setTimeout(() => banner.remove(), 500);
        }, 4000);
    });
});

// ── Email Modal State ─────────────────────────────
let currentCourseId = null;

// ── Open Email Modal ──────────────────────────────
function openEmailModal(courseId, courseName) {
    currentCourseId = courseId;

    // Reset modal state
    document.getElementById('emailError').classList.add('hidden');
    document.getElementById('emailSuccess').classList.add('hidden');
    document.getElementById('sendEmailBtn').disabled = false;
    document.getElementById('sendingSpinner').classList.add('hidden');
    document.getElementById('emailPreviewPane').classList.add('hidden');
    document.getElementById('emailBody').classList.remove('hidden');
    document.getElementById('previewToggle').textContent = 'Preview';

    document.getElementById('emailModalTitle').textContent =
        `Notify Students — ${courseName}`;

    // Show modal with loading state
    document.getElementById('recipientList').innerHTML =
        '<span style="color:#888; font-size:0.85rem">Loading...</span>';
    document.getElementById('emailModal').classList.remove('hidden');

    // Fetch preview data from server
    fetch(`/instructor/course/${courseId}/email-preview`)
        .then(res => {
            if (!res.ok) throw new Error('Failed to load email data');
            return res.json();
        })
        .then(data => {
            // Populate recipients
            const list = document.getElementById('recipientList');
            if (data.emails.length === 0) {
                list.innerHTML =
                    '<span style="color:#888">No active students enrolled</span>';
                document.getElementById('sendEmailBtn').disabled = true;
            } else {
                list.innerHTML = data.studentNames
                    .map(name => `<span class="recipient-chip">${name}</span>`)
                    .join('');
            }

            document.getElementById('recipientCount').textContent =
                `${data.recipientCount} student${data.recipientCount !== 1 ? 's' : ''}`;
            document.getElementById('emailSubject').value = data.subject;
            document.getElementById('emailBody').value   = data.body;
        })
        .catch(err => {
            showModalError('Could not load email data. Please try again.');
        });
}

// ── Close Email Modal ─────────────────────────────
function closeEmailModal() {
    document.getElementById('emailModal').classList.add('hidden');
    currentCourseId = null;
}

// ── Toggle HTML Preview ───────────────────────────
function togglePreview() {
    const textarea    = document.getElementById('emailBody');
    const previewPane = document.getElementById('emailPreviewPane');
    const btn         = document.getElementById('previewToggle');

    if (previewPane.classList.contains('hidden')) {
        previewPane.innerHTML = textarea.value;
        previewPane.classList.remove('hidden');
        textarea.classList.add('hidden');
        btn.textContent = 'Edit';
    } else {
        previewPane.classList.add('hidden');
        textarea.classList.remove('hidden');
        btn.textContent = 'Preview';
    }
}

// ── Basic text formatting ─────────────────────────
function formatText(command) {
    const textarea = document.getElementById('emailBody');
    const start    = textarea.selectionStart;
    const end      = textarea.selectionEnd;
    const selected = textarea.value.substring(start, end);

    const tags = {
        bold:      ['<strong>', '</strong>'],
        italic:    ['<em>', '</em>'],
        underline: ['<u>', '</u>'],
    };

    const [open, close] = tags[command];
    const replacement   = `${open}${selected}${close}`;

    textarea.value =
        textarea.value.substring(0, start) +
        replacement +
        textarea.value.substring(end);

    // Restore cursor position after the inserted tags
    const newPos = start + replacement.length;
    textarea.setSelectionRange(newPos, newPos);
    textarea.focus();
}

// ── Send Email ────────────────────────────────────
function sendEmail() {
    const subject = document.getElementById('emailSubject').value.trim();
    const body    = document.getElementById('emailBody').value.trim();

    // Client-side validation
    if (!subject) {
        showModalError('Please enter a subject line');
        return;
    }
    if (!body) {
        showModalError('Please write a message body');
        return;
    }

    // Show sending state
    document.getElementById('sendEmailBtn').disabled = true;
    document.getElementById('sendingSpinner').classList.remove('hidden');
    document.getElementById('emailError').classList.add('hidden');
    document.getElementById('emailSuccess').classList.add('hidden');

    fetch(`/instructor/course/${currentCourseId}/send-email`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ subject, body })
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById('sendingSpinner').classList.add('hidden');

        if (data.error) {
            showModalError(data.error);
            document.getElementById('sendEmailBtn').disabled = false;
        } else {
            showModalSuccess(data.message);
            // Auto-close after 3 seconds on success
            setTimeout(closeEmailModal, 3000);
        }
    })
    .catch(() => {
        document.getElementById('sendingSpinner').classList.add('hidden');
        document.getElementById('sendEmailBtn').disabled = false;
        showModalError('Network error. Please try again.');
    });
}

// ── Show feedback inside modal ────────────────────
function showModalError(message) {
    const el = document.getElementById('emailError');
    el.textContent = message;
    el.classList.remove('hidden');
    document.getElementById('emailSuccess').classList.add('hidden');
}

function showModalSuccess(message) {
    const el = document.getElementById('emailSuccess');
    el.textContent = message;
    el.classList.remove('hidden');
    document.getElementById('emailError').classList.add('hidden');
}

// Close modal when clicking outside it
document.getElementById('emailModal')
    .addEventListener('click', function(e) {
        if (e.target === this) closeEmailModal();
    });



function closeModal() {
    document.getElementById('printModal').classList.add('hidden');
}

function confirmDelete(courseName, numEnrolled) {
    if (numEnrolled > 0) {
        alert(
            `Cannot remove "${courseName}" — ${numEnrolled} student(s) ` +
            `are enrolled. All students must drop first.`
        );
        return false;
    }
    return confirm(
        `Are you sure you want to remove "${courseName}"? ` +
        `This cannot be undone.`
    );
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.success-banner, .error-banner')
        .forEach(banner => {
            setTimeout(() => {
                banner.style.transition = 'opacity 0.5s';
                banner.style.opacity = '0';
                setTimeout(() => banner.remove(), 500);
            }, 4000);
        });
});