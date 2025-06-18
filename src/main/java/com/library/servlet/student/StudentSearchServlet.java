package com.library.servlet.student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@WebServlet("/student/search")
public class StudentSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
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

        String searchTerm = request.getParameter("searchTerm");
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            List<Book> books;
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                books = searchBooks(conn, searchTerm.trim());
                request.setAttribute("searchTerm", searchTerm);
            } else {
                books = getAllAvailableBooks(conn);
            }
            
            request.setAttribute("books", books);
            
        } catch (SQLException e) {
            System.err.println("Database error during search: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while searching for books");
        }

        request.getRequestDispatcher("/student/search_books.jsp").forward(request, response);
    }

    private List<Book> searchBooks(Connection conn, String searchTerm) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, " +
                    "(SELECT COUNT(*) FROM book_issues bi WHERE bi.book_id = b.id AND bi.status IN ('issued', 'renewed')) as issued_count " +
                    "FROM books b " +
                    "WHERE (LOWER(b.book_name) LIKE ? OR LOWER(b.author) LIKE ?) " +
                    "AND b.quantity > 0 " +
                    "ORDER BY b.book_name";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("id"));
                    book.setBookName(rs.getString("book_name"));
                    book.setAuthor(rs.getString("author"));
                    book.setEdition(rs.getString("edition"));
                    int totalQuantity = rs.getInt("quantity");
                    int issuedCount = rs.getInt("issued_count");
                    book.setQuantity(totalQuantity - issuedCount);
                    book.setParkingSlot(rs.getString("parking_slot"));
                    if (book.getQuantity() > 0) {
                        books.add(book);
                    }
                }
            }
        }
        return books;
    }

    private List<Book> getAllAvailableBooks(Connection conn) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, " +
                    "(SELECT COUNT(*) FROM book_issues bi WHERE bi.book_id = b.id AND bi.status IN ('issued', 'renewed')) as issued_count " +
                    "FROM books b " +
                    "WHERE b.quantity > 0 " +
                    "ORDER BY b.book_name";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("id"));
                    book.setBookName(rs.getString("book_name"));
                    book.setAuthor(rs.getString("author"));
                    book.setEdition(rs.getString("edition"));
                    int totalQuantity = rs.getInt("quantity");
                    int issuedCount = rs.getInt("issued_count");
                    book.setQuantity(totalQuantity - issuedCount);
                    book.setParkingSlot(rs.getString("parking_slot"));
                    if (book.getQuantity() > 0) {
                        books.add(book);
                    }
                }
            }
        }
        return books;
    }
} 