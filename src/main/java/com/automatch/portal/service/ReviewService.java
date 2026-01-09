package com.automatch.portal.service;

import com.automatch.portal.dao.ReviewDAO;
import com.automatch.portal.dao.LessonDAO;
import com.automatch.portal.mapper.ReviewMapper;
import com.automatch.portal.model.ReviewModel;
import com.automatch.portal.records.ReviewRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final LessonDAO lessonDAO;

    @Transactional
    public ReviewRecord save(ReviewRecord reviewRecord) {
        validateReviewRecord(reviewRecord);

        ReviewModel reviewModel = ReviewMapper.fromRecord(reviewRecord);

        if (reviewModel.getId() == null) {
            return createReview(reviewModel);
        } else {
            return updateReview(reviewRecord.id(), reviewRecord);
        }
    }

    private ReviewRecord createReview(ReviewModel reviewModel) {
        UUID lessonId = reviewModel.getLessonId();

        // Verificar se a aula existe
        var lesson = lessonDAO.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));

        // Verificar se a aula já foi concluída
        if (lesson.getCompletedAt() == null) {
            throw new IllegalArgumentException("Cannot review a lesson that hasn't been completed");
        }

        // Verificar se já existe uma avaliação para esta aula
        if (reviewDAO.existsByLesson(lessonId)) {
            throw new IllegalArgumentException("Review already exists for this lesson");
        }

        // NOTA: A validação de se o aluno que está avaliando é o mesmo da aula
        // deve ser feita no controller ou em outro serviço, pois não temos
        // o studentId no ReviewRecord. Essa validação pode ser feita:
        // 1. Verificando o token JWT para obter o usuário autenticado
        // 2. Comparando com o studentId da aula
        // Por enquanto, vamos remover essa validação

        reviewModel.setCreatedAt(LocalDateTime.now());
        reviewModel.setUpdatedAt(LocalDateTime.now());

        ReviewModel savedModel = reviewDAO.save(reviewModel);

        // Atualizar a média de avaliações do instrutor
        updateInstructorAverageRating(lesson.getInstructorId());

        return ReviewMapper.toRecord(savedModel);
    }

    public ReviewRecord getById(String id) {
        UUID uuid = UUID.fromString(id);
        return reviewDAO.findById(uuid)
                .map(ReviewMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
    }

    public List<ReviewRecord> getAll() {
        return reviewDAO.findAll().stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    public ReviewRecord getByLesson(String lessonId) {
        UUID lessonUuid = UUID.fromString(lessonId);
        return reviewDAO.findByLesson(lessonUuid)
                .map(ReviewMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Review not found for lesson ID: " + lessonId));
    }

    public List<ReviewRecord> getByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return reviewDAO.findByInstructor(instructorUuid).stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<ReviewRecord> getByStudent(String studentId) {
        UUID studentUuid = UUID.fromString(studentId);
        return reviewDAO.findByStudent(studentUuid).stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<ReviewRecord> getByRating(Integer rating) {
        validateRating(rating);
        return reviewDAO.findByRating(rating).stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<ReviewRecord> getByMinRating(Integer minRating) {
        validateRating(minRating);
        return reviewDAO.findByMinRating(minRating).stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<ReviewRecord> getByMaxRating(Integer maxRating) {
        validateRating(maxRating);
        return reviewDAO.findByMaxRating(maxRating).stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<ReviewRecord> getByRatingRange(Integer minRating, Integer maxRating) {
        validateRating(minRating);
        validateRating(maxRating);

        if (minRating > maxRating) {
            throw new IllegalArgumentException("Minimum rating cannot be greater than maximum rating");
        }

        return reviewDAO.findByRatingRange(minRating, maxRating).stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Object getInstructorReviewStats(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return reviewDAO.getInstructorReviewStats(instructorUuid);
    }

    public Double getInstructorAverageRating(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return reviewDAO.getInstructorAverageRating(instructorUuid);
    }

    public List<ReviewRecord> getRecentReviews(int limit) {
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        return reviewDAO.findRecent(limit).stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<ReviewRecord> searchReviews(String comment) {
        return reviewDAO.searchByComment(comment).stream()
                .map(ReviewMapper::toRecord)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        ReviewModel review = reviewDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));

        if (review.getDeletedAt() != null) {
            throw new IllegalArgumentException("Review is already deleted");
        }

        boolean deleted = reviewDAO.delete(uuid);
        if (!deleted) {
            throw new RuntimeException("Failed to delete review with ID: " + id);
        }
    }

    @Transactional
    public void restore(String id) {
        UUID uuid = UUID.fromString(id);
        ReviewModel review = reviewDAO.findByIdWithDeleted(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));

        if (review.getDeletedAt() == null) {
            throw new IllegalArgumentException("Review is not deleted");
        }

        boolean restored = reviewDAO.restore(uuid);
        if (!restored) {
            throw new RuntimeException("Failed to restore review with ID: " + id);
        }
    }

    @Transactional
    public ReviewRecord updateReview(String id, ReviewRecord reviewRecord) {
        validateReviewRecord(reviewRecord);

        UUID uuid = UUID.fromString(id);
        ReviewModel existingReview = reviewDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));

        if (existingReview.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted review");
        }

        // Não permitir alterar a aula associada
        if (!existingReview.getLessonId().toString().equals(reviewRecord.lessonId())) {
            throw new IllegalArgumentException("Cannot change the lesson associated with a review");
        }

        ReviewModel updatedModel = ReviewMapper.fromRecord(reviewRecord);
        updatedModel.setId(uuid);
        updatedModel.setCreatedAt(existingReview.getCreatedAt());
        updatedModel.setUpdatedAt(LocalDateTime.now());
        updatedModel.setLessonId(existingReview.getLessonId());

        ReviewModel savedModel = reviewDAO.save(updatedModel);

        // Atualizar a média de avaliações do instrutor
        var lesson = lessonDAO.findById(existingReview.getLessonId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
        updateInstructorAverageRating(lesson.getInstructorId());

        return ReviewMapper.toRecord(savedModel);
    }

    public boolean reviewExistsForLesson(String lessonId) {
        UUID lessonUuid = UUID.fromString(lessonId);
        return reviewDAO.existsByLesson(lessonUuid);
    }

    public int countReviewsByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return reviewDAO.countByInstructor(instructorUuid);
    }

    public int countAllReviews() {
        return reviewDAO.countAll();
    }

    private void updateInstructorAverageRating(UUID instructorId) {
        Double averageRating = reviewDAO.getInstructorAverageRating(instructorId);
        reviewDAO.updateInstructorAverageRating(instructorId, averageRating);
    }

    private void validateReviewRecord(ReviewRecord reviewRecord) {
        if (reviewRecord == null) {
            throw new IllegalArgumentException("Review record cannot be null");
        }

        if (reviewRecord.lessonId() == null || reviewRecord.lessonId().trim().isEmpty()) {
            throw new IllegalArgumentException("Lesson ID is required");
        }

        if (reviewRecord.rating() == null) {
            throw new IllegalArgumentException("Rating is required");
        }

        validateRating(reviewRecord.rating());

        if (reviewRecord.comment() != null && reviewRecord.comment().length() > 1000) {
            throw new IllegalArgumentException("Comment cannot exceed 1000 characters");
        }
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}