package ui;

import entities.Course;
import entities.Enrollment;
import entities.Student;
import exceptions.EntityNotFoundException;
import exceptions.InvalidInputException;
import services.CourseService;
import services.EnrollmentService;
import services.StudentService;
import util.IdGenerator;
import util.InputValidator;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point. Keeps itself to displaying the menu, reading input,
 * and calling into the service layer - all business logic lives in services.
 */
public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();
    private final EnrollmentService enrollmentService = new EnrollmentService();

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("\nWelcome to LearnTrack Management System");
        boolean exit = false;

        while (!exit) {
            printMenu();

            try {
                int choice = readInt("# Enter choice: ");
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewStudents();
                    case 3 -> searchStudent();
                    case 4 -> deactivateStudent();
                    case 5 -> addCourse();
                    case 6 -> viewCourses();
                    case 7 -> toggleCourse();
                    case 8 -> enrollStudent();
                    case 9 -> viewEnrollmentsForStudent();
                    case 10 -> markEnrollment();
                    case 0 -> exit = confirmQuit();
                    default -> System.out.println("# Unknown option, please choose a number from the menu.");
                }
            } catch (EntityNotFoundException | InvalidInputException e) {
                // Clean, readable message instead of a raw stack trace.
                System.out.println("# Error: " + e.getMessage());
            }
        }

        System.out.println("\n# Quitting\n# Bye! :)");
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n\n###################### MENU ######################");
        System.out.println("#  1. Add Student");
        System.out.println("#  2. View all Students");
        System.out.println("#  3. Search Student by ID");
        System.out.println("#  4. Deactivate a Student");
        System.out.println("#  5. Add Course");
        System.out.println("#  6. View all Courses");
        System.out.println("#  7. Activate/Deactivate a Course");
        System.out.println("#  8. Enroll a Student in a Course");
        System.out.println("#  9. View Enrollments for a Student");
        System.out.println("# 10. Mark Enrollment Completed/Cancelled");
        System.out.println("#  0. Exit");
    }

    // ---------- Students ----------

    private void addStudent() {
        System.out.println("\n################# Add Student #################");
        String firstName = readNonBlank("# First Name: ");
        String lastName = readNonBlank("# Last Name: ");
        String email = readNonBlank("# Email: ");
        if (!InputValidator.isValidEmail(email)) {
            throw new InvalidInputException("Email looks invalid: " + email);
        }
        String batch = readNonBlank("# Batch: ");

        Student student = new Student(IdGenerator.getNextStudentId(), firstName, lastName, email, batch);
        studentService.addStudent(student);

        System.out.println("# Student added successfully with ID " + student.getId());
    }

    private void viewStudents() {
        System.out.println("\n################# Student List #################");
        List<Student> students = studentService.listStudents();
        if (students.isEmpty()) {
            System.out.println("# No students yet.");
            return;
        }

        for (Student student : students) {
            printStudent(student);
        }
    }

    private void searchStudent() {
        int id = readInt("# Enter Student ID: ");
        Student student = studentService.getStudent(id);
        printStudent(student);
    }

    private void deactivateStudent() {
        int id = readInt("# Enter Student ID to deactivate: ");
        studentService.toggleStudentActive(id);
        System.out.println("# Student " + id + " active status toggled.");
    }

    private void printStudent(Student student) {
        System.out.println(student.getDisplayName());
        System.out.println("# ID: " + student.getId());
        System.out.println("# Email: " + student.getEmail());
        System.out.println("# Active: " + student.isActive());
        System.out.println("# Completed courses: ");
        for (Course course : student.getCourses()) {
            System.out.println("#\t" + course.getCourseName());
        }
        System.out.println();
    }

    // ---------- Courses ----------

    private void addCourse() {
        System.out.println("\n################# Add Course #################");
        String courseName = readNonBlank("# Course Name: ");
        String description = readNonBlank("# Course Description: ");
        int duration = readPositiveInt("# Duration in weeks: ");

        Course course = new Course(IdGenerator.getNextCourseId(), courseName, description, duration);
        courseService.addCourse(course);

        System.out.println("# Course added successfully with ID " + course.getId());
    }

    private void viewCourses() {
        System.out.println("\n################# Course List #################");
        List<Course> courses = courseService.listCourses();
        if (courses.isEmpty()) {
            System.out.println("# No courses yet.");
            return;
        }

        for (Course course : courses) {
            System.out.println("# ID: " + course.getId()
                    + " | " + course.getCourseName()
                    + " (" + course.getDurationInWeeks() + " weeks)"
                    + " | Active: " + course.isActive());
        }
    }

    private void toggleCourse() {
        int id = readInt("# Enter Course ID: ");
        courseService.toggleCourseActive(id);
        System.out.println("# Course " + id + " active status toggled.");
    }

    // ---------- Enrollments ----------

    private void enrollStudent() {
        System.out.println("\n############### Enroll Student ################");
        int studentId = readInt("# Student ID: ");
        Student student = studentService.getStudent(studentId);

        int courseId = readInt("# Course ID: ");
        Course course = courseService.getCourse(courseId);

        Enrollment enrollment = new Enrollment(IdGenerator.getNextEnrollmentId(), student, course);
        enrollmentService.addEnrollment(enrollment);

        System.out.println("# Enrollment created with ID " + enrollment.getId());
    }

    private void viewEnrollmentsForStudent() {
        int studentId = readInt("# Student ID: ");
        studentService.getStudent(studentId); // validates the student exists
        List<Enrollment> enrollments = enrollmentService.listEnrollments(studentId);

        if (enrollments.isEmpty()) {
            System.out.println("# No enrollments for this student.");
            return;
        }

        for (Enrollment enrollment : enrollments) {
            System.out.println("# Enrollment " + enrollment.getId()
                    + " | Course: " + enrollment.getCourse().getCourseName()
                    + " | Status: " + enrollment.getStatus()
                    + " | Since: " + enrollment.getEnrollmentDate());
        }
    }

    private void markEnrollment() {
        int id = readInt("# Enrollment ID: ");
        Enrollment enrollment = enrollmentService.getEnrollment(id);

        System.out.println("# 1: Mark Completed   2: Cancel");
        int action = readInt("# Choice: ");

        if (action == 1) {
            enrollmentService.completeEnrollment(enrollment);
        } else if (action == 2) {
            enrollmentService.cancelEnrollment(enrollment);
            System.out.println("# Enrollment cancelled.");
        } else {
            System.out.println("# Unknown option.");
        }
    }

    // ---------- Input helpers ----------

    private boolean confirmQuit() {
        System.out.println("\n# Do you want to quit? 1: Yes  0: No");
        return readInt("# Choice: ") == 1;
    }

    private String readNonBlank(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine();
        InputValidator.requireNonBlank(value, "This field");
        return value;
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int value = scanner.nextInt();
            scanner.nextLine();
            return value;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // discard the bad token so the loop doesn't spin forever
            throw new InvalidInputException("Please enter a whole number.");
        }
    }

    private int readPositiveInt(String prompt) {
        int value = readInt(prompt);
        InputValidator.requirePositive(value, "Value");
        return value;
    }
}
