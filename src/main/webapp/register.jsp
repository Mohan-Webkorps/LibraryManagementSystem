<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - Library Management System</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .admin-fields {
            display: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h2>Register</h2>
            <%-- Display error message if any --%>
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            <form action="RegisterServlet" method="post">
                <div class="form-group">
                    <label for="name">Full Name</label>
                    <input type="text" id="name" name="name" required placeholder="Enter your full name">
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required placeholder="Enter your email">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required placeholder="Enter your password">
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required placeholder="Confirm your password">
                </div>
                <div class="form-group">
                    <label for="role">Role</label>
                    <select id="role" name="role" required onchange="toggleAdminFields()">
                        <option value="">Select your role</option>
                        <option value="student">Student</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>
                <div class="form-group admin-fields">
                    <label for="address">Address</label>
                    <textarea id="address" name="address" placeholder="Enter your address"></textarea>
                </div>
                <div class="form-group admin-fields">
                    <label for="libraryName">Library Name</label>
                    <input type="text" id="libraryName" name="libraryName" placeholder="Enter library name">
                </div>
                <button type="submit" class="btn">Register</button>
            </form>
            <p>Already have an account? <a href="index.jsp">Login here</a></p>
        </div>
    </div>

    <script>
        function toggleAdminFields() {
            const roleSelect = document.getElementById('role');
            const adminFields = document.querySelectorAll('.admin-fields');
            const isAdmin = roleSelect.value === 'admin';
            
            adminFields.forEach(field => {
                field.style.display = isAdmin ? 'block' : 'none';
                // Make fields required only for admin
                const inputs = field.querySelectorAll('input, textarea');
                inputs.forEach(input => {
                    input.required = isAdmin;
                });
            });
        }

        // Call the function on page load to set initial state
        document.addEventListener('DOMContentLoaded', toggleAdminFields);
    </script>
</body>
</html> 