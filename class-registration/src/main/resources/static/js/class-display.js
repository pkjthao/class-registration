// Client-side filter for open seats only
document.querySelectorAll('.filter-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.filter-btn')
            .forEach(b => b.classList.remove('active'));
        this.classList.add('active');

        const filter = this.dataset.filter;
        document.querySelectorAll('.course-card').forEach(card => {
            if (filter === 'available') {
                card.style.display =
                    parseInt(card.dataset.seats) > 0 ? 'block' : 'none';
            } else {
                card.style.display = 'block';
            }
        });
    });
});