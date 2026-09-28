<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Group</title>
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>

<div class="association-header">Small Groups</div>

<div class="association-page">
    <div class="form-container association-form-panel">
        <h1>Add Group</h1>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
            <div class="success-message"><%= request.getAttribute("success") %></div>
        <% } %>

        <form action="/associations/create" method="post">
            <div class="form-group">
                <label for="groupName">Group Name</label>
                <input type="text" id="groupName" name="groupName" required>
            </div>
            <div class="form-group">
                <label for="leaderName">Servant Name</label>
                <input type="text" id="leaderName" name="leaderName" required>
            </div>
            <div class="form-group">
                <label for="groupVision">Group Vision</label>
                <textarea id="groupVision" name="groupVision" rows="3" required></textarea>
            </div>
            <div class="form-group">
                <label for="groupMission">Group Mission</label>
                <textarea id="groupMission" name="groupMission" rows="3" required></textarea>
            </div>
            <div class="form-group">
                <label for="location">Location</label>
                <input type="text" id="location" name="location" required>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="phoneNumber">Phone Number</label>
                <input type="tel" id="phoneNumber" name="phoneNumber" required>
            </div>
            <button type="submit" class="btn-primary">Save Group</button>
        </form>

        <p class="redirect-link"><a href="/instructor/home">Back to Home</a></p>
    </div>

    <div class="association-list-panel">
        <button class="association-toggle" type="button" aria-expanded="true" aria-controls="associationListContent">
            <span>Existing Groups</span>
            <span class="toggle-indicator">−</span>
        </button>

        <div id="associationListContent" class="association-list-content">
            <c:if test="${empty associations}">
                <p class="empty-state">No groups have been created yet.</p>
            </c:if>

            <c:if test="${not empty associations}">
                <ul class="association-list">
                    <c:forEach var="association" items="${associations}">
                        <li class="association-item">
                            <div class="association-row">
                                <a href="/associations/${association.id}/edit" class="association-link">
                                    <div class="association-name">${association.groupName}</div>
                                    <div class="association-meta">${association.leaderName}</div>
                                    <div class="association-meta">${association.email}</div>
                                </a>
                                <form action="/associations/${association.id}/delete" method="post" onsubmit="return confirm('Delete this association?');">
                                    <button type="submit" class="delete-btn">Delete</button>
                                </form>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </div>
    </div>
</div>

<script>
    const toggleButton = document.querySelector('.association-toggle');
    const listContent = document.getElementById('associationListContent');

    if (toggleButton && listContent) {
        toggleButton.addEventListener('click', () => {
            const isExpanded = toggleButton.getAttribute('aria-expanded') === 'true';
            toggleButton.setAttribute('aria-expanded', String(!isExpanded));
            listContent.classList.toggle('collapsed', isExpanded);
            toggleButton.querySelector('.toggle-indicator').textContent = isExpanded ? '+' : '−';
        });
    }
</script>

</body>
</html>
