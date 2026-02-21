package entities;

import java.time.LocalDate;

public class BookLend {
    private final int id;
    private final Patron patron;
    private final Book book;
    private final LocalDate lendDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public BookLend(int id, Patron patron, Book book, LocalDate lendDate, LocalDate dueDate) {
        this.id = id;
        this.patron = patron;
        this.book = book;
        this.lendDate = lendDate;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public Patron getPatron() {
        return patron;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getLendDate() {
        return lendDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "BookLend{id=" + id
                + ", patronId=" + patron.getId()
                + ", bookId=" + book.getId()
                + ", lendDate=" + lendDate
                + ", dueDate=" + dueDate
                + ", returnDate=" + returnDate + "}";
    }
}
