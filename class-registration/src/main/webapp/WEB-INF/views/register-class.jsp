<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register for ${course.courseName}</title>
    <link rel="stylesheet" href="/css/register.css">
</head>
<body>

<div class="form-container">
    <h1>Confirm Registration</h1>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message"><%= request.getAttribute("error") %></div>
    <% } %>

    <div class="confirm-details">
        <p><strong>Course:</strong> ${course.courseName}</p>
        <p><strong>Code:</strong> ${course.courseCode}</p>
        <p><strong>Instructor:</strong>
            ${course.instructor.firstName} ${course.instructor.lastName}</p>
        <p><strong>Seats Available:</strong>
            ${course.maxSeats - course.numEnrolled}</p>
        <p><strong>Dates:</strong> ${course.startDate} → ${course.endDate}</p>
    </div>

    <form action="/api/courses/${course.id}/register" method="post">
        <button type="submit" class="btn-primary">Confirm Registration</button>
    </form>

    <a href="/api/courses/${course.id}/info" class="redirect-link">Cancel</a>
</div>

</body>
</html>