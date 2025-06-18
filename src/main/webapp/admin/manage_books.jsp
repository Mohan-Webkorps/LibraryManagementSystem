<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.Book" %>
<%@ page import="com.library.model.User" %>
<%
    // Check if user is logged in and is admin
    User user = (User) session.getAttribute("user");
    if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    // Get books list from request
    List<Book> books = (List<Book>) request.getAttribute("books");
    if (books == null) {
        response.sendRedirect(request.getContextPath() + "/admin/BookManagementServlet?action=list");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Books - Library Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        /* Container and Layout */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        /* Header Styles */
        header {
            background-color: #2c3e50;
            color: white;
            padding: 1rem;
            margin-bottom: 2rem;
        }
        
        header h1 {
            margin: 0;
            font-size: 24px;
        }
        
        header p {
            margin: 5px 0 0;
            font-size: 14px;
            opacity: 0.8;
        }

        /* Dashboard Layout */
        .dashboard {
            display: flex;
            gap: 20px;
            min-height: calc(100vh - 100px);
        }

        /* Sidebar Navigation */
        .sidebar {
            width: 250px;
            background: #2c3e50;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            height: fit-content;
            color: white;
        }

        .sidebar h3 {
            margin-top: 0;
            color: white;
            font-size: 18px;
            padding-bottom: 10px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .sidebar ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .sidebar ul li {
            margin-bottom: 10px;
        }

        .sidebar ul li a {
            display: block;
            padding: 10px;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .sidebar ul li a:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }

        .sidebar ul li a.active {
            background-color: #2196F3;
            color: white;
        }

        /* Main Content Area */
        .main-content {
            flex: 1;
            background: #f9f9f9;
            padding: 20px;
            border-radius: 8px;
        }

        /* Existing styles */
        .book-form {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        .book-form h3 {
            margin-top: 0;
            color: #333;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #666;
        }
        .form-group input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .book-list {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .book-list table {
            width: 100%;
            border-collapse: collapse;
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
        .action-buttons {
            display: flex;
            gap: 10px;
        }
        .btn-edit, .btn-delete, .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s;
        }
        .btn-edit {
            background: #4CAF50;
            color: white;
        }
        .btn-delete {
            background: #f44336;
            color: white;
        }
        .btn {
            background: #2196F3;
            color: white;
        }
        .btn:hover, .btn-edit:hover, .btn-delete:hover {
            opacity: 0.9;
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
        /* Modal styles */
        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.4);
        }

        .modal-content {
            background-color: #fefefe;
            margin: 15% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 80%;
            max-width: 500px;
            border-radius: 8px;
        }

        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }

        .close:hover,
        .close:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }

        .btn-issue {
            background: #2196F3;
            color: white;
            padding: 5px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
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
                    <li><a href="${pageContext.request.contextPath}/admin/manage_books.jsp" class="active">Manage Books</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/ViewBooksServlet">View All Books</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/BookIssueHistoryServlet">View Issuances</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp">Logout</a></li>
                </ul>
            </div>

            <div class="main-content">
                <h2>Manage Books</h2>
                
                <%-- Display success message --%>
                <% if (session.getAttribute("success") != null) { %>
                    <div class="alert alert-success">
                        <%= session.getAttribute("success") %>
                        <% session.removeAttribute("success"); %>
                    </div>
                <% } %>
                
                <%-- Display error message --%>
                <% if (session.getAttribute("error") != null) { %>
                    <div class="alert alert-danger">
                        <%= session.getAttribute("error") %>
                        <% session.removeAttribute("error"); %>
                    </div>
                <% } %>

                <!-- Add Book Form -->
                <div class="book-form">
                    <h3>Add New Book</h3>
                    <form action="${pageContext.request.contextPath}/admin/BookManagementServlet" method="post">
                        <input type="hidden" name="action" value="add">
                        <div class="form-group">
                            <label for="bookName">Book Name</label>
                            <input type="text" id="bookName" name="bookName" required>
                        </div>
                        <div class="form-group">
                            <label for="author">Author</label>
                            <input type="text" id="author" name="author" required>
                        </div>
                        <div class="form-group">
                            <label for="edition">Edition</label>
                            <input type="text" id="edition" name="edition" required>
                        </div>
                        <div class="form-group">
                            <label for="quantity">Quantity</label>
                            <input type="number" id="quantity" name="quantity" min="1" required>
                        </div>
                        <div class="form-group">
                            <label for="parkingSlot">Parking Slot</label>
                            <input type="text" id="parkingSlot" name="parkingSlot" required>
                        </div>
                        <button type="submit" class="btn">Add Book</button>
                    </form>
                </div>

                <!-- Book List -->
                <div class="book-list">
                    <h3>Book List</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Book Name</th>
                                <th>Author</th>
                                <th>Edition</th>
                                <th>Total</th>
                                <th>Parking Slot</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Book book : books) { %>
                                <tr>
                                    <td><%= book.getBookName() %></td>
                                    <td><%= book.getAuthor() %></td>
                                    <td><%= book.getEdition() %></td>
                                    <td><%= book.getQuantity() %></td>
                                    <td><%= book.getParkingSlot() %></td>
                                    <td class="action-buttons">
                                        <form action="${pageContext.request.contextPath}/admin/BookManagementServlet" method="get" style="display: inline;">
                                            <input type="hidden" name="action" value="edit">
                                            <input type="hidden" name="id" value="<%= book.getId() %>">
                                            <button type="submit" class="btn-edit">Edit</button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/admin/BookManagementServlet" method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="<%= book.getId() %>">
                                            <button type="submit" class="btn-delete" onclick="return confirm('Are you sure you want to delete this book?')">Delete</button>
                                        </form>
                                    </td>
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