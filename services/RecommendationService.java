package services;

import entities.Book;
import entities.Patron;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationService {
    private final BookService bookService;

    public RecommendationService(BookService bookService) {
        this.bookService = bookService;
    }

    public List<Book> recommendBooks(Patron patron) {
        Set<String> genres = new HashSet<>(patron.getHistory());
        List<Book> recommendations = new ArrayList<>();

        for (Book book : bookService.getAllBooks()) {
            if (!book.isLentOut() && genres.contains(book.getGenre())) {
                recommendations.add(book);
            }
        }

        return recommendations;
    }
}
