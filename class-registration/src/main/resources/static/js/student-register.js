document.getElementById('studentRegisterForm')
    .addEventListener('submit', function(e) {

    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const phone = document.getElementById('phone').value;

    // Check passwords match before submitting
    if (password !== confirmPassword) {
        e.preventDefault();
        showError('Passwords do not match');
        return;
    }

    // Basic phone format check
    const phoneRegex = /^\d{3}-?\d{3}-?\d{4}$/;
    if (!phoneRegex.test(phone)) {
        e.preventDefault();
        showError('Please enter a valid phone number (e.g. 555-867-5309)');
        return;
    }
});

function showError(message) {
    // Remove any existing error first
    const existing = document.querySelector('.error-message');
    if (existing) existing.remove();

    const error = document.createElement('div');
    error.className = 'error-message';
    error.textContent = message;

    const form = document.getElementById('studentRegisterForm');
    form.parentNode.insertBefore(error, form);
}