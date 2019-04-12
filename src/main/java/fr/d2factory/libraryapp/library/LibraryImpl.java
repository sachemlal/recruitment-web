package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;

import java.time.Duration;
import java.time.LocalDate;

public class LibraryImpl implements Library
{

    public BookRepository bookRepository;

    private long calculateNbrOfDays(Book book) {
        LocalDate borrowDate = this.getBookRepository().findBorrowedBookDate(book);
        LocalDate now = LocalDate.now();
        return Duration.between(borrowDate.atStartOfDay(), now.atStartOfDay()).toDays();
    }

    public boolean checkIfLateMember(Member member) {
        for (Book book : member.getBorrowedBooks()) {
            long nbrDays = this.calculateNbrOfDays(book);
            if(nbrDays > member.getMaxPeriod()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {
        if(member instanceof Student || member instanceof Resident) {
            if(this.checkIfLateMember(member)) {
                throw new HasLateBooksException();
            } else {
                Book availableBook = this.getBookRepository().findBook(isbnCode);
                if(availableBook != null) {
                    this.getBookRepository().saveBookBorrow(availableBook, borrowedAt);
                    member.borrowBook(availableBook);
                }
            }
        }
        return null;
    }

    @Override
    public void returnBook(Book book, Member member) {
        long numberOfDays = this.calculateNbrOfDays(book);
        member.payBook(numberOfDays);
        member.getBorrowedBooks().remove(book);
        this.getBookRepository().getBorrowedBooks().remove(book);
        this.getBookRepository().addBook(book);
    }

    public BookRepository getBookRepository() {
        return bookRepository;
    }

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
