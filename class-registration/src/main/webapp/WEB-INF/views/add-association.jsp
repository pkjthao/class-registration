<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Association</title>
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>

<div class="form-container">
    <h1>Add Association</h1>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message"><%= request.getAttribute("error") %></div>
    <% } %>

    <form action="/associations/create" method="post">
        <div class="form-group">
            <label for="groupName">Group Name</label>
            <input type="text" id="groupName" name="groupName" required>
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
        <button type="submit" class="btn-primary">Save Association</button>
    </form>

    <p class="redirect-link"><a href="/instructor/home">Back to Home</a></p>
</div>

</body>
</html>
