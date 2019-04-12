package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {

    private Map<ISBN, Book> availableBooks = new HashMap<>();
    private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    public void addBooks(List<Book> books){
        for (Book book : books) {
           this.addBook(book);
        }
    }

    public void addBook(Book book){
        availableBooks.put(book.getIsbn(), book);
    }

    public Book findBook(long isbnCode) {
        return availableBooks.get(new ISBN(isbnCode));
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
        availableBooks.remove(book.getIsbn());
        borrowedBooks.put(book, borrowedAt);
    }

    public LocalDate findBorrowedBookDate(Book book) {
        return borrowedBooks.get(book);
    }

    public Map<ISBN, Book> getAvailableBooks() {
        return availableBooks;
    }

    public Map<Book, LocalDate> getBorrowedBooks() {
        return borrowedBooks;
    }
}
