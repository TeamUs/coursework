package com.example.coursework.repository;

import com.example.coursework.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT p FROM Course p WHERE CONCAT(p.courseName, ' ', p.courseDescription, ' ', p.teacher) LIKE %?1%")
    List<Course> search(String searchTerm);
    @Query("SELECT c FROM Course c WHERE c.startDateTime >= :startDateTime AND c.endDateTime <= :endDateTime")
    List<Course> findCoursesByDateRange(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

}
