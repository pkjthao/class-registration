<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Group</title>
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>

<div class="association-header">Small Groups</div>

<div class="association-page">
    <div class="form-container association-form-panel">
        <h1>Edit Group</h1>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="/associations/${association.id}/update" method="post">
            <div class="form-group">
                <label for="groupName">Group Name</label>
                <input type="text" id="groupName" name="groupName" value="${association.groupName}" required>
            </div>
            <div class="form-group">
                <label for="leaderName">Servant Name</label>
                <input type="text" id="leaderName" name="leaderName" value="${association.leaderName}" required>
            </div>
            <div class="form-group">
                <label for="groupVision">Group Vision</label>
                <textarea id="groupVision" name="groupVision" rows="3" required>${association.groupVision}</textarea>
            </div>
            <div class="form-group">
                <label for="groupMission">Group Mission</label>
                <textarea id="groupMission" name="groupMission" rows="3" required>${association.groupMission}</textarea>
            </div>
            <div class="form-group">
                <label for="location">Location</label>
                <input type="text" id="location" name="location" value="${association.location}" required>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="${association.email}" required>
            </div>
            <div class="form-group">
                <label for="phoneNumber">Phone Number</label>
                <input type="tel" id="phoneNumber" name="phoneNumber" value="${association.phoneNumber}" required>
            </div>
            <button type="submit" class="btn-primary">Update Group</button>
        </form>

        <form action="/associations/${association.id}/delete" method="post" onsubmit="return confirm('Delete this group?');" style="margin-top: 1rem;">
            <button type="submit" class="delete-btn full-width">Delete Group</button>
        </form>

        <p class="redirect-link"><a href="/associations/add">Back to Add Group</a></p>
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
                    <c:forEach var="item" items="${associations}">
                        <li class="association-item">
                            <a href="/associations/${item.id}/edit" class="association-link">
                                <div class="association-name">${item.groupName}</div>
                                <div class="association-meta">${item.location}</div>
                                <div class="association-meta">${item.email}</div>
                            </a>
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
