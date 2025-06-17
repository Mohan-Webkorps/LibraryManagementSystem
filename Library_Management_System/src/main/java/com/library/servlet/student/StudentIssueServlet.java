package com.library.servlet.student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

import com.library.model.User;
import com.library.util.DatabaseUtil;
import com.library.model.BookIssue;
import com.library.DAO.BookIssueDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/student/issue")
public class StudentIssueServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

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

        String bookIdStr = request.getParameter("bookId");
        if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
            session.setAttribute("error", "Invalid book selection");
            response.sendRedirect(request.getContextPath() + "/student/search");
            return;
        }

        int bookId = Integer.parseInt(bookIdStr);
        int userId = user.getUser_Id();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check if book is available
            if (!isBookAvailable(conn, bookId)) {
                session.setAttribute("error", "Book is not available for issue");
                response.sendRedirect(request.getContextPath() + "/student/search");
                return;
            }

            // Check if student has any overdue books
            if (hasOverdueBooks(conn, userId)) {
                session.setAttribute("error", "Cannot issue book. You have overdue books to return.");
                response.sendRedirect(request.getContextPath() + "/student/search");
                return;
            }

            // Issue the book
            BookIssue bookIssue = new BookIssue();
            bookIssue.setBookId(bookId);
            bookIssue.setUser_Id(userId);
            bookIssue.setIssueDate(Date.valueOf(LocalDate.now()));
            bookIssue.setReturnDate(Date.valueOf(LocalDate.now().plusDays(14)));
            bookIssue.setStatus("issued");

            BookIssueDAO bookIssueDAO = new BookIssueDAO();
            if (bookIssueDAO.issueBook(bookIssue)) {
                session.setAttribute("success", "Book issued successfully");
            } else {
                session.setAttribute("error", "Failed to issue book");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during book issue: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while issuing the book");
        }

        response.sendRedirect(request.getContextPath() + "/student/search");
    }

    private boolean isBookAvailable(Connection conn, int bookId) throws SQLException {
        String sql = "SELECT availableQuantity FROM books WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int availableQuantity = rs.getInt("availableQuantity");
                    return availableQuantity > 0;
                }
            }
        }
        return false;
    }

    private boolean hasOverdueBooks(Connection conn, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM book_issues " +
                    "WHERE user_Id = ? AND status = 'issued' AND return_date < CURRENT_DATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }
} 