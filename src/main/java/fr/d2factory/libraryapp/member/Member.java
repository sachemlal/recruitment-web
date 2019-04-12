package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.library.Library;
import fr.d2factory.libraryapp.library.NoMoneyLeftException;
import java.util.HashSet;
import java.util.Set;

/**
 * A member is a person who can borrow and return books to a {@link Library}
 * A member can be either a student or a resident
 */
public abstract class Member {

    String firstName;
    String lastName;
    int freePeriod = 0;
    int maxPeriod;
    private float rentPricePerDay;
    private float rentPricePerLateDay;
    /**
     * An initial sum of money the member has
     */
    private float wallet;
    private Set<Book> borrowedBooks = new HashSet<Book>();

    public Member(String firstName, String lastName, float wallet) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.wallet = wallet;
    }

    public float getWallet() {
        return wallet;
    }

    public void setWallet(float wallet) {
        this.wallet = wallet;
    }

    public int getFreePeriod() {
        return freePeriod;
    }

    public void setFreePeriod(int freePeriod) {
        this.freePeriod = freePeriod;
    }

    public int getMaxPeriod() {
        return maxPeriod;
    }

    public void setMaxPeriod(int maxPeriod) {
        this.maxPeriod = maxPeriod;
    }

    public float getRentPricePerDay() {
        return rentPricePerDay;
    }

    public void setRentPricePerDay(float rentPricePerDay) {
        this.rentPricePerDay = rentPricePerDay;
    }

    public float getRentPricePerLateDay() {
        return rentPricePerLateDay;
    }

    public void setRentPricePerLateDay(float rentPricePerLateDay) {
        this.rentPricePerLateDay = rentPricePerLateDay;
    }

    public void borrowBook(Book book) {
        this.borrowedBooks.add(book);
    }

    public Set<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    /**
     * The member should pay their books when they are returned to the library
     *
     * @param numberOfDays the number of days they kept the book
     */
    public void payBook(long numberOfDays) {
        float amountToPay = 0;
        if(numberOfDays > this.getFreePeriod() && numberOfDays <= this.getMaxPeriod()) {
            amountToPay += (numberOfDays - this.getFreePeriod()) * this.getRentPricePerDay();
        }
        else if(numberOfDays > this.getMaxPeriod()) {
            amountToPay += (this.getMaxPeriod() - this.getFreePeriod()) * this.getRentPricePerDay();
            amountToPay += (numberOfDays - this.getMaxPeriod()) * this.getRentPricePerLateDay();
        }
        if(this.getWallet() - amountToPay < 0) {
            throw new NoMoneyLeftException();
        }

        this.setWallet(this.getWallet() - amountToPay);
    }
}
