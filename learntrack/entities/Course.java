package entities;

import java.util.ArrayList;

public class Course {
    private int id;
    private String courseName, description;
    private int durationInWeeks;
    private boolean active;
    private ArrayList<Student> studentList = new ArrayList<>();

    // Default constructor - fields filled in later via setters.
    public Course() {
        this.active = true;
    }

    public Course(int id, String courseName, String description, int durationInWeeks) {
        this.id = id;
        this.courseName = courseName;
        this.description = description;
        this.durationInWeeks = durationInWeeks;
        this.active = true;
    }

    // Overload: create a course with an explicit active flag (e.g. loading archived data).
    public Course(int id, String courseName, String description, int durationInWeeks, boolean active) {
        this.id = id;
        this.courseName = courseName;
        this.description = description;
        this.durationInWeeks = durationInWeeks;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationInWeeks() {
        return durationInWeeks;
    }

    public void setDurationInWeeks(int durationInWeeks) {
        this.durationInWeeks = durationInWeeks;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addStudent(Student student) {
        studentList.add(student);
    }

    public ArrayList<Student> getStudents() {
        return studentList;
    }
}
