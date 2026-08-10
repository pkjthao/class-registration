<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Browse Courses</title>
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

        <a href="/api/courses/display" class="active">Browse Courses</a>

        <% if ("STUDENT".equals(role)) { %>
            <a href="/student/logout">Logout</a>
        <% } else if ("INSTRUCTOR".equals(role)) { %>
            <a href="/instructor/logout">Logout</a>
        <% } %>
    </div>
</nav>

<div class="page-container">
    <h1>Available Courses</h1>

    <!-- Filter Bar -->
    <div class="filter-bar">
        <form action="/api/courses/display" method="get" id="filterForm">
            <input type="text" name="search" placeholder="Search by course name..."
                   value="${search}" class="search-input">
            <button type="submit" class="btn-primary">Search</button>
            <a href="/api/courses/display" class="btn-secondary">Clear</a>
        </form>
        <div class="filter-options">
            <label>Show: </label>
            <button class="filter-btn active" data-filter="all">All</button>
            <button class="filter-btn" data-filter="available">Open Seats Only</button>
        </div>
    </div>

    <!-- Course Count -->
    <p class="result-count">${courses.size()} course(s) found</p>

    <!-- Course List -->
    <c:choose>
        <c:when test="${empty courses}">
            <div class="empty-state">
                <p>No courses found matching your search.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="course-grid" id="courseGrid">
                <c:forEach var="course" items="${courses}">
                    <div class="course-card"
                         data-seats="${course.maxSeats - course.numEnrolled}">
                        <div class="course-header">
                            <span class="course-code">${course.courseCode}</span>
                            <span class="seats ${course.numEnrolled >= course.maxSeats
                                ? 'full' : 'open'}">
                                <c:choose>
                                    <c:when test="${course.numEnrolled >= course.maxSeats}">
                                        FULL
                                    </c:when>
                                    <c:otherwise>
                                        ${course.maxSeats - course.numEnrolled} seats left
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <h3>${course.courseName}</h3>
                        <p class="instructor">
                            ${course.instructor.firstName} ${course.instructor.lastName}
                        </p>
                        <p class="description">${course.description}</p>
                        <p class="dates">${course.startDate} → ${course.endDate}</p>
                        <a href="/api/courses/${course.id}/info"
                           class="btn-primary">View & Register</a>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="/js/class-display.js"></script>
</body>
</html>