// ── Live Preview ──────────────────────────────────────
const fields = {
    courseName: document.getElementById('courseName'),
    courseCode: document.getElementById('courseCode'),
    maxSeats:   document.getElementById('maxSeats'),
    startDate:  document.getElementById('startDate'),
    endDate:    document.getElementById('endDate'),
};

const preview = {
    name:  document.getElementById('previewName'),
    code:  document.getElementById('previewCode'),
    seats: document.getElementById('previewSeats'),
    dates: document.getElementById('previewDates'),
};

function formatDate(value) {
    if (!value) return '—';
    const d = new Date(value);
    return d.toLocaleDateString('en-US', {
        year: 'numeric', month: 'short', day: 'numeric'
    });
}

function updatePreview() {
    preview.name.textContent  = fields.courseName.value || '—';
    preview.code.textContent  = fields.courseCode.value.toUpperCase() || '—';
    preview.seats.textContent = fields.maxSeats.value
        ? fields.maxSeats.value + ' seats'
        : '—';

    const start = formatDate(fields.startDate.value);
    const end   = formatDate(fields.endDate.value);
    preview.dates.textContent = (fields.startDate.value || fields.endDate.value)
        ? `${start} → ${end}`
        : '—';
}

Object.values(fields).forEach(f =>
    f.addEventListener('input', updatePreview)
);

// Auto-uppercase course code as user types
fields.courseCode.addEventListener('input', function() {
    const pos = this.selectionStart;
    this.value = this.value.toUpperCase();
    this.setSelectionRange(pos, pos);
});

// ── Form Validation ───────────────────────────────────
document.getElementById('addCourseForm')
    .addEventListener('submit', function(e) {

    let valid = true;

    // Clear previous errors
    document.querySelectorAll('.invalid').forEach(el =>
        el.classList.remove('invalid')
    );
    const existingError = document.querySelector('.validation-error');
    if (existingError) existingError.remove();

    // Course code format check (letters, numbers, hyphens only)
    const codeRegex = /^[A-Z0-9\-]{2,10}$/;
    if (!codeRegex.test(fields.courseCode.value.toUpperCase())) {
        fields.courseCode.classList.add('invalid');
        showError('Course code must be 2-10 characters (letters, numbers, hyphens only)');
        valid = false;
    }

    // Max seats must be at least 1
    if (parseInt(fields.maxSeats.value) < 1) {
        fields.maxSeats.classList.add('invalid');
        showError('Max seats must be at least 1');
        valid = false;
    }

    // End date must be after start date
    if (fields.startDate.value && fields.endDate.value) {
        const start = new Date(fields.startDate.value);
        const end   = new Date(fields.endDate.value);
        if (end <= start) {
            fields.endDate.classList.add('invalid');
            showError('End date must be after the start date');
            valid = false;
        }
    }

    if (!valid) e.preventDefault();
});

function showError(message) {
    const existing = document.querySelector('.validation-error');
    if (existing) existing.remove();

    const div = document.createElement('div');
    div.className = 'error-message validation-error';
    div.textContent = message;

    const form = document.getElementById('addCourseForm');
    form.insertBefore(div, form.firstChild);
    div.scrollIntoView({ behavior: 'smooth', block: 'center' });
}