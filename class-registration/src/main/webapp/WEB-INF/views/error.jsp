<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error ${errorCode}</title>
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>
<div class="form-container" style="text-align:center">
    <h1 style="font-size:3rem; color:#c62828;">${errorCode}</h1>
    <h2 style="color:#333; margin-bottom:1rem;">${errorTitle}</h2>
    <p style="color:#666; margin-bottom:1.5rem;">${errorMessage}</p>
    <a href="javascript:history.back()" class="btn-primary"
       style="display:inline-block; width:auto; padding: 0.6rem 1.5rem">
       Go Back
    </a>
</div>
</body>
</html>