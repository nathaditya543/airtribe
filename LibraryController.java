import entities.Book;
import entities.BookLend;
import entities.Patron;
import services.BookService;
import services.LendService;
import services.PatronService;
import services.RecommendationService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LibraryController {
    private final PatronService patronService;
    private final BookService bookService;
    private final LendService lendService;
    private final RecommendationService recommendationService;
    private final ConsoleUI ui;
    private final Map<Integer, MenuAction> mainActions = new LinkedHashMap<>();
    private boolean running = true;

    public LibraryController(
            PatronService patronService,
            BookService bookService,
            LendService lendService,
            RecommendationService recommendationService,
            ConsoleUI ui
    ) {
        this.patronService = patronService;
        this.bookService = bookService;
        this.lendService = lendService;
        this.recommendationService = recommendationService;
        this.ui = ui;
        registerMainActions();
    }

    public void run() {
        while (running) {
            ui.printMainMenu(toLabelMap(mainActions));
            int choice = ui.readInt("Choose an option: ");
            MenuAction action = mainActions.get(choice);
            if (action == null) {
                ui.printMessage("Invalid option.");
                continue;
            }

            try {
                action.execute();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ui.printMessage("Error: " + ex.getMessage());
            }
        }
    }

    private void registerMainActions() {
        addMainAction(1, "Create patron", this::createPatron);
        addMainAction(2, "Create book", this::createBook);
        addMainAction(3, "List patrons", this::listPatrons);
        addMainAction(4, "List books", this::listBooks);
        addMainAction(5, "Lend book", this::lendBook);
        addMainAction(6, "Return book", this::returnBook);
        addMainAction(7, "Show active lends", this::showActiveLends);
        addMainAction(8, "Show lend history", this::showLendHistory);
        addMainAction(9, "Search books", this::searchBooks);
        addMainAction(10, "Show recommendations for patron", this::showRecommendations);
        addMainAction(11, "Show inventory", this::showInventory);
        addMainAction(0, "Exit", this::exit);
    }

    private void addMainAction(int key, String label, Runnable task) {
        mainActions.put(key, new RunnableMenuAction(label, task));
    }

    private Map<Integer, String> toLabelMap(Map<Integer, MenuAction> actions) {
        Map<Integer, String> labels = new LinkedHashMap<>();
        for (Map.Entry<Integer, MenuAction> entry : actions.entrySet()) {
            labels.put(entry.getKey(), entry.getValue().label());
        }
        return labels;
    }

    private void createPatron() {
        String name = ui.readLine("Enter patron name: ");
        String email = ui.readLine("Enter patron email: ");
        Patron patron = patronService.createPatron(name, email);
        ui.printMessage("Created: " + patron);
    }

    private void createBook() {
        String title = ui.readLine("Enter book title: ");
        String author = ui.readLine("Enter book author: ");
        String isbn = ui.readLine("Enter book ISBN: ");
        String genre = ui.readLine("Enter book genre: ");
        Book book = bookService.createBook(title, author, isbn, genre);
        ui.printMessage("Created: " + book);
    }

    private void listPatrons() {
        ui.printPatrons(patronService.getAllPatrons());
    }

    private void listBooks() {
        ui.printBooks(bookService.getAllBooks());
    }

    private void lendBook() {
        int patronId = ui.readInt("Enter patron ID: ");
        int bookId = ui.readInt("Enter book ID: ");
        int lendDays = ui.readInt("Enter lend days: ");
        BookLend lend = lendService.lendBook(patronId, bookId, lendDays);
        ui.printMessage("Lent: " + lend);
    }

    private void returnBook() {
        int bookId = ui.readInt("Enter book ID to return: ");
        BookLend lend = lendService.returnBook(bookId);
        ui.printMessage("Returned: " + lend);
    }

    private void showActiveLends() {
        ui.printLends(lendService.getActiveLends(), "No active lends.");
    }

    private void showLendHistory() {
        ui.printLends(lendService.getLendHistory(), "No lend history.");
    }

    private void searchBooks() {
        Map<Integer, MenuAction> searchActions = new LinkedHashMap<>();
        searchActions.put(1, new RunnableMenuAction("ISBN", this::searchByIsbn));
        searchActions.put(2, new RunnableMenuAction("Name", this::searchByName));
        searchActions.put(3, new RunnableMenuAction("Author", this::searchByAuthor));
        searchActions.put(4, new RunnableMenuAction("Genre", this::searchByGenre));

        ui.printMenu("Search by:", toLabelMap(searchActions));
        int option = ui.readInt("Choose search option: ");
        MenuAction searchAction = searchActions.get(option);
        if (searchAction == null) {
            ui.printMessage("Invalid search option.");
            return;
        }
        searchAction.execute();
    }

    private void searchByIsbn() {
        String isbn = ui.readLine("Enter ISBN: ");
        ui.printMessage(String.valueOf(bookService.getBookByISBN(isbn)));
    }

    private void searchByName() {
        String name = ui.readLine("Enter name: ");
        ui.printBooks(bookService.getBooksByName(name));
    }

    private void searchByAuthor() {
        String author = ui.readLine("Enter author: ");
        ui.printBooks(bookService.getBooksByAuthor(author));
    }

    private void searchByGenre() {
        String genre = ui.readLine("Enter genre: ");
        ui.printBooks(bookService.getBooksByGenre(genre));
    }

    private void showRecommendations() {
        int patronId = ui.readInt("Enter patron ID: ");
        Patron patron = patronService.getPatronById(patronId);
        List<Book> recommendations = recommendationService.recommendBooks(patron);
        ui.printBooks(recommendations);
    }

    private void showInventory() {
        ui.printInventory(bookService.getInventory());
    }

    private void exit() {
        running = false;
        ui.printMessage("Exiting.");
    }

    private static final class RunnableMenuAction implements MenuAction {
        private final String label;
        private final Runnable task;

        private RunnableMenuAction(String label, Runnable task) {
            this.label = label;
            this.task = task;
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public void execute() {
            task.run();
        }
    }
}
