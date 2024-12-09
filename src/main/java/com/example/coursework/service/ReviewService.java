package com.example.coursework.service;

import com.example.coursework.entity.Review;
import com.example.coursework.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public void saveReview(Review review) {
        reviewRepository.save(review);
    }

    public List<Review> getReviewsByCourseId(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
    @Transactional
    public void deleteByCourseId(Long courseId) {
        reviewRepository.deleteByCourseId(courseId);
    }
}

