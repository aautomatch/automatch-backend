package com.automatch.portal.dao;

import com.automatch.portal.model.ReviewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReviewModel save(ReviewModel review) {
        if (review.getId() == null) {
            return insert(review);
        } else {
            return update(review);
        }
    }

    private ReviewModel insert(ReviewModel review) {
        String sql = """
            INSERT INTO reviews (id, lesson_id, rating, comment, created_at, updated_at)
            VALUES (:id, :lessonId, :rating, :comment, :createdAt, :updatedAt)
        """;

        UUID id = UUID.randomUUID();
        review.setId(id);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("lessonId", review.getLessonId())
                .addValue("rating", review.getRating())
                .addValue("comment", review.getComment())
                .addValue("createdAt", review.getCreatedAt())
                .addValue("updatedAt", review.getUpdatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(id).orElse(null);
    }

    private ReviewModel update(ReviewModel review) {
        String sql = """
            UPDATE reviews 
            SET rating = :rating,
                comment = :comment,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NULL
        """;

        review.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", review.getId())
                .addValue("rating", review.getRating())
                .addValue("comment", review.getComment())
                .addValue("updatedAt", review.getUpdatedAt());

        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated > 0) {
            return findById(review.getId()).orElse(null);
        }
        return null;
    }

    public Optional<ReviewModel> findById(UUID id) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE id = ? AND deleted_at IS NULL
        """;

        try {
            ReviewModel review = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(review);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<ReviewModel> findByIdWithDeleted(UUID id) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE id = ?
        """;

        try {
            ReviewModel review = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(review);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ReviewModel> findAll() {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE deleted_at IS NULL ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public Optional<ReviewModel> findByLesson(UUID lessonId) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE lesson_id = ? AND deleted_at IS NULL
        """;

        try {
            ReviewModel review = jdbcTemplate.queryForObject(sql, getRowMapper(), lessonId);
            return Optional.ofNullable(review);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ReviewModel> findByInstructor(UUID instructorId) {
        String sql = """
            SELECT r.id, r.lesson_id, r.rating, r.comment, r.created_at, r.updated_at, r.deleted_at
            FROM reviews r
            JOIN lessons l ON r.lesson_id = l.id
            WHERE l.instructor_id = ? AND r.deleted_at IS NULL 
            ORDER BY r.created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), instructorId);
    }

    public List<ReviewModel> findByStudent(UUID studentId) {
        String sql = """
            SELECT r.id, r.lesson_id, r.rating, r.comment, r.created_at, r.updated_at, r.deleted_at
            FROM reviews r
            JOIN lessons l ON r.lesson_id = l.id
            WHERE l.student_id = ? AND r.deleted_at IS NULL 
            ORDER BY r.created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), studentId);
    }

    public List<ReviewModel> findByRating(Integer rating) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE rating = ? AND deleted_at IS NULL 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), rating);
    }

    public List<ReviewModel> findByMinRating(Integer minRating) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE rating >= ? AND deleted_at IS NULL 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), minRating);
    }

    public List<ReviewModel> findByMaxRating(Integer maxRating) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE rating <= ? AND deleted_at IS NULL 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), maxRating);
    }

    public List<ReviewModel> findByRatingRange(Integer minRating, Integer maxRating) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE rating BETWEEN ? AND ? AND deleted_at IS NULL 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), minRating, maxRating);
    }

    public List<ReviewModel> findRecent(int limit) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE deleted_at IS NULL 
            ORDER BY created_at DESC LIMIT ?
        """;

        return jdbcTemplate.query(sql, getRowMapper(), limit);
    }

    public List<ReviewModel> searchByComment(String comment) {
        String sql = """
            SELECT id, lesson_id, rating, comment, created_at, updated_at, deleted_at
            FROM reviews 
            WHERE deleted_at IS NULL AND LOWER(comment) LIKE LOWER(?) 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), "%" + comment + "%");
    }

    public boolean delete(UUID id) {
        String sql = """
            UPDATE reviews 
            SET deleted_at = :deletedAt,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NULL
        """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("deletedAt", now)
                .addValue("updatedAt", now);

        int updated = namedParameterJdbcTemplate.update(sql, params);
        return updated > 0;
    }

    public boolean restore(UUID id) {
        String sql = """
            UPDATE reviews 
            SET deleted_at = NULL,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NOT NULL
        """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("updatedAt", now);

        int updated = namedParameterJdbcTemplate.update(sql, params);
        return updated > 0;
    }

    public boolean existsByLesson(UUID lessonId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE lesson_id = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, lessonId);
        return count != null && count > 0;
    }

    public Map<String, Object> getInstructorReviewStats(UUID instructorId) {
        String sql = """
            SELECT 
                COUNT(*) as total_reviews,
                COALESCE(AVG(r.rating), 0) as average_rating,
                COUNT(CASE WHEN r.rating = 5 THEN 1 END) as five_star,
                COUNT(CASE WHEN r.rating = 4 THEN 1 END) as four_star,
                COUNT(CASE WHEN r.rating = 3 THEN 1 END) as three_star,
                COUNT(CASE WHEN r.rating = 2 THEN 1 END) as two_star,
                COUNT(CASE WHEN r.rating = 1 THEN 1 END) as one_star
            FROM reviews r
            JOIN lessons l ON r.lesson_id = l.id
            WHERE l.instructor_id = ? AND r.deleted_at IS NULL
        """;

        return jdbcTemplate.queryForMap(sql, instructorId);
    }

    public Double getInstructorAverageRating(UUID instructorId) {
        String sql = """
            SELECT COALESCE(AVG(r.rating), 0)
            FROM reviews r
            JOIN lessons l ON r.lesson_id = l.id
            WHERE l.instructor_id = ? AND r.deleted_at IS NULL
        """;

        Double average = jdbcTemplate.queryForObject(sql, Double.class, instructorId);
        return average != null ? average : 0.0;
    }

    public int countByInstructor(UUID instructorId) {
        String sql = """
            SELECT COUNT(*)
            FROM reviews r
            JOIN lessons l ON r.lesson_id = l.id
            WHERE l.instructor_id = ? AND r.deleted_at IS NULL
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, instructorId);
        return count != null ? count : 0;
    }

    public int countByStudent(UUID studentId) {
        String sql = """
            SELECT COUNT(*)
            FROM reviews r
            JOIN lessons l ON r.lesson_id = l.id
            WHERE l.student_id = ? AND r.deleted_at IS NULL
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
        return count != null ? count : 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM reviews WHERE deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public void updateInstructorAverageRating(UUID instructorId, Double averageRating) {
        String sql = """
            UPDATE instructors 
            SET average_rating = :averageRating,
                total_reviews = (SELECT COUNT(*) FROM reviews r 
                                 JOIN lessons l ON r.lesson_id = l.id 
                                 WHERE l.instructor_id = :instructorId AND r.deleted_at IS NULL),
                updated_at = :updatedAt
            WHERE user_id = :instructorId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("instructorId", instructorId)
                .addValue("averageRating", averageRating)
                .addValue("updatedAt", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, params);
    }

    private RowMapper<ReviewModel> getRowMapper() {
        return new RowMapper<ReviewModel>() {
            @Override
            public ReviewModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReviewModel review = new ReviewModel();

                // IDs
                review.setId(UUID.fromString(rs.getString("id")));
                review.setLessonId(UUID.fromString(rs.getString("lesson_id")));

                // Dados da avaliação
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));

                // Timestamps
                review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                review.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                java.sql.Timestamp deletedTimestamp = rs.getTimestamp("deleted_at");
                if (deletedTimestamp != null) {
                    review.setDeletedAt(deletedTimestamp.toLocalDateTime());
                }

                return review;
            }
        };
    }
}