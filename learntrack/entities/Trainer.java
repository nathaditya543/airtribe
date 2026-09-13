package entities;

public class Trainer extends Person {
    private String[] courses;

    public Trainer(int id, String firstName, String lastName, String email, String[] courses) {
        super(id, firstName, lastName, email);
        this.courses = courses;
    }

    public String[] getCourses() {
        return courses;
    }

    @Override
    public String getDisplayName() {
        return "Trainer: " + super.getDisplayName();
    }
}
