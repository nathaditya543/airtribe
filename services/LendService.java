package services;

import entities.Book;
import entities.BookLend;
import entities.Patron;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LendService {
    private final PatronService patronService;
    private final BookService bookService;
    private final Map<Integer, BookLend> activeLendsByBookId = new HashMap<>();
    private final List<BookLend> lendHistory = new ArrayList<>();
    private int lendCounter = 1;

    public LendService(PatronService patronService, BookService bookService) {
        this.patronService = patronService;
        this.bookService = bookService;
    }

    public BookLend lendBook(int patronId, int bookId, int lendDays) {
        Patron patron = patronService.getPatronById(patronId);
        Book book = bookService.getBookById(bookId);

        if (book.isLentOut()) {
            throw new IllegalStateException("Book already lent out: " + bookId);
        }

        LocalDate lendDate = LocalDate.now();
        LocalDate dueDate = lendDate.plusDays(lendDays);

        BookLend lend = new BookLend(lendCounter++, patron, book, lendDate, dueDate);
        book.setLentOut(true);
        patron.addGenreToHistory(book.getGenre());
        activeLendsByBookId.put(bookId, lend);
        lendHistory.add(lend);
        return lend;
    }

    public BookLend returnBook(int bookId) {
        BookLend lend = activeLendsByBookId.remove(bookId);
        if (lend == null) {
            throw new IllegalStateException("No active lend found for book: " + bookId);
        }

        lend.markReturned(LocalDate.now());
        lend.getBook().setLentOut(false);
        return lend;
    }

    public List<BookLend> getActiveLends() {
        return new ArrayList<>(activeLendsByBookId.values());
    }

    public List<BookLend> getLendHistory() {
        return new ArrayList<>(lendHistory);
    }
}
