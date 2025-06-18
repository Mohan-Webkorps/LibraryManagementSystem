<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.library.model.Book" %>
<%@ page import="com.library.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    Book book = (Book) request.getAttribute("book");
    if (book == null) {
        response.sendRedirect(request.getContextPath() + "/admin/manage_books.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Book - Library Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .book-form {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            max-width: 600px;
            margin: 30px auto;
        }
        .book-form h3 {
            margin-top: 0;
            color: #333;
            text-align: center;
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
        .button-group {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin-top: 20px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-primary {
            background: #4CAF50;
            color: white;
        }
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="book-form">
            <h3>Edit Book</h3>
            
            <form action="${pageContext.request.contextPath}/admin/BookManagementServlet" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="<%= book.getId() %>">
                
                <div class="form-group">
                    <label for="bookName">Book Name</label>
                    <input type="text" id="bookName" name="bookName" value="<%= book.getBookName() %>" required>
                </div>
                
                <div class="form-group">
                    <label for="author">Author</label>
                    <input type="text" id="author" name="author" value="<%= book.getAuthor() %>" required>
                </div>
                
                <div class="form-group">
                    <label for="edition">Edition</label>
                    <input type="text" id="edition" name="edition" value="<%= book.getEdition() %>" required>
                </div>
                
                <div class="form-group">
                    <label for="quantity">Quantity</label>
                    <input type="number" id="quantity" name="quantity" value="<%= book.getQuantity() %>" min="1" required>
                </div>
                
                <div class="form-group">
                    <label for="parkingSlot">Parking Slot</label>
                    <input type="text" id="parkingSlot" name="parkingSlot" value="<%= book.getParkingSlot() %>" required>
                </div>
                
                <div class="button-group">
                    <button type="submit" class="btn btn-primary">Update Book</button>
                    <a href="${pageContext.request.contextPath}/admin/manage_books.jsp" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html> 