package com.library.servlet.student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.library.model.User;
import com.library.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/student/renew")
public class StudentRenewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        User user = (User) session.getAttribute("user");

        String issueIdStr = request.getParameter("issueId");
        if (issueIdStr == null || issueIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Invalid book issue ID");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        try {
            int issueId = Integer.parseInt(issueIdStr);
            int userId = user.getUser_Id();

            try (Connection conn = DatabaseUtil.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    if (!isBookIssuedToUser(conn, issueId, userId)) {
                        request.setAttribute("error", "This book is not issued to you");
                    } else if (isBookReserved(conn, issueId)) {
                        request.setAttribute("error", "This book is reserved by another user and cannot be renewed");
                    } else if (renewBook(conn, issueId)) {
                        request.setAttribute("success", "Book renewed successfully");
                        conn.commit();
                    } else {
                        request.setAttribute("error", "Failed to renew book");
                        conn.rollback();
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid book issue ID format");
        } catch (SQLException e) {
            System.err.println("Database error during book renewal: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while renewing the book");
        }

        response.sendRedirect(request.getContextPath() + "/student/dashboard");
    }

    private boolean isBookIssuedToUser(Connection conn, int issueId, int userId) throws SQLException {
        String sql = "SELECT * FROM book_issues WHERE id = ? AND user_Id = ? AND status = 'issued'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, issueId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean isBookReserved(Connection conn, int issueId) throws SQLException {
        String sql = "SELECT bi2.id FROM book_issues bi1 " +
                    "JOIN book_issues bi2 ON bi1.book_id = bi2.book_id " +
                    "WHERE bi1.id = ? AND bi2.status = 'reserved'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean renewBook(Connection conn, int issueId) throws SQLException {
        String sql = "UPDATE book_issues SET status = 'renewed', return_date = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            LocalDate newReturnDate = LocalDate.now().plusDays(14); // 14 days renewal period
            stmt.setDate(1, Date.valueOf(newReturnDate));
            stmt.setInt(2, issueId);
            return stmt.executeUpdate() > 0;
        }
    }
} 