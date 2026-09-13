package entities;

import java.util.ArrayList;

public class Student extends Person {
    private String batch;
    private boolean active;
    private ArrayList<Course> courseList = new ArrayList<>();

    // Constructor overloading #1: no batch given yet -> defaults to "UNASSIGNED", active by default.
    public Student(int id, String firstName, String lastName, String email) {
        this(id, firstName, lastName, email, "UNASSIGNED");
    }

    // Constructor overloading #2: batch known up front, active by default.
    public Student(int id, String firstName, String lastName, String email, String batch) {
        super(id, firstName, lastName, email);
        this.batch = batch;
        this.active = true;
    }

    // Constructor overloading #3: full control, e.g. re-creating an inactive student.
    public Student(int id, String firstName, String lastName, String email, String batch, boolean active) {
        super(id, firstName, lastName, email);
        this.batch = batch;
        this.active = active;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getDisplayName() {
        return "Student: " + super.getDisplayName() + " (Batch " + batch + ")";
    }

    public void addCourse(Course course) {
        courseList.add(course);
    }

    public ArrayList<Course> getCourses() {
        return courseList;
    }
}
