package com.automatch.portal.dao;

import com.automatch.portal.mapper.InstructorMapper;
import com.automatch.portal.model.InstructorModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InstructorDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_FIELDS = """
        i.user_id, i.hourly_rate, i.bio, i.years_experience, i.is_verified,
        i.average_rating, i.total_reviews, i.created_at, i.updated_at, i.deleted_at,
        u.id as user_id_full, u.full_name, u.email, u.phone, u.role, u.is_active,
        u.profile_image_url, u.created_at as user_created_at, u.last_loggin,
        u.updated_at as user_updated_at, u.deleted_at as user_deleted_at
    """;

    private static final String FROM_CLAUSE = """
        FROM instructors i
        JOIN users u ON i.user_id = u.id
    """;

    public InstructorModel save(InstructorModel instructor) {
        if (instructor.getUser() == null || instructor.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (existsById(instructor.getUser().getId())) {
            return update(instructor);
        } else {
            return insert(instructor);
        }
    }

    private InstructorModel insert(InstructorModel instructor) {
        String sql = """
            INSERT INTO instructors (user_id, hourly_rate, bio, years_experience, 
                                   is_verified, average_rating, total_reviews,
                                   created_at, updated_at)
            VALUES (:userId, :hourlyRate, :bio, :yearsExperience, 
                    :isVerified, :averageRating, :totalReviews,
                    :createdAt, :updatedAt)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", instructor.getUser().getId())
                .addValue("hourlyRate", instructor.getHourlyRate())
                .addValue("bio", instructor.getBio())
                .addValue("yearsExperience", instructor.getYearsExperience())
                .addValue("isVerified", instructor.getIsVerified())
                .addValue("averageRating", instructor.getAverageRating())
                .addValue("totalReviews", instructor.getTotalReviews())
                .addValue("createdAt", instructor.getCreatedAt())
                .addValue("updatedAt", instructor.getUpdatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(instructor.getUser().getId()).orElse(null);
    }

    private InstructorModel update(InstructorModel instructor) {
        String sql = """
            UPDATE instructors 
            SET hourly_rate = :hourlyRate,
                bio = :bio,
                years_experience = :yearsExperience,
                is_verified = :isVerified,
                average_rating = :averageRating,
                total_reviews = :totalReviews,
                updated_at = :updatedAt
            WHERE user_id = :userId AND deleted_at IS NULL
        """;

        instructor.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", instructor.getUser().getId())
                .addValue("hourlyRate", instructor.getHourlyRate())
                .addValue("bio", instructor.getBio())
                .addValue("yearsExperience", instructor.getYearsExperience())
                .addValue("isVerified", instructor.getIsVerified())
                .addValue("averageRating", instructor.getAverageRating())
                .addValue("totalReviews", instructor.getTotalReviews())
                .addValue("updatedAt", instructor.getUpdatedAt());

        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated > 0) {
            return findById(instructor.getUser().getId()).orElse(null);
        }
        return null;
    }

    public Optional<InstructorModel> findById(UUID userId) {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE i.user_id = ? AND i.deleted_at IS NULL";

        try {
            InstructorModel instructor = jdbcTemplate.queryForObject(sql, InstructorMapper.getRowMapper(), userId);
            return Optional.ofNullable(instructor);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

//    public List<InstructorModel> findAll() {
//        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
//                "WHERE i.deleted_at IS NULL ORDER BY u.full_name";
//
//        return jdbcTemplate.query(sql, InstructorMapper.getRowMapper());
//    }

    public List<InstructorModel> findActive() {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE i.deleted_at IS NULL AND u.is_active = true ORDER BY u.full_name";

        return jdbcTemplate.query(sql, InstructorMapper.getRowMapper());
    }

//    public List<InstructorModel> findVerified() {
//        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
//                "WHERE i.deleted_at IS NULL AND i.is_verified = true ORDER BY u.full_name";
//
//        return jdbcTemplate.query(sql, InstructorMapper.getRowMapper());
//    }

//    public List<InstructorModel> search(String name, Integer minYearsExperience, BigDecimal maxHourlyRate, BigDecimal minRating) {
//        StringBuilder sql = new StringBuilder("SELECT " + SELECT_FIELDS + FROM_CLAUSE + "WHERE i.deleted_at IS NULL");
//        MapSqlParameterSource params = new MapSqlParameterSource();
//
//        if (name != null && !name.trim().isEmpty()) {
//            sql.append(" AND LOWER(u.full_name) LIKE LOWER(:name)");
//            params.addValue("name", "%" + name + "%");
//        }
//
//        if (minYearsExperience != null) {
//            sql.append(" AND i.years_experience >= :minYearsExperience");
//            params.addValue("minYearsExperience", minYearsExperience);
//        }
//
//        if (maxHourlyRate != null) {
//            sql.append(" AND i.hourly_rate <= :maxHourlyRate");
//            params.addValue("maxHourlyRate", maxHourlyRate);
//        }
//
//        if (minRating != null) {
//            sql.append(" AND i.average_rating >= :minRating");
//            params.addValue("minRating", minRating);
//        }
//
//        sql.append(" ORDER BY i.average_rating DESC");
//
//        return namedParameterJdbcTemplate.query(sql.toString(), params, InstructorMapper.getRowMapper());
//    }

//    public List<InstructorModel> findTopRated(int limit) {
//        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
//                "WHERE i.deleted_at IS NULL AND i.total_reviews > 0 " +
//                "ORDER BY i.average_rating DESC, i.total_reviews DESC LIMIT ?";
//
//        return jdbcTemplate.query(sql, InstructorMapper.getRowMapper(), limit);
//    }

//    public List<InstructorModel> findAvailableNow() {
//        String sql = """
//            SELECT DISTINCT """ + SELECT_FIELDS + FROM_CLAUSE + """
//            WHERE i.deleted_at IS NULL
//            AND u.is_active = true
//            AND i.is_verified = true
//            AND EXISTS (
//                SELECT 1 FROM instructor_availability ia
//                WHERE ia.instructor_id = i.user_id
//                AND ia.deleted_at IS NULL
//                AND ia.day_of_week = EXTRACT(DOW FROM CURRENT_DATE)
//                AND ia.start_time <= CURRENT_TIME
//                AND ia.end_time >= CURRENT_TIME
//            )
//            ORDER BY u.full_name
//        """;
//
//        return jdbcTemplate.query(sql, InstructorMapper.getRowMapper());
//    }

    public boolean delete(UUID userId) {
        String sql = """
            UPDATE instructors 
            SET deleted_at = :deletedAt,
                updated_at = :updatedAt
            WHERE user_id = :userId AND deleted_at IS NULL
        """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("deletedAt", now)
                .addValue("updatedAt", now);

        int updated = namedParameterJdbcTemplate.update(sql, params);
        return updated > 0;
    }

    public boolean existsById(UUID userId) {
        String sql = "SELECT COUNT(*) FROM instructors WHERE user_id = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    public Object getInstructorStats(UUID userId) {
        String sql = """
            SELECT 
                COUNT(DISTINCT l.id) as total_lessons,
                COUNT(DISTINCT CASE WHEN l.completed_at IS NOT NULL THEN l.id END) as completed_lessons,
                COALESCE(SUM(CASE WHEN l.completed_at IS NOT NULL THEN l.price END), 0) as total_earnings,
                COUNT(DISTINCT v.id) as total_vehicles,
                COUNT(DISTINCT sf.id) as total_favorites
            FROM instructors i
            LEFT JOIN lessons l ON i.user_id = l.instructor_id AND l.deleted_at IS NULL
            LEFT JOIN vehicles v ON i.user_id = v.instructor_id AND v.deleted_at IS NULL
            LEFT JOIN student_favorites sf ON i.user_id = sf.instructor_id
            WHERE i.user_id = ? AND i.deleted_at IS NULL
            GROUP BY i.user_id
        """;

        try {
            return jdbcTemplate.queryForMap(sql, userId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Integer> findLicenseTypesByInstructor(UUID userId) {
        String sql = "SELECT license_type_id FROM instructor_license_types WHERE instructor_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    public boolean hasLicenseType(UUID userId, Integer licenseTypeId) {
        String sql = "SELECT COUNT(*) FROM instructor_license_types WHERE instructor_id = ? AND license_type_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, licenseTypeId);
        return count != null && count > 0;
    }

    public void addLicenseType(UUID userId, Integer licenseTypeId) {
        String sql = """
            INSERT INTO instructor_license_types (id, instructor_id, license_type_id, created_at)
            VALUES (:id, :instructorId, :licenseTypeId, :createdAt)
        """;

        UUID id = UUID.randomUUID();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("instructorId", userId)
                .addValue("licenseTypeId", licenseTypeId)
                .addValue("createdAt", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, params);
    }

    public int removeLicenseType(UUID userId, Integer licenseTypeId) {
        String sql = "DELETE FROM instructor_license_types WHERE instructor_id = ? AND license_type_id = ?";
        return jdbcTemplate.update(sql, userId, licenseTypeId);
    }

    public List<Map<String, Object>> findVehiclesByInstructor(UUID userId) {
        String sql = """
        SELECT id, license_plate, model, brand, year, is_available, is_approved
        FROM vehicles 
        WHERE instructor_id = ? AND deleted_at IS NULL
        ORDER BY is_available DESC, model
    """;

        return jdbcTemplate.queryForList(sql, userId);
    }

    public List<Map<String, Object>> findScheduleByInstructor(UUID userId) {
        String sql = """
        SELECT day_of_week, start_time, end_time
        FROM instructor_availability 
        WHERE instructor_id = ? AND deleted_at IS NULL
        ORDER BY day_of_week, start_time
    """;

        return jdbcTemplate.queryForList(sql, userId);
    }

    public List<Map<String, Object>> findReviewsByInstructor(UUID userId) {
        String sql = """
        SELECT r.rating, r.comment, r.created_at, u.full_name as student_name
        FROM reviews r
        JOIN lessons l ON r.lesson_id = l.id
        JOIN users u ON l.student_id = u.id
        WHERE l.instructor_id = ? AND r.deleted_at IS NULL
        ORDER BY r.created_at DESC
    """;

        return jdbcTemplate.queryForList(sql, userId);
    }

//    public int countAll() {
//        String sql = "SELECT COUNT(*) FROM instructors WHERE deleted_at IS NULL";
//        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
//        return count != null ? count : 0;
//    }
//
//    public int countVerified() {
//        String sql = "SELECT COUNT(*) FROM instructors WHERE deleted_at IS NULL AND is_verified = true";
//        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
//        return count != null ? count : 0;
//    }
//
//    public Object getHourlyRateStats() {
//        String sql = """
//            SELECT
//                COUNT(*) as total_instructors,
//                COALESCE(AVG(hourly_rate), 0) as average_hourly_rate,
//                COALESCE(MIN(hourly_rate), 0) as min_hourly_rate,
//                COALESCE(MAX(hourly_rate), 0) as max_hourly_rate
//            FROM instructors
//            WHERE deleted_at IS NULL
//        """;
//
//        return jdbcTemplate.queryForMap(sql);
//    }

    public List<InstructorModel> findByHourlyRateRange(BigDecimal minRate, BigDecimal maxRate) {
        StringBuilder sql = new StringBuilder("SELECT " + SELECT_FIELDS + FROM_CLAUSE + "WHERE i.deleted_at IS NULL");

        if (minRate != null) {
            sql.append(" AND i.hourly_rate >= ?");
        }

        if (maxRate != null) {
            sql.append(" AND i.hourly_rate <= ?");
        }

        sql.append(" ORDER BY i.hourly_rate");

        if (minRate != null && maxRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorMapper.getRowMapper(), minRate, maxRate);
        } else if (minRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorMapper.getRowMapper(), minRate);
        } else if (maxRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorMapper.getRowMapper(), maxRate);
        } else {
            return jdbcTemplate.query(sql.toString(), InstructorMapper.getRowMapper());
        }
    }

    public List<InstructorModel> findByExperienceRange(Integer minYears, Integer maxYears) {
        StringBuilder sql = new StringBuilder("SELECT " + SELECT_FIELDS + FROM_CLAUSE + "WHERE i.deleted_at IS NULL");

        if (minYears != null) {
            sql.append(" AND i.years_experience >= ?");
        }

        if (maxYears != null) {
            sql.append(" AND i.years_experience <= ?");
        }

        sql.append(" ORDER BY i.years_experience DESC");

        if (minYears != null && maxYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorMapper.getRowMapper(), minYears, maxYears);
        } else if (minYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorMapper.getRowMapper(), minYears);
        } else if (maxYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorMapper.getRowMapper(), maxYears);
        } else {
            return jdbcTemplate.query(sql.toString(), InstructorMapper.getRowMapper());
        }
    }
}