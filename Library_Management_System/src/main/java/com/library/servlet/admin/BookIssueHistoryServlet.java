package com.library.servlet.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.ConnectionEvent;

import com.library.model.Book;
import com.library.model.User;
import com.library.model.BookIssue;
import com.library.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/BookIssueHistoryServlet")
public class BookIssueHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Connection conn;

        User user = (User) session.getAttribute("user");
        if (!"admin".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String searchTerm = request.getParameter("searchTerm");
        
        try {
        	conn = DatabaseUtil.getConnection();
            List<BookIssue> issueHistory;
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // Search query
                String sql = "SELECT bi.*, b.book_name, b.author, b.edition, " +
                             "u.name as student_name, u.email as student_email " +
                             "FROM book_issues bi " +
                             "JOIN books b ON bi.book_id = b.id " +
                             "JOIN users u ON bi.user_id = u.user_Id " +
                             "WHERE (LOWER(b.book_name) LIKE ? OR LOWER(u.name) LIKE ?) " +
                             "AND bi.status IN ('issued', 'renewed') " +
                             "ORDER BY bi.issue_date DESC";
                           
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                    stmt.setString(1, searchPattern);
                    stmt.setString(2, searchPattern);
                    issueHistory = fetchIssueHistory(stmt);
                }
            } else {
                String sql = "SELECT bi.*, b.book_name, b.author, b.edition, " +
                             "u.name as student_name, u.email as student_email " +
                             "FROM book_issues bi " +
                             "JOIN books b ON bi.book_id = b.id " +
                             "JOIN users u ON bi.user_id = u.user_Id " +
                             "WHERE bi.status IN ('issued', 'renewed') " +
                             "ORDER BY bi.issue_date DESC";                           
               PreparedStatement stmt = conn.prepareStatement(sql); 
               issueHistory = fetchIssueHistory(stmt);                
            }
            
            request.setAttribute("issueHistory", issueHistory);
            request.getRequestDispatcher("/admin/book_issue_history.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in BookIssueHistoryServlet: " + e.getMessage());
            request.setAttribute("error", "An error occurred while loading issue history");
            request.getRequestDispatcher("/admin/book_issue_history.jsp").forward(request, response);
        }
    }

    private List<BookIssue> fetchIssueHistory(PreparedStatement stmt) throws Exception {
        List<BookIssue> issueHistory = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BookIssue issue = new BookIssue();
                issue.setId(rs.getInt("id"));
                issue.setBookId(rs.getInt("book_id"));
                issue.setUser_Id(rs.getInt("user_id"));
                issue.setIssueDate(rs.getDate("issue_date"));
                issue.setReturnDate(rs.getDate("return_date"));
                issue.setStatus(rs.getString("status"));

                Book book = new Book();
                book.setId(rs.getInt("book_id"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setEdition(rs.getString("edition"));
                issue.setBook(book);

                User student = new User();
                student.setUser_Id(rs.getInt("user_id"));
                student.setName(rs.getString("student_name"));
                student.setEmail(rs.getString("student_email"));
                issue.setUser(student);

                issueHistory.add(issue);
            }
        return issueHistory;
    }
} 