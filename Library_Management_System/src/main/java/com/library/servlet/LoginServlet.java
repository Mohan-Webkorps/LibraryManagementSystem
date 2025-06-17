package com.library.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.library.model.User;
import com.library.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection conn;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String user_Id = request.getParameter("userId");
        String password = request.getParameter("password");
        
        if (user_Id == null || user_Id.trim().isEmpty() 
            || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter both user ID and password");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        try {
        	conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM users WHERE user_Id = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql); 
                stmt.setInt(1, Integer.parseInt(user_Id));
                stmt.setString(2, password);
                
                ResultSet rs = stmt.executeQuery(); {
                    if (rs.next()) {
                        User user = new User();
                        user.setUser_Id(rs.getInt("user_Id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setRole(rs.getString("role"));
                        user.setLibraryName(rs.getString("library_name"));
                        user.setAddress(rs.getString("address"));
                        
                        HttpSession session = request.getSession();
                        session.setAttribute("user", user);
                        
                        if ("admin".equalsIgnoreCase(user.getRole())) {
                            response.sendRedirect(request.getContextPath() + "/admin/DashboardServlet");
                        } else if ("student".equalsIgnoreCase(user.getRole())) {
                            response.sendRedirect(request.getContextPath() + "/student/dashboard");
                        } else {
                            request.setAttribute("error", "Invalid user role");
                            request.getRequestDispatcher("/index.jsp").forward(request, response);
                        }
                    } else {
                        request.setAttribute("error", "Invalid ID or password");
                        request.getRequestDispatcher("/index.jsp").forward(request, response);
                    }
                }
         } catch (Exception e) {
            System.out.println("Error in LoginServlet: " + e.getMessage());
            request.setAttribute("error", "An error occurred during login");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

} 