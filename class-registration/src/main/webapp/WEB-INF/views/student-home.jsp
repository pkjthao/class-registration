<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Home</title>
    <link rel="stylesheet" href="/css/navbar.css">
</head>
<body>

<nav class="navbar">
    <span class="nav-brand">Class Registration</span>
    <div class="nav-links">
        <a href="/student/home" class="active">My Classes</a>
        <a href="/api/courses/display">Browse Courses</a>
        <a href="/student/logout">Logout</a>
    </div>
</nav>

<div class="page-container">
    <h1>Welcome, ${studentName}</h1>

    <h2>My Registered Classes</h2>

    <c:choose>
        <c:when test="${empty registrations}">
            <div class="empty-state">
                <p>You are not registered for any classes yet.</p>
                <a href="/api/courses/display" class="btn-primary">Browse Courses</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="course-grid">
                <c:forEach var="reg" items="${registrations}">
                    <div class="course-card ${reg.status == 'DROPPED' ? 'dropped' : ''}">
                        <div class="course-header">
                            <span class="course-code">${reg.course.courseCode}</span>
                            <span class="status-badge ${reg.status}">${reg.status}</span>
                        </div>
                        <h3>${reg.course.courseName}</h3>
                        <p class="instructor">
                            ${reg.course.instructor.firstName} ${reg.course.instructor.lastName}
                        </p>
                        <p class="seats">${reg.course.numEnrolled} / ${reg.course.maxSeats} seats</p>
                        <div class="card-actions">
                            <a href="/api/courses/${reg.course.id}/info"
                               class="btn-secondary">View Info</a>
                            <c:if test="${reg.status == 'ACTIVE'}">
                                <form action="/student/drop" method="post" style="display:inline">
                                    <input type="hidden" name="courseId"
                                           value="${reg.course.id}">
                                    <button type="submit" class="btn-danger"
                                            onclick="return confirm('Drop this course?')">
                                        Drop
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="/js/student-home.js"></script>
</body>
</html>