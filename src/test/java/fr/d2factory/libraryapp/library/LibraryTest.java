package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class LibraryTest {

    private Library library ;
    private BookRepository bookRepository;
    private String testFilePath = "src\\test\\resources\\books.json";

    @Before
    public void setup(){
        this.bookRepository = new BookRepository();
        this.loadTestData();

        this.library = new LibraryImpl();
        ((LibraryImpl)this.library).setBookRepository(this.bookRepository);
    }

    @Test
    public void member_can_borrow_a_book_if_book_is_available(){

        Member member = new Student("student", "1", 10, 2);
        long isbnTest = 3326456467846L;
        Book borrowedBook = this.library.borrowBook(isbnTest,  member,  LocalDate.now());

        assertTrue(((LibraryImpl)this.library).getBookRepository().getAvailableBooks().get(new ISBN(isbnTest)) == null );

        LocalDate date = ((LibraryImpl)this.library).getBookRepository().getBorrowedBooks().get(borrowedBook);
        assertNull(date);
    }

    @Test
    public void borrowed_book_is_no_longer_available(){
        Member member = new Student("student", "1", 10, 2);
        long isbnTest = 3326456467846L;
        this.library.borrowBook(isbnTest,  member, LocalDate.now());

        Member member2 = new Student("student2", "2", 15, 1);
        this.library.borrowBook(isbnTest,  member2, LocalDate.now());

        assertTrue(member.getBorrowedBooks().size() == 1);
        assertTrue(member2.getBorrowedBooks().size() == 0);
    }

    @Test
    public void residents_are_taxed_10cents_for_each_day_they_keep_a_book(){
        long isbnTest = 3326456467846L;
        long nbrOfDays = 10;
        Book book = ((LibraryImpl)this.library).bookRepository.findBook(isbnTest);
        Member member = new Resident("Resident", "1", 10);
        LocalDate oldDate = LocalDate.from(LocalDate.now()).minusDays(nbrOfDays);

        this.library.borrowBook(isbnTest, member, oldDate);
        this.library.returnBook(book, member);

        assertEquals((long) 9, (long) member.getWallet());
    }

    @Test
    public void students_pay_10_cents_the_first_30days(){
        long isbnTest = 3326456467846L;
        long nbrOfDays = 30;
        Book book = ((LibraryImpl)this.library).bookRepository.findBook(isbnTest);
        Member member = new Student("student", "1", 10, 2);
        LocalDate oldDate = LocalDate.from(LocalDate.now()).minusDays(nbrOfDays);

        this.library.borrowBook(isbnTest, member, oldDate);
        this.library.returnBook(book, member);

        assertEquals((long) 7, (long) member.getWallet());
    }

    @Test
    public void students_in_1st_year_are_not_taxed_for_the_first_15days(){
        long isbnTest = 3326456467846L;
        long nbrOfDays = 30;
        Book book = ((LibraryImpl)this.library).bookRepository.findBook(isbnTest);
        Member member = new Student("student", "1", 10, 1);
        LocalDate oldDate = LocalDate.from(LocalDate.now()).minusDays(nbrOfDays);

        this.library.borrowBook(isbnTest, member, oldDate);
        this.library.returnBook(book, member);

        assertEquals((long) 8.5, (long) member.getWallet());
    }

    @Test
    public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days(){
        long isbnTest = 3326456467846L;
        long isbnTest2 = 3326456467846L;
        long nbrOfDays = 40;
        Book book1 = ((LibraryImpl)this.library).bookRepository.findBook(isbnTest);
        Book book2 = ((LibraryImpl)this.library).bookRepository.findBook(isbnTest2);

        Member memberFirstYear = new Student("student", "1", 10, 1);
        Member memberSecondYear = new Student("student2", "2", 10, 2);
        LocalDate oldDate = LocalDate.from(LocalDate.now()).minusDays(nbrOfDays);

        this.library.borrowBook(isbnTest, memberFirstYear, oldDate);
        this.library.returnBook(book1, memberFirstYear);

        this.library.borrowBook(isbnTest, memberSecondYear, oldDate);
        this.library.returnBook(book2, memberSecondYear);

        assertEquals((long) 7, (long) memberFirstYear.getWallet());
        assertEquals((long) 5.5, (long) memberSecondYear.getWallet());
    }

    @Test
    public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(){
        long isbnTest = 3326456467846L;
        long nbrOfDays = 70;
        Book book = ((LibraryImpl)this.library).bookRepository.findBook(isbnTest);
        Member member = new Resident("Residentnt", "1", 10);
        LocalDate oldDate = LocalDate.from(LocalDate.now()).minusDays(nbrOfDays);

        this.library.borrowBook(isbnTest, member, oldDate);
        this.library.returnBook(book, member);

        assertEquals((long) 2, (long) member.getWallet());
    }

    @Test(expected = HasLateBooksException.class)
    public void members_cannot_borrow_book_if_they_have_late_books(){
        Member member = new Student("student", "1", 10, 2);
        long isbnTest = 3326456467846L;
        long isbnTest2 = 968787565445L;

        LocalDate oldDate = LocalDate.from(LocalDate.now()).minusDays(40);
        this.library.borrowBook(isbnTest, member, oldDate);
        assertTrue(((LibraryImpl)this.library).checkIfLateMember(member));

        assertEquals((long) 1, (long) member.getBorrowedBooks().size());

        // trying a second borrow which must throw an exception because the member is late
        this.library.borrowBook(isbnTest2, member, LocalDate.now());
    }

    @Test
    public void member_can_borrow_another_book_if_not_late(){
        long isbnTest = 3326456467846L;
        long nbrOfDays = 30;
        Book book = ((LibraryImpl)this.library).bookRepository.findBook(isbnTest);
        Member member = new Student("student", "1", 10, 1);
        LocalDate oldDate = LocalDate.from(LocalDate.now()).minusDays(nbrOfDays);

        this.library.borrowBook(isbnTest, member, oldDate);
        this.library.returnBook(book, member);

        assertEquals((long) 8.5, (long) member.getWallet());

        nbrOfDays = 17;
        oldDate = LocalDate.from(LocalDate.now()).minusDays(nbrOfDays);

        this.library.borrowBook(isbnTest, member, oldDate);
        this.library.returnBook(book, member);

        assertTrue(Float.compare((float) 8.3, member.getWallet()) == 0);
    }

    private void loadTestData() {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(this.testFilePath))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray bookList = (JSONArray) obj;
            //Iterate over book array
            bookList.forEach( book -> parseBookObject( (JSONObject) book ) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseBookObject(JSONObject book)
    {
        String title = (String) book.get("title");
        String author = (String) book.get("author");

        JSONObject isbn = (JSONObject) book.get("isbn");
        Object isbnCode = isbn.get("isbnCode");

        Book tmpBook = new Book(title, author, new ISBN((long)isbnCode));
        this.bookRepository.addBook(tmpBook);
    }
}
