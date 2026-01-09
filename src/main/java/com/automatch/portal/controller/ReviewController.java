package com.automatch.portal.controller;

import com.automatch.portal.records.ReviewRecord;
import com.automatch.portal.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/protected/review")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewRecord> createReview(@RequestBody ReviewRecord reviewRecord) {
        ReviewRecord createdReview = reviewService.save(reviewRecord);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewRecord> getReviewById(@PathVariable String id) {
        ReviewRecord review = reviewService.getById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping
    public ResponseEntity<List<ReviewRecord>> getAllReviews() {
        List<ReviewRecord> reviews = reviewService.getAll();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ReviewRecord> getReviewByLesson(@PathVariable String lessonId) {
        ReviewRecord review = reviewService.getByLesson(lessonId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<ReviewRecord>> getReviewsByInstructor(@PathVariable String instructorId) {
        List<ReviewRecord> reviews = reviewService.getByInstructor(instructorId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ReviewRecord>> getReviewsByStudent(@PathVariable String studentId) {
        List<ReviewRecord> reviews = reviewService.getByStudent(studentId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ReviewRecord>> getReviewsByRating(@PathVariable Integer rating) {
        List<ReviewRecord> reviews = reviewService.getByRating(rating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rating/greater-than/{minRating}")
    public ResponseEntity<List<ReviewRecord>> getReviewsByMinRating(@PathVariable Integer minRating) {
        List<ReviewRecord> reviews = reviewService.getByMinRating(minRating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rating/less-than/{maxRating}")
    public ResponseEntity<List<ReviewRecord>> getReviewsByMaxRating(@PathVariable Integer maxRating) {
        List<ReviewRecord> reviews = reviewService.getByMaxRating(maxRating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rating/range")
    public ResponseEntity<List<ReviewRecord>> getReviewsByRatingRange(
            @RequestParam Integer minRating,
            @RequestParam Integer maxRating) {
        List<ReviewRecord> reviews = reviewService.getByRatingRange(minRating, maxRating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/instructor/{instructorId}/stats")
    public ResponseEntity<Object> getInstructorReviewStats(@PathVariable String instructorId) {
        Object stats = reviewService.getInstructorReviewStats(instructorId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/instructor/{instructorId}/average-rating")
    public ResponseEntity<Double> getInstructorAverageRating(@PathVariable String instructorId) {
        Double averageRating = reviewService.getInstructorAverageRating(instructorId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ReviewRecord>> getRecentReviews(@RequestParam(defaultValue = "10") int limit) {
        List<ReviewRecord> reviews = reviewService.getRecentReviews(limit);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReviewRecord>> searchReviews(@RequestParam(required = false) String comment) {
        List<ReviewRecord> reviews = reviewService.searchReviews(comment);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewRecord> updateReview(@PathVariable String id, @RequestBody ReviewRecord reviewRecord) {
        ReviewRecord updatedReview = reviewService.updateReview(id, reviewRecord);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restoreReview(@PathVariable String id) {
        reviewService.restore(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists/lesson/{lessonId}")
    public ResponseEntity<Boolean> reviewExistsForLesson(@PathVariable String lessonId) {
        boolean exists = reviewService.reviewExistsForLesson(lessonId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/instructor/{instructorId}/count")
    public ResponseEntity<Integer> countReviewsByInstructor(@PathVariable String instructorId) {
        int count = reviewService.countReviewsByInstructor(instructorId);
        return ResponseEntity.ok(count);
    }
}