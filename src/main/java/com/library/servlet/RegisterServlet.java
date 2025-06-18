package com.library.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.regex.Pattern;

import com.library.util.DatabaseUtil;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	
    private Connection conn;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		String address = request.getParameter("address");
		String libraryName = request.getParameter("libraryName");
		String role = request.getParameter("role");

		if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null
				|| password.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty()) {
			request.setAttribute("error", "All required fields must be filled");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
		}

		if (!isValidEmail(email)) {
			request.setAttribute("error", "Provide valid Email");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
		}

		if (!password.equals(confirmPassword)) {
			request.setAttribute("error", "Passwords do not match");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
		}

		try {
			conn = DatabaseUtil.getConnection();
			String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
			PreparedStatement checkStmt = conn.prepareStatement(checkSql);
				checkStmt.setString(1, email);
					ResultSet rs = checkStmt.executeQuery();
					if (rs.next() && rs.getInt(1) > 0) {
						request.setAttribute("error", "Email already registered");
						request.getRequestDispatcher("/register.jsp").forward(request, response);
						return;				
					}

			String sql = "INSERT INTO users (name, email, password, role, address, library_name) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, password);
			stmt.setString(4, role);
			stmt.setString(5, address);
			stmt.setString(6, libraryName);

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected >= 0) {
				request.setAttribute("success",
						"Registration successful! Please login with your email and password");
				sendEmail(email, name, password);
				request.setAttribute("message", "Registration successful! Email sent.");
				request.getRequestDispatcher("/index.jsp").forward(request, response);
			} else {
				request.setAttribute("error", "Registration failed");
				request.getRequestDispatcher("/register.jsp").forward(request, response);
			}
		}
		catch (Exception e) {
			System.err.println("Error in RegisterServlet: " + e.getMessage());
			request.setAttribute("error", "An error occurred during registration");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
		}
	}

	private void sendEmail(String to, String name, String password) throws Exception {
		int id = 0;
		String from = "mohanyarasu1201@gmail.com";
		String host = "smtp.gmail.com";

		Connection conn = DatabaseUtil.getConnection();
		String checkSql = "SELECT user_Id FROM users WHERE email = ?";
		PreparedStatement checkStmt = conn.prepareStatement(checkSql);
		checkStmt.setString(1, to);
		ResultSet rs = checkStmt.executeQuery();
		if (rs.next()) {
			id = rs.getInt("user_Id");
		}

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("mohanyarasu1201@gmail.com", "byfx ohdi rtmn babf");
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		message.setSubject("Welcome to Library_Management_System");
		message.setText("Hi " + name + ",\n\nYour registration was successful.\n\n" + "Email: " + to + "\nuser_id: "
				+ id + "\nPassword: " + password + "\n\nThank you!");

		Transport.send(message);
	}

	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$";
		return Pattern.matches(emailRegex, email);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/register.jsp").forward(request, response);
	}
}