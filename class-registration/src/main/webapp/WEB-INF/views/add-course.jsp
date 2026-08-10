<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Browse Courses</title>
    <link rel="stylesheet" href="/css/add-course.css">
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

<div class="page-container">
    <div class="form-card">

        <div class="form-card-header">
            <h1>Add New Course</h1>
            <p>Fill out the details below to add a course to the catalogue.</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message"><%= request.getAttribute("error") %></div>
        <% } %>

        <form id="addCourseForm" action="/instructor/add-course" method="post">

            <div class="form-row">
                <div class="form-group">
                    <label for="courseName">Course Name <span class="required">*</span></label>
                    <input type="text" id="courseName" name="courseName"
                           placeholder="e.g. Introduction to Computer Science"
                           required>
                </div>
                <div class="form-group">
                    <label for="courseCode">Course Code <span class="required">*</span></label>
                    <input type="text" id="courseCode" name="courseCode"
                           placeholder="e.g. CS-101"
                           maxlength="10"
                           required>
                    <span class="field-hint">Will be converted to uppercase</span>
                </div>
            </div>

            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" name="description"
                          placeholder="Brief description of the course..."
                          rows="4"></textarea>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="maxSeats">Max Seats <span class="required">*</span></label>
                    <input type="number" id="maxSeats" name="maxSeats"
                           min="1" max="500"
                           placeholder="e.g. 30"
                           required>
                </div>
                <div class="form-group">
                    <!-- Empty column for layout balance -->
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="startDate">Start Date <span class="required">*</span></label>
                    <input type="datetime-local" id="startDate" name="startDate" required>
                </div>
                <div class="form-group">
                    <label for="endDate">End Date <span class="required">*</span></label>
                    <input type="datetime-local" id="endDate" name="endDate" required>
                </div>
            </div>

            <div class="form-preview" id="formPreview">
                <h3>Preview</h3>
                <div class="preview-grid">
                    <span class="preview-label">Course Name:</span>
                    <span id="previewName">—</span>
                    <span class="preview-label">Course Code:</span>
                    <span id="previewCode">—</span>
                    <span class="preview-label">Max Seats:</span>
                    <span id="previewSeats">—</span>
                    <span class="preview-label">Dates:</span>
                    <span id="previewDates">—</span>
                </div>
            </div>

            <div class="form-actions">
                <a href="/instructor/home" class="btn-secondary">Cancel</a>
                <button type="submit" class="btn-primary">Add Course</button>
            </div>

        </form>
    </div>
</div>

<script src="/js/add-course.js"></script>
</body>
</html>