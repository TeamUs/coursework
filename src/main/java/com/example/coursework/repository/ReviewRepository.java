package com.example.coursework.repository;

import com.example.coursework.entity.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r ORDER BY r.date DESC")
    List<Review> findTopNByOrderByDateDesc(@Param("limit") int limit);
    List<Review> findByCourseId(Long courseId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
}


