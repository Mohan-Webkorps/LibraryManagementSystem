package com.library.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.library.model.BookIssue;
import com.library.util.DatabaseUtil;
import com.library.model.Book;

public class BookIssueDAO {
    private BookDAO bookDAO;

    public BookIssueDAO() {
        bookDAO = new BookDAO();
    }

    public boolean issueBook(BookIssue bookIssue) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String checkSql = "SELECT quantity, availableQuantity FROM books WHERE id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, bookIssue.getBookId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Book not found");
                        }
                        int quantity = rs.getInt("quantity");
                        int availableQuantity = rs.getInt("availableQuantity");
                        if (availableQuantity <= 0) {
                            throw new SQLException("No copies available for issue");
                        }
                        if (availableQuantity > quantity) {
                            throw new SQLException("Data inconsistency: Available quantity cannot be greater than total quantity");
                        }
                    }
                }

                String issueSql = "INSERT INTO book_issues (book_id, user_Id, issue_date, return_date, status) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement issueStmt = conn.prepareStatement(issueSql)) {
                    issueStmt.setInt(1, bookIssue.getBookId());
                    issueStmt.setInt(2, bookIssue.getUser_Id());
                    issueStmt.setDate(3, bookIssue.getIssueDate());
                    issueStmt.setDate(4, bookIssue.getReturnDate());
                    issueStmt.setString(5, bookIssue.getStatus());
                    issueStmt.executeUpdate();
                }

                String updateSql = "UPDATE books SET availableQuantity = availableQuantity - 1 WHERE id = ? AND availableQuantity > 0";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, bookIssue.getBookId());
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Book not available for issue");
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBookIssue(BookIssue bookIssue) {
        String sql = "UPDATE book_issues SET status = ?, return_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookIssue.getStatus());
            stmt.setDate(2, bookIssue.getReturnDate());
            stmt.setInt(3, bookIssue.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book issue: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public BookIssue getBookIssueById(int id) {
        String sql = "SELECT * FROM book_issues WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BookIssue bookIssue = new BookIssue();
                    bookIssue.setId(rs.getInt("id"));
                    bookIssue.setBookId(rs.getInt("book_id"));
                    bookIssue.setUser_Id(rs.getInt("user_Id"));
                    bookIssue.setIssueDate(rs.getDate("issue_date"));
                    bookIssue.setReturnDate(rs.getDate("return_date"));
                    bookIssue.setStatus(rs.getString("status"));
                    
                    Book book = bookDAO.getBookById(rs.getInt("book_id"));
                    bookIssue.setBook(book);
                    
                    return bookIssue;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book issue: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int getDueBooksCountByStudent(int user_Id) {
        String sql = "SELECT COUNT(*) FROM book_issues WHERE user_Id = ? AND status = 'issued' AND return_date < CURRENT_DATE";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, user_Id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting due books count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public List<BookIssue> getIssuedBooksByStudent(int user_Id) {
        List<BookIssue> issuedBooks = new ArrayList<>();
        String sql = "SELECT bi.*, b.book_name, b.author, b.edition " +
                    "FROM book_issues bi " +
                    "JOIN books b ON bi.book_id = b.id " +
                    "WHERE bi.user_Id = ? AND bi.status = 'issued' " +
                    "ORDER BY bi.issue_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_Id);
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
                    
                    issuedBooks.add(bookIssue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return issuedBooks;
    }

    public boolean renewBook(int issueId, java.sql.Date newReturnDate) {
        String sql = "UPDATE book_issues SET return_date = ?, status = 'renewed' WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, newReturnDate);
            stmt.setInt(2, issueId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error renewing book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnBook(int issueId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String getBookIdSql = "SELECT book_id FROM book_issues WHERE id = ?";
                int bookId;
                try (PreparedStatement stmt = conn.prepareStatement(getBookIdSql)) {
                    stmt.setInt(1, issueId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Book issue record not found");
                        }
                        bookId = rs.getInt("book_id");
                    }
                }

                String checkSql = "SELECT quantity, availableQuantity FROM books WHERE id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, bookId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Book not found");
                        }
                        int quantity = rs.getInt("quantity");
                        int availableQuantity = rs.getInt("availableQuantity");
                        if (availableQuantity >= quantity) {
                            throw new SQLException("Cannot return book: Available quantity would exceed total quantity");
                        }
                    }
                }

                String updateIssueSql = "UPDATE book_issues SET status = 'returned' WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateIssueSql)) {
                    stmt.setInt(1, issueId);
                    if (stmt.executeUpdate() == 0) {
                        throw new SQLException("Failed to update book issue status");
                    }
                }

                String updateBookSql = "UPDATE books SET availableQuantity = availableQuantity + 1 WHERE id = ? AND availableQuantity < quantity";
                try (PreparedStatement stmt = conn.prepareStatement(updateBookSql)) {
                    stmt.setInt(1, bookId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Cannot return book: Would exceed total quantity");
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<BookIssue> getIssuedBooksWithDetails(int user_Id) {
        List<BookIssue> issues = new ArrayList<>();
        String sql = "SELECT bi.*, b.book_name as book_title, u.name as student_name " +
                    "FROM book_issues bi " +
                    "JOIN books b ON bi.book_id = b.id " +
                    "JOIN users u ON bi.user_Id = u.user_Id " +
                    "WHERE bi.user_Id = ? AND bi.status IN ('issued', 'renewed')";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, user_Id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookIssue issue = new BookIssue();
                    issue.setId(rs.getInt("id"));
                    issue.setBookId(rs.getInt("book_id"));
                    issue.setUser_Id(rs.getInt("user_Id"));
                    issue.setIssueDate(rs.getDate("issue_date"));
                    issue.setReturnDate(rs.getDate("return_date"));
                    issue.setStatus(rs.getString("status"));
                    issues.add(issue);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting issued books with details: " + e.getMessage());
            e.printStackTrace();
        }
        return issues;
    }

    public List<BookIssue> getIssuedBooksByBookId(int bookId) {
        List<BookIssue> issues = new ArrayList<>();
        String sql = "SELECT * FROM book_issues WHERE book_id = ? AND status = 'issued'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookIssue issue = new BookIssue();
                    issue.setId(rs.getInt("id"));
                    issue.setBookId(rs.getInt("book_id"));
                    issue.setUser_Id(rs.getInt("user_Id"));
                    issue.setIssueDate(rs.getDate("issue_date"));
                    issue.setReturnDate(rs.getDate("return_date"));
                    issue.setStatus(rs.getString("status"));
                    issues.add(issue);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting issued books by book ID: " + e.getMessage());
            e.printStackTrace();
        }
        return issues;
    }
} 