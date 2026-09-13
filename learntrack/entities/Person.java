package entities;

public class Person {
    private int id;
    private String firstName, lastName, email;

    public Person(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    // Overridden by subclasses to add role-specific context (polymorphism).
    public String getDisplayName() {
        return firstName + " " + lastName;
    }
}
