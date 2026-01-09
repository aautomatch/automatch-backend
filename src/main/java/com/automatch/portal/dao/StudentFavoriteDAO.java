package com.automatch.portal.dao;

import com.automatch.portal.model.StudentFavoriteModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StudentFavoriteDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StudentFavoriteModel save(StudentFavoriteModel favorite) {
        if (favorite.getId() == null) {
            return insert(favorite);
        } else {
            throw new UnsupportedOperationException("Updates not supported for favorites");
        }
    }

    private StudentFavoriteModel insert(StudentFavoriteModel favorite) {
        String sql = """
            INSERT INTO student_favorites (id, student_id, instructor_id, created_at)
            VALUES (:id, :studentId, :instructorId, :createdAt)
        """;

        UUID id = UUID.randomUUID();
        favorite.setId(id);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("studentId", favorite.getStudentId())
                .addValue("instructorId", favorite.getInstructorId())
                .addValue("createdAt", favorite.getCreatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(id).orElse(null);
    }

    public Optional<StudentFavoriteModel> findById(UUID id) {
        String sql = """
            SELECT id, student_id, instructor_id, created_at
            FROM student_favorites 
            WHERE id = ?
        """;

        try {
            StudentFavoriteModel favorite = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(favorite);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<StudentFavoriteModel> findAll() {
        String sql = """
            SELECT id, student_id, instructor_id, created_at
            FROM student_favorites 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<StudentFavoriteModel> findByStudent(UUID studentId) {
        String sql = """
            SELECT id, student_id, instructor_id, created_at
            FROM student_favorites 
            WHERE student_id = ? 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), studentId);
    }

    public List<StudentFavoriteModel> findByInstructor(UUID instructorId) {
        String sql = """
            SELECT id, student_id, instructor_id, created_at
            FROM student_favorites 
            WHERE instructor_id = ? 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), instructorId);
    }

    public List<StudentFavoriteModel> findByStudentAndInstructor(UUID studentId, UUID instructorId) {
        String sql = """
            SELECT id, student_id, instructor_id, created_at
            FROM student_favorites 
            WHERE student_id = ? AND instructor_id = ?
        """;

        return jdbcTemplate.query(sql, getRowMapper(), studentId, instructorId);
    }

    public List<StudentFavoriteModel> findRecentByStudent(UUID studentId, int limit) {
        String sql = """
            SELECT id, student_id, instructor_id, created_at
            FROM student_favorites 
            WHERE student_id = ? 
            ORDER BY created_at DESC 
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, getRowMapper(), studentId, limit);
    }

    public List<StudentFavoriteModel> findRecent(int limit) {
        String sql = """
            SELECT id, student_id, instructor_id, created_at
            FROM student_favorites 
            ORDER BY created_at DESC 
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, getRowMapper(), limit);
    }

    public boolean existsByStudentAndInstructor(UUID studentId, UUID instructorId) {
        String sql = "SELECT COUNT(*) FROM student_favorites WHERE student_id = ? AND instructor_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, instructorId);
        return count != null && count > 0;
    }

    public boolean delete(UUID id) {
        String sql = "DELETE FROM student_favorites WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        return deleted > 0;
    }

    public int deleteByStudentAndInstructor(UUID studentId, UUID instructorId) {
        String sql = "DELETE FROM student_favorites WHERE student_id = ? AND instructor_id = ?";
        return jdbcTemplate.update(sql, studentId, instructorId);
    }

    public int deleteAllByStudent(UUID studentId) {
        String sql = "DELETE FROM student_favorites WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }

    public int countByStudent(UUID studentId) {
        String sql = "SELECT COUNT(*) FROM student_favorites WHERE student_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
        return count != null ? count : 0;
    }

    public int countByInstructor(UUID instructorId) {
        String sql = "SELECT COUNT(*) FROM student_favorites WHERE instructor_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, instructorId);
        return count != null ? count : 0;
    }

    public List<UUID> findTopFavoriteInstructors(int limit) {
        String sql = """
            SELECT instructor_id, COUNT(*) as favorite_count
            FROM student_favorites
            GROUP BY instructor_id
            ORDER BY favorite_count DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> UUID.fromString(rs.getString("instructor_id")),
                limit);
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM student_favorites";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public Object getInstructorPopularityStats(UUID instructorId) {
        String sql = """
            SELECT 
                COUNT(*) as total_favorites,
                COUNT(DISTINCT student_id) as unique_students,
                MIN(created_at) as first_favorited_at,
                MAX(created_at) as last_favorited_at
            FROM student_favorites
            WHERE instructor_id = ?
            GROUP BY instructor_id
        """;

        try {
            return jdbcTemplate.queryForMap(sql, instructorId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<UUID> findStudentsWhoFavoritedInstructor(UUID instructorId) {
        String sql = "SELECT DISTINCT student_id FROM student_favorites WHERE instructor_id = ?";
        return jdbcTemplate.queryForList(sql, UUID.class, instructorId);
    }

    public List<UUID> findCommonFavoritesBetweenStudents(UUID studentId1, UUID studentId2) {
        String sql = """
            SELECT sf1.instructor_id
            FROM student_favorites sf1
            JOIN student_favorites sf2 ON sf1.instructor_id = sf2.instructor_id
            WHERE sf1.student_id = ? AND sf2.student_id = ?
        """;

        return jdbcTemplate.queryForList(sql, UUID.class, studentId1, studentId2);
    }

    private RowMapper<StudentFavoriteModel> getRowMapper() {
        return new RowMapper<StudentFavoriteModel>() {
            @Override
            public StudentFavoriteModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                StudentFavoriteModel favorite = new StudentFavoriteModel();
                favorite.setId(UUID.fromString(rs.getString("id")));

                String studentId = rs.getString("student_id");
                if (studentId != null) {
                    favorite.setStudentId(UUID.fromString(studentId));
                }

                String instructorId = rs.getString("instructor_id");
                if (instructorId != null) {
                    favorite.setInstructorId(UUID.fromString(instructorId));
                }

                favorite.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                return favorite;
            }
        };
    }
}