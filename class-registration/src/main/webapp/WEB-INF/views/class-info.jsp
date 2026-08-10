<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${course.courseName}</title>
    <link rel="stylesheet" href="/css/navbar.css">
</head>
<body>

<nav class="navbar">
    <span class="nav-brand">Class Registration</span>
    <div class="nav-links">
        <% 
            String role = (String) session.getAttribute("role");
        %>

        <% if ("STUDENT".equals(role)) { %>
            <a href="/student/home">My Classes</a>
        <% } else if ("INSTRUCTOR".equals(role)) { %>
            <a href="/instructor/home">My Courses</a>
        <% } %>

        <a href="/api/courses/display">Browse Courses</a>

        <% if ("STUDENT".equals(role)) { %>
            <a href="/student/logout">Logout</a>
        <% } else if ("INSTRUCTOR".equals(role)) { %>
            <a href="/instructor/logout">Logout</a>
        <% } %>
    </div>
</nav>
</nav>

<div class="page-container">
    <a href="/api/courses/display" class="back-link">← Back to courses</a>

    <div class="info-card">
        <div class="info-header">
            <div>
                <span class="course-code">${course.courseCode}</span>
                <h1>${course.courseName}</h1>
            </div>
            <span class="seats ${course.numEnrolled >= course.maxSeats ? 'full' : 'open'}">
                ${course.numEnrolled} / ${course.maxSeats} enrolled
            </span>
        </div>

        <div class="info-body">
            <div class="info-row">
                <span class="label">Instructor</span>
                <span>${course.instructor.firstName} ${course.instructor.lastName}</span>
            </div>
            <div class="info-row">
                <span class="label">Description</span>
                <span>${course.description}</span>
            </div>
            <div class="info-row">
                <span class="label">Start Date</span>
                <span>${course.startDate}</span>
            </div>
            <div class="info-row">
                <span class="label">End Date</span>
                <span>${course.endDate}</span>
            </div>
            <div class="info-row">
                <span class="label">Max Seats</span>
                <span>${course.maxSeats}</span>
            </div>
        </div>

        <div class="info-actions">
            <% if ("STUDENT".equals(role)) { %>

                <c:choose>
                    <c:when test="${course.numEnrolled >= course.maxSeats}">
                        <form action="/api/courses/waitlist" method="post">
                            <input type="hidden" name="courseId" value="${course.id}">
                            <input type="hidden" name="studentId" value="${session.studentId}">
                            <button type="submit" class="btn-primary">
                                Join Waitlist
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a href="/api/courses/${course.id}/register"
                        class="btn-primary">
                            Register for this Course
                        </a>
                    </c:otherwise>
                </c:choose>

            <% } else if ("INSTRUCTOR".equals(role)) { %>

                <span style="color:#888;font-size:0.9rem;">
                    Viewing as instructor — registration not available
                </span>

            <% } %>
        </div>
    </div>
</div>

</body>
</html>