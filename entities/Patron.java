package entities;

import java.util.ArrayList;
import java.util.List;

public class Patron {
    private final int id;
    private String name;
    private String email;
    private final List<String> history;

    public Patron(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.history = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addGenreToHistory(String genre) {
        if (genre != null && !genre.isBlank()) {
            history.add(genre);
        }
    }

    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public String toString() {
        return "Patron{id=" + id + ", name='" + name + "', email='" + email + "', history=" + history + "}";
    }
}
