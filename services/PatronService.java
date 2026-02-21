package services;

import entities.Patron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatronService {
    private final Map<Integer, Patron> patronsById = new HashMap<>();
    private int patronCounter = 1;

    public Patron createPatron(String name, String email) {
        Patron patron = new Patron(patronCounter++, name, email);
        patronsById.put(patron.getId(), patron);
        return patron;
    }

    public Patron getPatronById(int patronId) {
        Patron patron = patronsById.get(patronId);
        if (patron == null) {
            throw new IllegalArgumentException("Patron not found: " + patronId);
        }
        return patron;
    }

    public Patron updatePatron(int patronId, String name, String email) {
        Patron patron = getPatronById(patronId);
        patron.setName(name);
        patron.setEmail(email);
        return patron;
    }

    public void removePatron(int patronId) {
        if (patronsById.remove(patronId) == null) {
            throw new IllegalArgumentException("Patron not found: " + patronId);
        }
    }

    public List<Patron> getAllPatrons() {
        return new ArrayList<>(patronsById.values());
    }
}
