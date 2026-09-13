package services;

import entities.Course;
import entities.Enrollment;
import entities.Student;
import exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnrollmentService {

    private final ArrayList<Enrollment> enrollmentList = new ArrayList<>();

    public void addEnrollment(Enrollment newEnrollment) {
        enrollmentList.add(newEnrollment);
    }

    public Enrollment getEnrollment(int id) {
        for (Enrollment enrollment : enrollmentList) {
            if (enrollment.getId() == id) {
                return enrollment;
            }
        }
        throw new EntityNotFoundException("Enrollment not found with ID: " + id);
    }

    public List<Enrollment> listEnrollments() {
        return Collections.unmodifiableList(enrollmentList);
    }

    // Overload: filter enrollments down to just one student's history.
    public List<Enrollment> listEnrollments(int studentId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : enrollmentList) {
            if (enrollment.getStudentId() == studentId) {
                result.add(enrollment);
            }
        }
        return result;
    }

    // Marks an enrollment COMPLETED and links the student/course records together.
    public void completeEnrollment(Enrollment enrollment) {
        if (enrollment.getStatus() != Enrollment.Status.ACTIVE) {
            System.out.println("# Enrollment is not ACTIVE (current status: " + enrollment.getStatus() + ")");
            return;
        }

        Student student = enrollment.getStudent();
        Course course = enrollment.getCourse();
        student.addCourse(course);
        course.addStudent(student);

        enrollment.setStatus(Enrollment.Status.COMPLETED);
        System.out.println("# Enrollment marked COMPLETED");
    }

    public void cancelEnrollment(Enrollment enrollment) {
        enrollment.setStatus(Enrollment.Status.CANCELLED);
    }
}
