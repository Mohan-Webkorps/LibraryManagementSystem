<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.Book" %>
<%@ page import="com.library.model.User" %>
<%
    // Redirect to DashboardServlet if accessed directly
    if (request.getAttribute("totalUniqueBooks") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/DashboardServlet");
        return;
    }
    
    // Get the user from session
    User user = (User) session.getAttribute("user");
    if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Library Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
            transition: transform 0.3s ease;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
        .stat-card h3 {
            color: #666;
            margin-bottom: 10px;
            font-size: 1.1em;
        }
        .stat-card p {
            font-size: 2em;
            font-weight: bold;
            color: #333;
            margin: 0;
        }
        .quick-actions {
            margin-bottom: 30px;
        }
        .action-buttons {
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
        }
        .action-buttons .btn {
            flex: 1;
            min-width: 150px;
            text-align: center;
        }
        .recent-activity {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .recent-activity table {
            width: 100%;
            border-collapse: collapse;
        }
        .recent-activity th,
        .recent-activity td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        .recent-activity th {
            background: #f5f5f5;
            font-weight: 600;
        }
        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .recent-students {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .recent-students table {
            width: 100%;
            border-collapse: collapse;
        }
        .recent-students th,
        .recent-students td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        .recent-students th {
            background: #f5f5f5;
            font-weight: 600;
        }
        .book-management {
            margin-top: 30px;
        }
        .book-list table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background: white;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .book-list th, .book-list td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        .book-list th {
            background: #f5f5f5;
            font-weight: 600;
        }
        .btn-danger {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid transparent;
            border-radius: 4px;
        }
        .alert-success {
            color: #155724;
            background-color: #d4edda;
            border-color: #c3e6cb;
        }
        .alert-danger {
            color: #721c24;
            background-color: #f8d7da;
            border-color: #f5c6cb;
        }
    </style>
</head>
<body>
    <header>
        <h1>Library Management System</h1>
        <p>Welcome, <%= user != null ? user.getName() : "Guest" %></p>
    </header>

    <div class="container">
        <div class="dashboard">
            <div class="sidebar">
                <h3>Navigation</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/DashboardServlet">Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/manage_books.jsp">Manage Books</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/ViewBooksServlet">View All Books</a></li>                    
                    <li><a href="${pageContext.request.contextPath}/admin/BookIssueHistoryServlet">View Issuances</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp">Logout</a></li>
                </ul>
            </div>

            <div class="main-content">
                <h2>Admin Dashboard</h2>

                <div class="stats">
                    <div class="stat-card">
                        <h3>Total Book Titles</h3>
                        <p><%= request.getAttribute("totalUniqueBooks") != null ? request.getAttribute("totalUniqueBooks") : "0" %></p>
                    </div>
                    <div class="stat-card">
                        <h3>Total Book Copies</h3>
                        <p><%= request.getAttribute("totalBooks") != null ? request.getAttribute("totalBooks") : "0" %></p>
                    </div>
                    <div class="stat-card">
                        <h3>Available Books</h3>
                        <p><%= request.getAttribute("availableBooks") != null ? request.getAttribute("availableBooks") : "0" %></p>
                    </div>
                    <div class="stat-card">
                        <h3>Issued Books</h3>
                        <p><%= request.getAttribute("issuedBooks") != null ? request.getAttribute("issuedBooks") : "0" %></p>
                    </div>
                    <div class="stat-card">
                        <h3>Total Students</h3>
                        <p><%= request.getAttribute("totalStudents") != null ? request.getAttribute("totalStudents") : "0" %></p>
                    </div>
                </div>                
                </div>
            </div>
        </div>
</body>
</html> 