<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Login</title>
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>

<div class="form-container">
    <h1>Student Login</h1>

    <% if ("true".equals(request.getParameter("registered"))) { %>
        <div class="success-message">Account created! Please sign in.</div>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message"><%= request.getAttribute("error") %></div>
    <% } %>

    <form id="studentLoginForm" action="/student/login" method="post">
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit" class="btn-primary">Sign In</button>
    </form>

    <p class="redirect-link">Don't have an account? <a href="/student/register">Register</a></p>
    <p class="redirect-link">Are you an instructor? <a href="/instructor/login">Instructor login</a></p>
</div>

<script src="/js/student-login.js"></script>
</body>
</html>