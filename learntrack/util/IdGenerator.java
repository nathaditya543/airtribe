package util;

/**
 * Central place for generating unique, auto-incrementing IDs.
 *
 * Demonstrates static fields/methods: each counter is a private static
 * field shared across every call, so callers never have to invent or
 * manually type IDs (and can't accidentally collide two records).
 */
public class IdGenerator {

    private static int studentIdCounter = 1;
    private static int courseIdCounter = 1;
    private static int enrollmentIdCounter = 1;

    // Utility class - no instances needed.
    private IdGenerator() { }

    public static int getNextStudentId() {
        return studentIdCounter++;
    }

    public static int getNextCourseId() {
        return courseIdCounter++;
    }

    public static int getNextEnrollmentId() {
        return enrollmentIdCounter++;
    }
}
