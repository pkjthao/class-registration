<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Instructor Home</title>
    <link rel="stylesheet" href="/css/navbar.css">
    <link rel="stylesheet" href="/css/instructor-home.css">
</head>
<body>

<nav class="navbar">
    <span class="nav-brand">Class Registration</span>
    <div class="nav-links">
        <a href="/instructor/home" class="active">My Courses</a>
        <a href="/api/courses/display">Browse Courses</a>
        <a href="/instructor/logout">Logout</a>
    </div>
</nav>

<div class="page-container">

    <div class="page-header">
        <div>
            <h1>Welcome, ${instructorName}</h1>
            <h2>My Courses</h2>
        </div>
        <a href="/instructor/add-course" class="btn-primary">+ Add Course</a>
    </div>

    <%-- Success and error banners --%>
    <% if ("true".equals(request.getParameter("added"))) { %>
        <div class="success-banner">Course successfully added to the catalogue.</div>
    <% } %>
    <% if ("true".equals(request.getParameter("deleted"))) { %>
        <div class="success-banner">Course successfully removed.</div>
    <% } %>
    <% if ("unauthorized".equals(request.getParameter("error"))) { %>
        <div class="error-banner">You can only delete your own courses.</div>
    <% } %>
    <% if ("notfound".equals(request.getParameter("error"))) { %>
        <div class="error-banner">Course not found.</div>
    <% } %>

    <c:choose>
        <c:when test="${empty courses}">
            <div class="empty-state">
                <p>You have not created any courses yet.</p>
                <a href="/instructor/add-course" class="btn-primary">
                    + Add Your First Course
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="course-grid">
                <c:forEach var="course" items="${courses}">
                    <div class="course-card">
                        <div class="course-header">
                            <span class="course-code">${course.courseCode}</span>
                            <span class="seat-count">
                                ${course.numEnrolled} / ${course.maxSeats} enrolled
                            </span>
                        </div>

                        <h3>${course.courseName}</h3>
                        <p class="description">${course.description}</p>
                        <p class="dates">${course.startDate} → ${course.endDate}</p>

                        <%-- Enrollment bar --%>
                        <div class="enrollment-bar">
                            <div class="enrollment-fill"
                                 style="width: '${(course.numEnrolled / course.maxSeats) * 100}%'">
                            </div>
                        </div>

                        <div class="card-actions">
                            <a href="/api/courses/${course.id}/info"
                            class="btn-secondary">View</a>
                            <button class="btn-secondary"
                                    onclick="printStudents('${course.id}',
                                            '${course.courseName}')">
                                Print Roster
                            </button>
                            <%-- NEW: Notify button --%>
                            <button class="btn-notify"
                                    onclick="openEmailModal('${course.id}',
                                            '${course.courseName}')">
                                ✉ Notify
                            </button>
                            <form action="/instructor/course/${course.id}/delete"
                                method="post"
                                style="display:inline"
                                onsubmit="return confirmDelete('${course.courseName}',
                                            '${course.numEnrolled}')">
                                <button type="submit" class="btn-danger">Remove</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Print Modal -->
<div id="printModal" class="modal hidden">
    <div class="modal-content">
        <div class="modal-header">
            <h2 id="modalTitle">Course Roster</h2>
            <button onclick="closeModal()">✕</button>
        </div>
        <div id="rosterContent"></div>
        <div class="modal-footer">
            <button onclick="window.print()" class="btn-primary">Print</button>
            <button onclick="closeModal()" class="btn-secondary">Close</button>
        </div>
    </div>
</div>

<%-- Email Modal --%>
<div id="emailModal" class="modal hidden">
    <div class="modal-content email-modal-content">

        <div class="modal-header">
            <h2 id="emailModalTitle">Notify Students</h2>
            <button onclick="closeEmailModal()">✕</button>
        </div>

        <div class="modal-body">

            <%-- Recipient list --%>
            <div class="recipient-section">
                <label class="section-label">
                    Recipients
                    <span id="recipientCount" class="recipient-count">0 students</span>
                </label>
                <div id="recipientList" class="recipient-list"></div>
            </div>

            <%-- Subject line --%>
            <div class="email-field">
                <label for="emailSubject" class="section-label">Subject</label>
                <input type="text"
                       id="emailSubject"
                       class="email-input"
                       placeholder="Email subject...">
            </div>

            <%-- Body editor --%>
            <div class="email-field">
                <label class="section-label">
                    Message Body
                    <span class="field-hint">HTML is supported</span>
                </label>
                <div class="editor-toolbar">
                    <button type="button" onclick="formatText('bold')"
                            title="Bold"><b>B</b></button>
                    <button type="button" onclick="formatText('italic')"
                            title="Italic"><i>I</i></button>
                    <button type="button" onclick="formatText('underline')"
                            title="Underline"><u>U</u></button>
                    <span class="toolbar-divider"></span>
                    <button type="button" onclick="togglePreview()"
                            id="previewToggle">Preview</button>
                </div>
                <textarea id="emailBody"
                          class="email-body-input"
                          placeholder="Write your message here..."></textarea>
                <div id="emailPreviewPane"
                     class="email-preview-pane hidden"></div>
            </div>

            <%-- Error/success inside modal --%>
            <div id="emailError"  class="modal-error  hidden"></div>
            <div id="emailSuccess" class="modal-success hidden"></div>

        </div>

        <div class="modal-footer">
            <span id="sendingSpinner" class="spinner hidden">Sending...</span>
            <button onclick="closeEmailModal()" class="btn-secondary">Cancel</button>
            <button onclick="sendEmail()" class="btn-primary" id="sendEmailBtn">
                Send to All Students
            </button>
        </div>

    </div>
</div>

<script src="/js/instructor-home.js"></script>
</body>
</html>