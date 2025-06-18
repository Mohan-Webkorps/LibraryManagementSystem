<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.Book" %>
<%@ page import="com.library.model.User" %>
<%@ page import="com.library.model.BookIssue" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Current Book Issues - Library Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .book-history {
            margin: 20px;
            padding: 20px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .book-history table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .book-history th, .book-history td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .book-history th {
            background-color: #f5f5f5;
            font-weight: bold;
        }
        .book-history tr:hover {
            background-color: #f9f9f9;
        }
        .status-issued {
            color: #e67e22;
            font-weight: bold;
        }
        .status-renewed {
            color: #3498db;
            font-weight: bold;
        }
        .search-section {
            margin-bottom: 20px;
        }
        .search-section input[type="text"] {
            padding: 8px;
            width: 300px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .btn {
            background-color: #4CAF50;
            color: white;
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #45a049;
        }
        .return-date {
            color: #e74c3c;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <header>
        <h1>Library Management System</h1>
        <p>Welcome, <%= user.getName() %></p>
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
                <h2>Currently Issued Books</h2>
                
                <div class="search-section">
                    <form action="${pageContext.request.contextPath}/admin/BookIssueHistoryServlet" method="get">
                        <input type="text" name="searchTerm" placeholder="Search by book name or student name..." 
                               value="<%= request.getParameter("searchTerm") != null ? request.getParameter("searchTerm") : "" %>">
                        <button type="submit" class="btn">Search</button>
                    </form>
                </div>

                <div class="book-history">
                    <table>
                        <thead>
                            <tr>
                                <th>Book Name</th>
                                <th>Current Holder</th>
                                <th>Contact Email</th>
                                <th>Due Date</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            List<BookIssue> issueHistory = (List<BookIssue>)request.getAttribute("issueHistory");
                            if (issueHistory != null && !issueHistory.isEmpty()) {
                                for (BookIssue issue : issueHistory) {
                            %>
                                <tr>
                                    <td><%= issue.getBook().getBookName() %></td>
                                    <td><%= issue.getUser().getName() %></td>
                                    <td><%= issue.getUser().getEmail() %></td>
                                    <td class="return-date"><%= dateFormat.format(issue.getReturnDate()) %></td>
                                    <td class="status-<%= issue.getStatus().toLowerCase() %>"><%= issue.getStatus() %></td>
                                </tr>
                            <% 
                                }
                            } else {
                            %>
                                <tr>
                                    <td colspan="5" style="text-align: center;">No books are currently issued</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 