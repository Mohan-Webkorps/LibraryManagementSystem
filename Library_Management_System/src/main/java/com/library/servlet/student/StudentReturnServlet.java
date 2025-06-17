package com.library.servlet.student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.library.model.User;
import com.library.util.DatabaseUtil;
import com.library.DAO.BookIssueDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/student/return")
public class StudentReturnServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookIssueDAO bookIssueDAO;

    public void init() {
        bookIssueDAO = new BookIssueDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"student".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String issueIdStr = request.getParameter("issueId");
        if (issueIdStr == null || issueIdStr.trim().isEmpty()) {
            session.setAttribute("error", "Invalid book selection");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        int issueId = Integer.parseInt(issueIdStr);
        int userId = user.getUser_Id();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Verify the book belongs to the student
            if (!isBookIssuedToStudent(conn, issueId, userId)) {
                session.setAttribute("error", "This book is not issued to you");
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
                return;
            }

            // Return the book using BookIssueDAO
            if (bookIssueDAO.returnBook(issueId)) {
                session.setAttribute("success", "Book returned successfully");
            } else {
                session.setAttribute("error", "Failed to return book");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during book return: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while returning the book");
        }

        response.sendRedirect(request.getContextPath() + "/student/dashboard");
    }

    private boolean isBookIssuedToStudent(Connection conn, int issueId, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM book_issues " +
                    "WHERE id = ? AND user_Id = ? AND status IN ('issued', 'renewed')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, issueId);
            stmt.setInt(2, userId);
            try (var rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        }
    }
} 