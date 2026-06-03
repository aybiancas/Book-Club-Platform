package model;

import java.util.Objects;

public class Book implements Comparable<Book> {
    private String title, author;
    private final String isbn;
    private int noOfPages;
    private Category category;

    public Book (String isbn, String title, String author, Category category, int noOfPages) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.noOfPages = noOfPages;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Category getCategory() {
        return category;
    }

    public int getNoOfPages() {
        return noOfPages;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setNoOfPages(int noOfPages) {
        this.noOfPages = noOfPages;
    }

    @Override
    public int compareTo(Book other) {
        int cmp = this.title.compareToIgnoreCase(other.title);
        return cmp != 0 ? cmp : this.isbn.compareTo(other.isbn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return Objects.equals(isbn, ((Book) o).isbn);
    }

    @Override
    public int hashCode() { return Objects.hash(isbn); }

    @Override
    public String toString() {
        return String.format("\"%s\" by %s (%d)", title, author, noOfPages);
    }

}
