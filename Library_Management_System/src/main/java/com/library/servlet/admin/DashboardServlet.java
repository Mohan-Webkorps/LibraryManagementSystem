package com.library.servlet.admin;

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

@WebServlet("/admin/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute("user");
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            String studentsSql = "SELECT COUNT(*) as total FROM users WHERE role = 'student'";
            try (PreparedStatement stmt = conn.prepareStatement(studentsSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("totalStudents", rs.getInt("total"));
                    }
                }
            }            

            String booksSql = "SELECT SUM(quantity) AS total FROM books;";
            try (PreparedStatement stmt = conn.prepareStatement(booksSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("totalBooks", rs.getInt("total"));
                    }
                }
            }
            
            String issuedSql = "SELECT COUNT(*) as total FROM book_issues WHERE status IN ('renewed', 'issued');";
            try (PreparedStatement stmt = conn.prepareStatement(issuedSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("issuedBooks", rs.getInt("total"));
                    }
                }
            }

            int totalBooks=(int) request.getAttribute("totalBooks");
            int issuedBooks=(int) request.getAttribute("issuedBooks");
            request.setAttribute("availableBooks", totalBooks-issuedBooks);
            
            String uniqueBooksSql = "SELECT count(*) as total FROM library_management_system.books;";
            try (PreparedStatement stmt = conn.prepareStatement(uniqueBooksSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("totalUniqueBooks", rs.getInt("total"));
                    }
                }
            }

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error in DashboardServlet: " + e.getMessage());
            request.setAttribute("error", "An error occurred while loading the dashboard");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
} 