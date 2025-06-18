package com.library.servlet.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Book;
import com.library.model.User;
import com.library.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/ViewBooksServlet")
public class ViewBooksServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"admin".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String searchTerm = request.getParameter("searchTerm");
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            List<Book> books;
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // Search query
                String sql = "SELECT b.*, " +
                           "(SELECT COUNT(*) FROM book_issues bi WHERE bi.book_id = b.id AND bi.status IN ('issued', 'renewed') AND bi.return_date >= CURRENT_DATE) as issued_count " +
                           "FROM books b WHERE LOWER(b.book_name) LIKE ? OR LOWER(b.author) LIKE ? ORDER BY b.book_name";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                    stmt.setString(1, searchPattern);
                    stmt.setString(2, searchPattern);
                    books = fetchBooks(stmt);
                }
            } else {
                String sql = "SELECT b.*, " +
                           "(SELECT COUNT(*) FROM book_issues bi WHERE bi.book_id = b.id AND bi.status IN ('issued', 'renewed') AND bi.return_date >= CURRENT_DATE) as issued_count " +
                           "FROM books b ORDER BY b.book_name";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    books = fetchBooks(stmt);
                }
            }
            
            request.setAttribute("books", books);
            request.getRequestDispatcher("/admin/view_books.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in ViewBooksServlet: " + e.getMessage());
            request.setAttribute("error", "An error occurred while loading books");
            request.getRequestDispatcher("/admin/view_books.jsp").forward(request, response);
        }
    }

    private List<Book> fetchBooks(PreparedStatement stmt) throws Exception {
        List<Book> books = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setEdition(rs.getString("edition"));
                int totalQuantity = rs.getInt("quantity");
                int issuedCount = rs.getInt("issued_count");
                book.setQuantity(totalQuantity); // Set total quantity
                book.setAvailableQuantity(Math.max(0, totalQuantity - issuedCount)); // Ensure available quantity never goes below 0
                book.setParkingSlot(rs.getString("parking_slot"));
                books.add(book);
            }
        }
        return books;
    }
} 