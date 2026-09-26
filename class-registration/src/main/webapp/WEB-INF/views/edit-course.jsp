<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Course Sections</title>
    <link rel="stylesheet" href="/css/navbar.css">
</head>
<body>

<nav class="navbar">
    <span class="nav-brand">Class Registration</span>
    <div class="nav-links">
        <a href="/instructor/home">My Courses</a>
        <a href="/api/courses/display">Browse Courses</a>
        <a href="/instructor/logout">Logout</a>
    </div>
</nav>

<div class="page-container">
    <a href="/instructor/home" class="back-link">← Back to My Courses</a>

    <h1>${course.courseName}</h1>
    <p><strong>Course code:</strong> ${course.courseCode}</p>

    <% if ("true".equals(request.getParameter("sectionAdded"))) { %>
        <div class="success-banner">Section successfully added.</div>
    <% } %>
    <% if ("true".equals(request.getParameter("sectionUpdated"))) { %>
        <div class="success-banner">Section successfully updated.</div>
    <% } %>
    <% if ("true".equals(request.getParameter("courseUpdated"))) { %>
        <div class="success-banner">Course successfully updated.</div>
    <% } %>
    <% if ("true".equals(request.getParameter("sectionRemoved"))) { %>
        <div class="success-banner">Section successfully removed.</div>
    <% } %>
    <% if ("true".equals(request.getParameter("gradeSaved"))) { %>
        <div class="success-banner">Roster grade saved.</div>
    <% } %>

    <h2>Edit Course</h2>
    <form action="/instructor/course/${course.id}/update" method="post" style="background:#f7f7f7; padding:1rem; border-radius:12px; margin-bottom:2rem; max-width:700px;">
        <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:1rem;">
            <div>
                <label>Course Name</label><br>
                <input type="text" name="courseName" value="${course.courseName}" required>
            </div>
            <div>
                <label>Course Code</label><br>
                <input type="text" name="courseCode" value="${course.courseCode}" required>
            </div>
            <div style="grid-column: 1 / -1;">
                <label>Description</label><br>
                <textarea name="description" rows="3" style="width:100%;">${course.description}</textarea>
            </div>
            <div>
                <label>Seat Capacity</label><br>
                <input type="number" name="maxSeats" min="1" value="${course.maxSeats}" required>
            </div>
            <div>
                <label>Start Date</label><br>
                <input type="datetime-local" name="startDate" value="<fmt:formatDate value='${course.startDate}' pattern='yyyy-MM-dd\'T\'HH:mm' />" required>
            </div>
            <div>
                <label>End Date</label><br>
                <input type="datetime-local" name="endDate" value="<fmt:formatDate value='${course.endDate}' pattern='yyyy-MM-dd\'T\'HH:mm' />" required>
            </div>
        </div>
        <div style="margin-top:1rem;">
            <button type="submit" class="btn-primary">Save Course Changes</button>
        </div>
    </form>

    <h2>Add Section</h2>
    <form action="/instructor/course/${course.id}/section/add" method="post" style="background:#f7f7f7; padding:1rem; border-radius:12px; margin-bottom:2rem; max-width:700px;">
        <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:1rem;">
            <div>
                <label>Section Name</label><br>
                <input type="text" name="sectionName" placeholder="Section A">
            </div>
            <div>
                <label>Days</label><br>
                <input type="text" name="days" value="Mon/Wed" required>
            </div>
            <div>
                <label>Start Time</label><br>
                <input type="time" name="startTime" required>
            </div>
            <div>
                <label>End Time</label><br>
                <input type="time" name="endTime" required>
            </div>
            <div>
                <label>Location</label><br>
                <input type="text" name="location" placeholder="Room 101">
            </div>
            <div>
                <label>Seat Capacity</label><br>
                <input type="number" name="maxSeats" min="1" value="${course.maxSeats}">
            </div>
        </div>
        <div style="margin-top:1rem;">
            <button type="submit" class="btn-primary">Add Section</button>
        </div>
    </form>

    <h2>Course Sections</h2>
    <c:choose>
        <c:when test="${empty sections}">
            <p>No sections exist yet. Add one above.</p>
        </c:when>
        <c:otherwise>
            <form action="/instructor/course/${course.id}/edit" method="get" style="margin-bottom:1.5rem; max-width:500px;">
                <label for="sectionId"><strong>Select section roster</strong></label>
                <div style="display:flex; gap:0.75rem; align-items:center; margin-top:0.5rem;">
                    <select id="sectionId" name="sectionId" style="flex:1; padding:0.5rem;">
                        <c:forEach var="section" items="${sections}">
                            <option value="${section.id}" ${selectedSectionId == section.id ? 'selected' : ''}>
                                ${section.sectionName} (${section.days} ${section.startTime}-${section.endTime})
                            </option>
                        </c:forEach>
                    </select>
                    <button type="submit" class="btn-secondary">View roster</button>
                </div>
            </form>

            <c:if test="${not empty selectedSection}">
                <div style="border:1px solid #dcdcdc; border-radius:12px; padding:1rem; margin-bottom:1.5rem; background:#fff; max-width:900px;">
                    <div style="display:flex; justify-content:space-between; align-items:center; gap:1rem; flex-wrap:wrap;">
                        <div>
                            <h3>${selectedSection.sectionName}</h3>
                            <p>${selectedSection.days} • ${selectedSection.startTime} - ${selectedSection.endTime}</p>
                            <p>Location: ${selectedSection.location}</p>
                            <p>${selectedSection.numEnrolled} / ${selectedSection.maxSeats} enrolled</p>
                        </div>
                        <form action="/instructor/course/${course.id}/section/${selectedSection.id}/delete" method="post" onsubmit="return confirm('Remove this section?');">
                            <button type="submit" class="btn-danger">Remove Section</button>
                        </form>
                    </div>

                    <form action="/instructor/course/${course.id}/section/${selectedSection.id}/update" method="post" style="margin-top:1rem; background:#fafafa; padding:1rem; border-radius:10px;">
                        <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap:1rem;">
                            <div>
                                <label>Section Name</label><br>
                                <input type="text" name="sectionName" value="${selectedSection.sectionName}" required>
                            </div>
                            <div>
                                <label>Days</label><br>
                                <input type="text" name="days" value="${selectedSection.days}" required>
                            </div>
                            <div>
                                <label>Start Time</label><br>
                                <input type="time" name="startTime" value="${selectedSection.startTime}" required>
                            </div>
                            <div>
                                <label>End Time</label><br>
                                <input type="time" name="endTime" value="${selectedSection.endTime}" required>
                            </div>
                            <div>
                                <label>Location</label><br>
                                <input type="text" name="location" value="${selectedSection.location}">
                            </div>
                            <div>
                                <label>Seat Capacity</label><br>
                                <input type="number" name="maxSeats" min="1" value="${selectedSection.maxSeats}">
                            </div>
                        </div>
                        <div style="margin-top:0.75rem;">
                            <button type="submit" class="btn-secondary">Save Section</button>
                        </div>
                    </form>

                    <div style="margin-top:1rem;">
                        <h4>Roster</h4>
                        <c:choose>
                            <c:when test="${empty selectedSection.registrations}">
                                <p>No students enrolled in this section yet.</p>
                            </c:when>
                            <c:otherwise>
                                <table style="width:100%; border-collapse:collapse;">
                                    <thead>
                                        <tr>
                                            <th style="text-align:left; padding:0.5rem;">Student</th>
                                            <th style="text-align:left; padding:0.5rem;">Current Grade</th>
                                            <th style="text-align:left; padding:0.5rem;">Update Grade</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="registration" items="${selectedSection.registrations}">
                                            <c:if test="${registration.status == 'ACTIVE'}">
                                                <tr>
                                                    <td style="padding:0.5rem; border-top:1px solid #eee;">
                                                        ${registration.student.firstName} ${registration.student.lastName}
                                                    </td>
                                                    <td style="padding:0.5rem; border-top:1px solid #eee;">
                                                        ${empty registration.grade ? 'No grade yet' : registration.grade}
                                                    </td>
                                                    <td style="padding:0.5rem; border-top:1px solid #eee;">
                                                        <form action="/instructor/course/${course.id}/section/${selectedSection.id}/grade" method="post" style="display:flex; gap:0.5rem; align-items:center;">
                                                            <input type="hidden" name="registrationId" value="${registration.registrationId}">
                                                            <input type="text" name="grade" value="${registration.grade}" placeholder="Enter grade">
                                                            <button type="submit" class="btn-secondary">Save</button>
                                                        </form>
                                                    </td>
                                                </tr>
                                            </c:if>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
