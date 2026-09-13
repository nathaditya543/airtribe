package services;

import entities.Course;
import exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseService {

    private final ArrayList<Course> courseList = new ArrayList<>();

    public void addCourse(Course newCourse) {
        courseList.add(newCourse);
    }

    public Course getCourse(int id) {
        for (Course course : courseList) {
            if (course.getId() == id) {
                return course;
            }
        }
        throw new EntityNotFoundException("Course not found with ID: " + id);
    }

    // Overload: look a course up by (case-insensitive) name instead of ID.
    public Course getCourse(String courseName) {
        for (Course course : courseList) {
            if (course.getCourseName().equalsIgnoreCase(courseName)) {
                return course;
            }
        }
        throw new EntityNotFoundException("Course not found with name: " + courseName);
    }

    public void updateCourse(int id, String description, int durationInWeeks) {
        Course course = getCourse(id);
        course.setDescription(description);
        course.setDurationInWeeks(durationInWeeks);
    }

    public void removeCourse(int id) {
        Course course = getCourse(id);
        courseList.remove(course);
    }

    public void toggleCourseActive(int id) {
        Course course = getCourse(id);
        course.setActive(!course.isActive());
    }

    public List<Course> listCourses() {
        return Collections.unmodifiableList(courseList);
    }
}
