package services;

import entities.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {
    private final Map<Integer, Book> booksById = new HashMap<>();
    private final Map<String, Book> booksByIsbn = new HashMap<>();
    private final Map<String, List<Book>> booksByName = new HashMap<>();
    private final Map<String, List<Book>> booksByAuthor = new HashMap<>();
    private final Map<String, List<Book>> booksByGenre = new HashMap<>();
    private int bookCounter = 1;

    public Book createBook(String title, String author, String isbn, String genre) {
        String isbnKey = normalize(isbn);
        if (booksByIsbn.containsKey(isbnKey)) {
            throw new IllegalArgumentException("Book already exists with ISBN: " + isbn);
        }

        Book book = new Book(bookCounter++, title, author, isbn, genre);
        booksById.put(book.getId(), book);
        indexBook(book);
        return book;
    }

    public Book getBookById(int bookId) {
        Book book = booksById.get(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        return book;
    }

    public Book updateBook(int bookId, String title, String author, String isbn, String genre) {
        Book book = getBookById(bookId);

        String newIsbnKey = normalize(isbn);
        Book existingWithIsbn = booksByIsbn.get(newIsbnKey);
        if (existingWithIsbn != null && existingWithIsbn.getId() != bookId) {
            throw new IllegalArgumentException("Book already exists with ISBN: " + isbn);
        }

        unindexBook(book);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setGenre(genre);
        indexBook(book);
        return book;
    }

    public void removeBook(int bookId) {
        Book book = getBookById(bookId);
        if (book.isLentOut()) {
            throw new IllegalStateException("Cannot remove a book that is currently lent out: " + bookId);
        }
        unindexBook(book);
        booksById.remove(bookId);
    }

    public Book getBookByISBN(String isbn) {
        Book book = booksByIsbn.get(normalize(isbn));
        if (book == null) {
            throw new IllegalArgumentException("Book not found for ISBN: " + isbn);
        }
        return book;
    }

    public List<Book> getBooksByName(String name) {
        List<Book> matches = booksByName.get(normalize(name));
        if (matches == null || matches.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(matches);
    }

    public List<Book> getBooksByAuthor(String author) {
        List<Book> matches = booksByAuthor.get(normalize(author));
        return matches == null ? new ArrayList<>() : new ArrayList<>(matches);
    }

    public List<Book> getBooksByGenre(String genre) {
        List<Book> matches = booksByGenre.get(normalize(genre));
        return matches == null ? new ArrayList<>() : new ArrayList<>(matches);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(booksById.values());
    }

    public List<String> getInventory() {
        List<String> inventory = new ArrayList<>();
        for (Book book : booksById.values()) {
            String status = book.isLentOut() ? "LENT_OUT" : "IN_LIBRARY";
            inventory.add("Book{id=" + book.getId()
                    + ", title='" + book.getTitle()
                    + "', isbn='" + book.getIsbn()
                    + "', status=" + status + "}");
        }
        return inventory;
    }

    private void indexBook(Book book) {
        booksByIsbn.put(normalize(book.getIsbn()), book);
        addToListIndex(booksByName, normalize(book.getTitle()), book);
        addToListIndex(booksByAuthor, normalize(book.getAuthor()), book);
        addToListIndex(booksByGenre, normalize(book.getGenre()), book);
    }

    private void unindexBook(Book book) {
        booksByIsbn.remove(normalize(book.getIsbn()));
        removeFromListIndex(booksByName, normalize(book.getTitle()), book);
        removeFromListIndex(booksByAuthor, normalize(book.getAuthor()), book);
        removeFromListIndex(booksByGenre, normalize(book.getGenre()), book);
    }

    private void addToListIndex(Map<String, List<Book>> index, String key, Book book) {
        index.computeIfAbsent(key, k -> new ArrayList<>()).add(book);
    }

    private void removeFromListIndex(Map<String, List<Book>> index, String key, Book book) {
        List<Book> books = index.get(key);
        if (books == null) {
            return;
        }

        books.removeIf(b -> b.getId() == book.getId());
        if (books.isEmpty()) {
            index.remove(key);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
