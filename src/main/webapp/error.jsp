<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error - Library Management System</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <div class="container">
        <div class="error-container">
            <h2>Oops! Something went wrong</h2>
            <p>${requestScope.error}</p>
            <a href="index.jsp" class="btn">Return to Home</a>
        </div>
    </div>
</body>
</html> 