package entities;

import java.time.LocalDate;

public class Enrollment {

    public enum Status {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    private int id;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private Status status;

    public Enrollment(int id, Student student, Course course) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.enrollmentDate = LocalDate.now();
        this.status = Status.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    // Convenience accessor matching the studentId field the spec describes,
    // without losing the richer object reference used elsewhere in the app.
    public int getStudentId() {
        return student.getId();
    }

    public Course getCourse() {
        return course;
    }

    public int getCourseId() {
        return course.getId();
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
