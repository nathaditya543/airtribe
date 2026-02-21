import services.BookService;
import services.LendService;
import services.PatronService;
import services.RecommendationService;

public class Main {
    public static void main(String[] args) {
        PatronService patronService = new PatronService();
        BookService bookService = new BookService();
        LendService lendService = new LendService(patronService, bookService);
        RecommendationService recommendationService = new RecommendationService(bookService);
        ConsoleUI ui = new ConsoleUI();

        LibraryController controller = new LibraryController(
                patronService,
                bookService,
                lendService,
                recommendationService,
                ui
        );

        try {
            controller.run();
        } finally {
            ui.close();
        }
    }
}
