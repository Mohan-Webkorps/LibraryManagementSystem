<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.Book" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Books - Library Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
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

        .search-container {
            margin: 20px 0;
            padding: 20px;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .search-form {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }

        .search-form input[type="text"] {
            flex: 1;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
        }

        .search-form button {
            padding: 12px 24px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: 500;
        }

        .search-form button:hover {
            background: #0056b3;
        }

        .book-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background: #fff;
            border-radius: 8px;
            overflow: hidden;
        }

        .book-table th, .book-table td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        .book-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #333;
        }

        .book-table tr:hover {
            background-color: #f5f5f5;
        }

        .no-results {
            text-align: center;
            padding: 20px;
            color: #666;
            font-style: italic;
        }

        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 12px;
            margin: 10px 0;
            border-radius: 4px;
            border: 1px solid #f5c6cb;
        }

        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 12px;
            margin: 10px 0;
            border-radius: 4px;
            border: 1px solid #c3e6cb;
        }

        .issue-btn {
            padding: 8px 16px;
            background: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s;
        }

        .issue-btn:hover {
            background: #218838;
        }

        .issue-btn:disabled {
            background: #6c757d;
            cursor: not-allowed;
        }

        .book-table td:last-child {
            text-align: center;
        }

        .action-column {
            width: 120px;
        }

        @media (max-width: 768px) {
            .container {
                width: 95%;
                padding: 10px;
            }

            header {
                flex-direction: column;
                text-align: center;
                gap: 15px;
            }

            .action-buttons {
                justify-content: center;
            }

            .search-form {
                flex-direction: column;
            }

            .search-form button {
                width: 100%;
            }

            .book-table {
                display: block;
                overflow-x: auto;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Library Management System</h1>
            <div class="user-info">
                <span>Welcome, ${sessionScope.user.name}</span>
            </div>
        </header>

        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/search">Search Books</a>
            <a href="${pageContext.request.contextPath}/index.jsp" class="logout-btn">Logout</a>
        </div>

        <div class="search-container">
            <h2>Search Books</h2>
            <form action="${pageContext.request.contextPath}/student/search" method="get" class="search-form">
                <input type="text" name="searchTerm" placeholder="Search by book name or author..." 
                       value="${param.searchTerm}" required>
                <button type="submit">Search</button>
            </form>

            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="success-message">
                    <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <table class="book-table">
                <thead>
                    <tr>
                        <th>Book Name</th>
                        <th>Author</th>
                        <th>Edition</th>
                        <th>Available Copies</th>
                        <th>Location</th>
                        <th class="action-column">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    List<Book> books = (List<Book>)request.getAttribute("books");
                    if (books != null && !books.isEmpty()) {
                        for (Book book : books) {
                            boolean isAvailable = book.getQuantity() > 0;
                    %>
                        <tr>
                            <td><%= book.getBookName() %></td>
                            <td><%= book.getAuthor() %></td>
                            <td><%= book.getEdition() %></td>
                            <td><%= book.getQuantity() %></td>
                            <td><%= book.getParkingSlot() != null ? book.getParkingSlot() : "Not specified" %></td>
                            <td>
                                <form action="${pageContext.request.contextPath}/student/issue" method="post" style="display: inline;">
                                    <input type="hidden" name="bookId" value="<%= book.getId() %>">
                                    <button type="submit" class="issue-btn" <%= !isAvailable ? "disabled" : "" %>>
                                        <%= isAvailable ? "Issue Book" : "Not Available" %>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    <% 
                        }
                    } else if (request.getParameter("searchTerm") != null) {
                    %>
                        <tr>
                            <td colspan="6" class="no-results">No books found matching your search criteria</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html> 