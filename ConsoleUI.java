import entities.Book;
import entities.BookLend;
import entities.Patron;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    public void printMainMenu(Map<Integer, String> options) {
        System.out.println();
        System.out.println("=== Library Tracking System ===");
        printOptions(options);
    }

    public void printMenu(String title, Map<Integer, String> options) {
        System.out.println(title);
        printOptions(options);
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public void printPatrons(List<Patron> patrons) {
        if (patrons.isEmpty()) {
            System.out.println("No patrons found.");
            return;
        }
        for (Patron patron : patrons) {
            System.out.println(patron);
        }
    }

    public void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void printLends(List<BookLend> lends, String emptyMessage) {
        if (lends.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (BookLend lend : lends) {
            System.out.println(lend);
        }
    }

    public void printInventory(List<String> inventory) {
        if (inventory.isEmpty()) {
            System.out.println("No books in inventory.");
            return;
        }
        for (String item : inventory) {
            System.out.println(item);
        }
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void close() {
        scanner.close();
    }

    public static Map<Integer, String> orderedOptions(Object... values) {
        Map<Integer, String> options = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            Integer key = (Integer) values[i];
            String label = (String) values[i + 1];
            options.put(key, label);
        }
        return options;
    }

    private void printOptions(Map<Integer, String> options) {
        for (Map.Entry<Integer, String> entry : options.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
    }
}
