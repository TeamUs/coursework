package com.example.coursework.service;

import com.example.coursework.entity.Course;
import com.example.coursework.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository repo;

    public List<Course> listAll(String keyword) {
        if (keyword != null) {
            return repo.search(keyword);
        }
        return repo.findAll();
    }

    public void save(Course cargo) {
        repo.save(cargo);
    }

    public Course get(Long id) {
        return repo.findById(id).get();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Course getCourseById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
    }

    public List<Course> getAllCourses() {
        return repo.findAll();
    }

    public List<Course> findCoursesByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return repo.findCoursesByDateRange(startDateTime, endDateTime);
    }

}