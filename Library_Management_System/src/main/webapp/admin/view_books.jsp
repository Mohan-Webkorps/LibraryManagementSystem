<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.library.model.Book"%>
<%@ page import="com.library.model.User"%>
<%
User user = (User) session.getAttribute("user");
String searchTerm = request.getParameter("searchTerm");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>View Books - Library Management System</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
	<header>
		<h1>Library Management System</h1>
		<p>
			Welcome,
			<%=user != null ? user.getName() : "Guest"%></p>
	</header>

	<div class="container">
		<div class="dashboard">
			<div class="sidebar">
				<h3>Navigation</h3>
				<ul>
					<li><a href="${pageContext.request.contextPath}/admin/DashboardServlet">Dashboard</a></li>
					<li><a href="${pageContext.request.contextPath}/admin/manage_books.jsp">Manage Books</a></li>
					<li><a href="${pageContext.request.contextPath}/admin/ViewBooksServlet">View All Books</a></li>					
                    <li><a href="${pageContext.request.contextPath}/admin/BookIssueHistoryServlet">View Issuances</a></li>
					<li><a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></li>
				</ul>
			</div>

			<div class="main-content">
				<h2>View Books</h2>

				<div class="search-section">
					<form
						action="${pageContext.request.contextPath}/admin/ViewBooksServlet"
						method="get">
						<input type="text" name="searchTerm"
							placeholder="Search by book name or author..."
							value="<%=searchTerm != null ? searchTerm : ""%>">
						<button type="submit" class="btn">Search</button>
					</form>
				</div>

				<div class="book-list">
					<table>
						<thead>
							<tr>
								<th>Book Name</th>
								<th>Author</th>
								<th>Edition</th>
								<th>Total Quantity</th>
								<th>Available</th>
								<th>Parking Slot</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							<%
							List<Book> books = (List<Book>) request.getAttribute("books");
							if (books != null) {
								for (Book book : books) {
							%>
							<tr>
								<td><%=book.getBookName()%></td>
								<td><%=book.getAuthor()%></td>
								<td><%=book.getEdition()%></td>
								<td><%=book.getQuantity()%></td>
								<td><%=book.getAvailableQuantity()%></td>
								<td><%=book.getParkingSlot()%></td>
								<td><a
									href="${pageContext.request.contextPath}/admin/BookIssueHistoryServlet?searchTerm=<%= book.getBookName() %>"
									class="btn">View Issue History</a></td>
							<%
							}
							}else{}
							%>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
