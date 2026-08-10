document.getElementById('studentLoginForm')
    .addEventListener('submit', function(e) {

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    if (!email || !password) {
        e.preventDefault();
        showError('Please fill in all fields');
    }
});

function showError(message) {
    const existing = document.querySelector('.error-message');
    if (existing) existing.remove();

    const error = document.createElement('div');
    error.className = 'error-message';
    error.textContent = message;

    const form = document.getElementById('studentLoginForm');
    form.parentNode.insertBefore(error, form);
}