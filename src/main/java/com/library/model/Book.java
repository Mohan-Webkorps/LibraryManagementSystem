package com.library.model;

public class Book {
    private int id;
    private String bookName;
    private String author;
    private String edition;
    private int quantity;
    private int availableQuantity;
    private String parkingSlot;

    public Book() {
	}

	public Book(int id, String bookName, String author, String edition, int quantity, int availableQuantity,
			String parkingSlot) {
		this.id = id;
		this.bookName = bookName;
		this.author = author;
		this.edition = edition;
		this.quantity = quantity;
		this.availableQuantity = availableQuantity;
		this.parkingSlot = parkingSlot;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getParkingSlot() {
        return parkingSlot;
    }

    public void setParkingSlot(String parkingSlot) {
        this.parkingSlot = parkingSlot;
    }
} 