<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
        <div class="section-picker">
            <c:choose>
                <c:when test="${empty sections}">
                    <p>No sections are available for this course yet.</p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="section" items="${sections}">
                        <label class="section-option" style="display:block; margin:0.75rem 0; border:1px solid #ddd; padding:0.75rem; border-radius:8px;">
                            <input type="radio" name="sectionId" value="${section.id}" required>
                            <span>
                                <strong>${section.sectionName}</strong><br>
                                ${section.days} • ${section.startTime} - ${section.endTime}<br>
                                ${section.location}<br>
                                ${section.numEnrolled} / ${section.maxSeats} enrolled
                            </span>
                        </label>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
        <button type="submit" class="btn-primary" ${empty sections ? 'disabled' : ''}>Confirm Registration</button>
    </form>

    <a href="/api/courses/${course.id}/info" class="redirect-link">Cancel</a>
</div>

</body>
</html>