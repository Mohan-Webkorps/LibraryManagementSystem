package com.library.servlet.student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Book;
import com.library.model.BookIssue;
import com.library.model.User;
import com.library.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        updateDashboard(request, user);
        request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
    }

    private void updateDashboard(HttpServletRequest request, User user) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get issued books count with real-time update
            int issuedBooksCount = getIssuedBooksCount(conn, user.getUser_Id());
            request.setAttribute("issuedBooksCount", issuedBooksCount);

            // Get due books count with real-time update
            int dueBooksCount = getDueBooksCount(conn, user.getUser_Id());
            request.setAttribute("dueBooksCount", dueBooksCount);

            // Get recent books with real-time update
            List<BookIssue> recentBooks = getRecentBooks(conn, user.getUser_Id());
            request.setAttribute("recentBooks", recentBooks);

        } catch (SQLException e) {
            System.err.println("Database error while updating dashboard: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Failed to update dashboard information");
        }
    }

    private int getIssuedBooksCount(Connection conn, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM book_issues WHERE user_Id = ? AND status IN ('issued', 'renewed')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private int getDueBooksCount(Connection conn, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM book_issues WHERE user_Id = ? AND status = 'issued' AND return_date < CURRENT_DATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private List<BookIssue> getRecentBooks(Connection conn, int userId) throws SQLException {
        List<BookIssue> books = new ArrayList<>();
        String sql = "SELECT bi.*, b.book_name, b.author, b.edition " +
                    "FROM book_issues bi " +
                    "JOIN books b ON bi.book_id = b.id " +
                    "WHERE bi.user_Id = ? AND bi.status IN ('issued', 'renewed') " +
                    "ORDER BY bi.issue_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookIssue bookIssue = new BookIssue();
                    bookIssue.setId(rs.getInt("id"));
                    bookIssue.setBookId(rs.getInt("book_id"));
                    bookIssue.setUser_Id(rs.getInt("user_Id"));
                    bookIssue.setIssueDate(rs.getDate("issue_date"));
                    bookIssue.setReturnDate(rs.getDate("return_date"));
                    bookIssue.setStatus(rs.getString("status"));

                    Book book = new Book();
                    book.setId(rs.getInt("book_id"));
                    book.setBookName(rs.getString("book_name"));
                    book.setAuthor(rs.getString("author"));
                    book.setEdition(rs.getString("edition"));
                    bookIssue.setBook(book);

                    books.add(bookIssue);
                }
            }
        }
        return books;
    }
} 