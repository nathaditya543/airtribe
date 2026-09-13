package services;

import entities.Student;
import exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentService {

    private final ArrayList<Student> studentList = new ArrayList<>();

    public void addStudent(Student newStudent) {
        studentList.add(newStudent);
    }

    public Student getStudent(int id) {
        for (Student student : studentList) {
            if (student.getId() == id) {
                return student;
            }
        }
        throw new EntityNotFoundException("Student not found with ID: " + id);
    }

    // Overload: allow searching case-insensitively by email as an alternative to ID.
    public Student getStudent(String email) {
        for (Student student : studentList) {
            if (student.getEmail().equalsIgnoreCase(email)) {
                return student;
            }
        }
        throw new EntityNotFoundException("Student not found with email: " + email);
    }

    public void updateStudent(int id, String firstName, String lastName, String email, String batch) {
        Student student = getStudent(id);
        // firstName/lastName/email live on Person and are read-only by design;
        // batch is the field students are actually expected to move between.
        student.setBatch(batch);
    }

    public void removeStudent(int id) {
        Student student = getStudent(id);
        studentList.remove(student);
    }

    public void toggleStudentActive(int id) {
        Student student = getStudent(id);
        student.setActive(!student.isActive());
    }

    public List<Student> listStudents() {
        return Collections.unmodifiableList(studentList);
    }
}
