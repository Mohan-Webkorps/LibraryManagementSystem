<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.BookIssue" %>
<%@ page import="com.library.model.User" %>
<%@ page import="com.library.model.Book" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Date" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student Dashboard - Library Management System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }

        .container {
            width: 90%;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        header {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        header h1 {
            margin: 0;
            color: #333;
            font-size: 1.8em;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .user-info span {
            font-size: 1.1em;
            color: #333;
            font-weight: 500;
        }

        .action-buttons {
            display: flex;
            gap: 15px;
            margin-bottom: 30px;
            flex-wrap: wrap;
        }

        .action-buttons a {
            padding: 12px 24px;
            background: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
            font-weight: 500;
        }

        .action-buttons a:hover {
            background: #0056b3;
        }

        .action-buttons .logout-btn {
            background: #dc3545;
        }

        .action-buttons .logout-btn:hover {
            background: #c82333;
        }

        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: #fff;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
            transition: transform 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }

        .stat-card h3 {
            color: #666;
            margin-bottom: 15px;
            font-size: 1.1em;
            font-weight: 500;
        }

        .stat-card p {
            font-size: 2.5em;
            font-weight: bold;
            color: #333;
            margin: 0;
        }

        .recent-activity {
            background: #fff;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .recent-activity h2 {
            color: #333;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
            font-size: 1.5em;
            font-weight: 600;
        }

        .book-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .book-table th, .book-table td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        .book-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #333;
            font-size: 1.1em;
        }

        .book-table tr:hover {
            background: #f8f9fa;
        }

        .book-table td {
            color: #555;
        }

        .book-table a {
            color: #007bff;
            text-decoration: none;
            margin-right: 15px;
            font-weight: 500;
            padding: 5px 10px;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .book-table a:hover {
            background: #f0f7ff;
        }

        .error-message, .success-message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
            font-weight: 500;
        }

        .error-message {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .success-message {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        @media (max-width: 768px) {
            .container {
                width: 95%;
                padding: 15px;
            }

            header {
                flex-direction: column;
                gap: 15px;
                text-align: center;
            }

            .stats {
                grid-template-columns: 1fr;
            }

            .action-buttons {
                justify-content: center;
            }

            .book-table {
                display: block;
                overflow-x: auto;
            }
        }

        .action-link {
            background: none;
            border: none;
            color: #007bff;
            text-decoration: none;
            cursor: pointer;
            padding: 5px 10px;
            margin-right: 10px;
            font-weight: 500;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .action-link:hover {
            background: #f0f7ff;
        }

        form {
            display: inline-block;
            margin: 0;
            padding: 0;
        }

        .action-btn {
            padding: 8px 16px;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s;
            margin: 0 4px;
        }

        .return-btn {
            background: #dc3545;
        }

        .return-btn:hover {
            background: #c82333;
        }

        .renew-btn {
            background: #28a745;
        }

        .renew-btn:hover {
            background: #218838;
        }

        .action-column {
            width: 200px;
            text-align: center;
        }

        .action-form {
            display: inline-block;
            margin: 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Library Management System</h1>
            <div class="user-info">
                <%
                    User user = (User) session.getAttribute("user");
                    String userName = (user != null) ? user.getName() : "";
                    String errorMsg = (String) request.getAttribute("error");
                    String successMsg = (String) request.getAttribute("success");
                %>
                <span>Welcome, <%= userName %></span>
            </div>
        </header>

        <% if (errorMsg != null) { %>
            <div class="error-message"><%= errorMsg %></div>
        <% } %>
        <% if (successMsg != null) { %>
            <div class="success-message"><%= successMsg %></div>
        <% } %>

        <div class="action-buttons">
            <a href="<%= request.getContextPath() %>/student/dashboard">Dashboard</a>
            <a href="<%= request.getContextPath() %>/student/search">Search Books</a>
            <a href="<%= request.getContextPath() %>/index.jsp" class="logout-btn">Logout</a>
        </div>

        <div class="stats">
            <div class="stat-card">
                <h3>Books Issued</h3>
                <p>
                    <%
                        Integer issuedCount = (Integer) request.getAttribute("issuedBooksCount");
                        out.print(issuedCount != null ? issuedCount : 0);
                    %>
                </p>
            </div>
            <div class="stat-card">
                <h3>Due Books</h3>
                <p>
                    <%
                        Integer dueCount = (Integer) request.getAttribute("dueBooksCount");
                        out.print(dueCount != null ? dueCount : 0);
                    %>
                </p>
            </div>
        </div>

        <div class="recent-activity">
            <h2>Currently Issued Books</h2>
            <table class="book-table">
                <thead>
                    <tr>
                        <th>Book Name</th>
                        <th>Author</th>
                        <th>Issue Date</th>
                        <th>Return Date</th>
                        <th>Status</th>
                        <th class="action-column">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<BookIssue> recentBooks = (List<BookIssue>) request.getAttribute("recentBooks");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        
                        if (recentBooks != null && !recentBooks.isEmpty()) {
                            for (BookIssue bookIssue : recentBooks) {
                                Book book = bookIssue.getBook();
                                if (bookIssue != null && book != null) {
                                    String status = bookIssue.getStatus();
                                    boolean canReturn = "issued".equals(status) || "renewed".equals(status);
                                    boolean canRenew = "issued".equals(status) || "renewed".equals(status);
                    %>
                        <tr>
                            <td><%= book.getBookName() %></td>
                            <td><%= book.getAuthor() %></td>
                            <td><%= bookIssue.getIssueDate() != null ? dateFormat.format(bookIssue.getIssueDate()) : "" %></td>
                            <td><%= bookIssue.getReturnDate() != null ? dateFormat.format(bookIssue.getReturnDate()) : "" %></td>
                            <td><%= status %></td>
                            <td>
                                <div class="action-buttons">
                                    <% if (canReturn) { %>
                                        <form action="<%= request.getContextPath() %>/student/return" method="post" class="action-form">
                                            <input type="hidden" name="issueId" value="<%= bookIssue.getId() %>">
                                            <button type="submit" class="action-btn return-btn">Return</button>
                                        </form>
                                    <% } %>
                                    <% if (canRenew) { %>
                                        <form action="<%= request.getContextPath() %>/student/renew" method="post" class="action-form">
                                            <input type="hidden" name="issueId" value="<%= bookIssue.getId() %>">
                                            <button type="submit" class="action-btn renew-btn">Renew</button>
                                        </form>
                                    <% } %>
                                    <% if (!canReturn && !canRenew) { %>
                                        <span>Returned</span>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                    <%
                                }
                            }
                        } else {
                    %>
                        <tr>
                            <td colspan="6" style="text-align: center;">No books issued yet</td>
                        </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html> 