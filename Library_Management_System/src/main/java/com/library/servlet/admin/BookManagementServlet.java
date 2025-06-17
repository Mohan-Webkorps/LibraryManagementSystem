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

@WebServlet("/admin/BookManagementServlet")
public class BookManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection conn;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
		HttpSession session = request.getSession(false);
     
        String action = request.getParameter("action");
	        
        if (action == null) {
	            action = "list";
	        }
	        switch (action) {
	            case "edit":
	                showEditForm(request, response);
	                break;
	            case "list":
	            default:
	                listBooks(request, response);
	                break;
	        }
    }
        
    

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute("user");

        String action = request.getParameter("action");
        switch (action) {
            case "add":
                addBook(request, response);
                break;
            case "update":
                updateBook(request, response);
                break;
            case "delete":
                deleteBook(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/manage_books.jsp");
                break;
        }
        
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
        	conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM books ORDER BY book_name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
                    List<Book> books = new ArrayList<>();
                    while (rs.next()) {
                        Book book = new Book();
                        book.setId(rs.getInt("id"));
                        book.setBookName(rs.getString("book_name"));
                        book.setAuthor(rs.getString("author"));
                        book.setEdition(rs.getString("edition"));
                        book.setQuantity(rs.getInt("quantity"));
                        book.setParkingSlot(rs.getString("parking_slot"));
                        books.add(book);
                    }
                    request.setAttribute("books", books);                
                    request.getRequestDispatcher("/admin/manage_books.jsp").forward(request, response);
            }
         catch (Exception e) {
            System.err.println("Error listing books: " + e.getMessage());
            throw new ServletException(e);
        }
    }


    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("id"));
                    book.setBookName(rs.getString("book_name"));
                    book.setAuthor(rs.getString("author"));
                    book.setEdition(rs.getString("edition"));
                    book.setQuantity(rs.getInt("quantity"));
                    book.setParkingSlot(rs.getString("parking_slot"));
                    request.setAttribute("book", book);
                }                    
            request.getRequestDispatcher("/admin/edit_book.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error showing edit form: " + e.getMessage());
            throw new ServletException(e);
        }
    }

    private void addBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String bookName = request.getParameter("bookName");
        String author = request.getParameter("author");
        String edition = request.getParameter("edition");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String parkingSlot = request.getParameter("parkingSlot");

        try {
        	conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO books (book_name, author, edition, quantity, parking_slot) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookName);
            stmt.setString(2, author);
            stmt.setString(3, edition);
            stmt.setInt(4, quantity);
            stmt.setString(5, parkingSlot);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                request.getSession().setAttribute("success", "Book added successfully");
            } else {
                request.getSession().setAttribute("error", "Failed to add book");
            }
            
        } catch (Exception e) {
            System.err.println("Error adding book: " + e.getMessage());
            request.getSession().setAttribute("error", "Error adding book: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/manage_books.jsp");
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String bookName = request.getParameter("bookName");
        String author = request.getParameter("author");
        String edition = request.getParameter("edition");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String parkingSlot = request.getParameter("parkingSlot");

        try {
        	conn = DatabaseUtil.getConnection();
            String sql = "UPDATE books SET book_name=?, author=?, edition=?, quantity=?, parking_slot=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, bookName);
                stmt.setString(2, author);
                stmt.setString(3, edition);
                stmt.setInt(4, quantity);
                stmt.setString(5, parkingSlot);
                stmt.setInt(6, id);
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    request.getSession().setAttribute("success", "Book updated successfully");
                } else {
                    request.getSession().setAttribute("error", "Failed to update book");
                }
        }
        catch (Exception e) {
            System.err.println("Error updating book: " + e.getMessage());
            request.getSession().setAttribute("error", "Error updating book: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/manage_books.jsp");
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        try {
        	conn = DatabaseUtil.getConnection();
            String checkSql = "SELECT COUNT(*) as count FROM book_issues WHERE book_id = ? AND status IN ('issued', 'renewed')";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
			checkStmt.setInt(1, id);
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt("count") > 0) {
				request.getSession().setAttribute("error", "Cannot delete book as it is currently issued");
				response.sendRedirect(request.getContextPath() + "/admin/manage_books.jsp");
				return;
			}
			String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                request.getSession().setAttribute("success", "Book deleted successfully");
            } else {
                request.getSession().setAttribute("error", "Failed to delete book");
            }
            
        } catch (Exception e) {
            System.err.println("Error deleting book: " + e.getMessage());
            request.getSession().setAttribute("error", "Error deleting book: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/manage_books.jsp");
    }
}