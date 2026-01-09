package com.automatch.portal.dao;

import com.automatch.portal.model.LessonModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
public class LessonDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_FIELDS = """
        id, instructor_id, student_id, vehicle_id, scheduled_at, duration_minutes,
        status_id, address_id, price, payment_status_id, payment_method_id,
        created_at, updated_at, completed_at, deleted_at
    """;

    public LessonModel save(LessonModel lesson) {
        if (lesson.getId() == null) {
            return insert(lesson);
        } else {
            return update(lesson);
        }
    }

    private LessonModel insert(LessonModel lesson) {
        String sql = """
            INSERT INTO lessons (id, instructor_id, student_id, vehicle_id, scheduled_at, 
                                 duration_minutes, status_id, address_id, price, 
                                 payment_status_id, payment_method_id, created_at, updated_at)
            VALUES (:id, :instructorId, :studentId, :vehicleId, :scheduledAt, 
                    :durationMinutes, :statusId, :addressId, :price, 
                    :paymentStatusId, :paymentMethodId, :createdAt, :updatedAt)
        """;

        UUID id = UUID.randomUUID();
        lesson.setId(id);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("instructorId", lesson.getInstructorId())
                .addValue("studentId", lesson.getStudentId())
                .addValue("vehicleId", lesson.getVehicleId())
                .addValue("scheduledAt", lesson.getScheduledAt())
                .addValue("durationMinutes", lesson.getDurationMinutes())
                .addValue("statusId", lesson.getStatusId())
                .addValue("addressId", lesson.getAddressId())
                .addValue("price", lesson.getPrice())
                .addValue("paymentStatusId", lesson.getPaymentStatusId())
                .addValue("paymentMethodId", lesson.getPaymentMethodId())
                .addValue("createdAt", lesson.getCreatedAt())
                .addValue("updatedAt", lesson.getUpdatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(id).orElse(null);
    }

    private LessonModel update(LessonModel lesson) {
        String sql = """
            UPDATE lessons 
            SET instructor_id = :instructorId,
                student_id = :studentId,
                vehicle_id = :vehicleId,
                scheduled_at = :scheduledAt,
                duration_minutes = :durationMinutes,
                status_id = :statusId,
                address_id = :addressId,
                price = :price,
                payment_status_id = :paymentStatusId,
                payment_method_id = :paymentMethodId,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NULL
        """;

        lesson.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", lesson.getId())
                .addValue("instructorId", lesson.getInstructorId())
                .addValue("studentId", lesson.getStudentId())
                .addValue("vehicleId", lesson.getVehicleId())
                .addValue("scheduledAt", lesson.getScheduledAt())
                .addValue("durationMinutes", lesson.getDurationMinutes())
                .addValue("statusId", lesson.getStatusId())
                .addValue("addressId", lesson.getAddressId())
                .addValue("price", lesson.getPrice())
                .addValue("paymentStatusId", lesson.getPaymentStatusId())
                .addValue("paymentMethodId", lesson.getPaymentMethodId())
                .addValue("updatedAt", lesson.getUpdatedAt());

        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated > 0) {
            return findById(lesson.getId()).orElse(null);
        }
        return null;
    }

    public Optional<LessonModel> findById(UUID id) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE id = ? AND deleted_at IS NULL";

        try {
            LessonModel lesson = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(lesson);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<LessonModel> findAll() {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE deleted_at IS NULL";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<LessonModel> findByInstructor(UUID instructorId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE instructor_id = ? AND deleted_at IS NULL";
        return jdbcTemplate.query(sql, getRowMapper(), instructorId);
    }

    public List<LessonModel> findByStudent(UUID studentId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE student_id = ? AND deleted_at IS NULL";
        return jdbcTemplate.query(sql, getRowMapper(), studentId);
    }

    public List<LessonModel> findByVehicle(UUID vehicleId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE vehicle_id = ? AND deleted_at IS NULL";
        return jdbcTemplate.query(sql, getRowMapper(), vehicleId);
    }

    public List<LessonModel> findByStatus(Integer statusId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE status_id = ? AND deleted_at IS NULL";
        return jdbcTemplate.query(sql, getRowMapper(), statusId);
    }

    public List<LessonModel> findByPaymentStatus(Integer paymentStatusId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE payment_status_id = ? AND deleted_at IS NULL";
        return jdbcTemplate.query(sql, getRowMapper(), paymentStatusId);
    }

    public List<LessonModel> findUpcoming() {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE deleted_at IS NULL AND scheduled_at > NOW() AND completed_at IS NULL";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<LessonModel> findCompleted() {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE deleted_at IS NULL AND completed_at IS NOT NULL";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<LessonModel> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE deleted_at IS NULL AND scheduled_at BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, getRowMapper(), startDate, endDate);
    }

    public List<LessonModel> findInstructorLessonsByDate(UUID instructorId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE instructor_id = ? AND deleted_at IS NULL AND scheduled_at BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, getRowMapper(), instructorId, startDate, endDate);
    }

    public List<LessonModel> findStudentLessonsByDate(UUID studentId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE student_id = ? AND deleted_at IS NULL AND scheduled_at BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, getRowMapper(), studentId, startDate, endDate);
    }

    public List<LessonModel> findInstructorPendingPayment(UUID instructorId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM lessons WHERE instructor_id = ? AND deleted_at IS NULL AND payment_status_id IN (1, 2)";
        return jdbcTemplate.query(sql, getRowMapper(), instructorId);
    }

    public boolean delete(UUID id) {
        String sql = """
            UPDATE lessons 
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

    public boolean hasScheduleConflict(UUID instructorId, LocalDateTime startTime, LocalDateTime endTime, UUID excludeLessonId) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*) FROM lessons 
        WHERE instructor_id = :instructorId 
        AND deleted_at IS NULL 
        AND completed_at IS NULL
        AND ((scheduled_at < :endTime AND (scheduled_at + (duration_minutes || ' minutes')::interval) > :startTime))
    """);

        if (excludeLessonId != null) {
            sql.append(" AND id != :excludeLessonId");
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("instructorId", instructorId)
                .addValue("startTime", startTime)
                .addValue("endTime", endTime);

        if (excludeLessonId != null) {
            params.addValue("excludeLessonId", excludeLessonId);
        }

        Integer count = namedParameterJdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
        return count != null && count > 0;
    }

    public Map<String, Object> getInstructorStats(UUID instructorId) {
        Map<String, Object> stats = new HashMap<>();

        // Total de aulas
        String totalSql = "SELECT COUNT(*) FROM lessons WHERE instructor_id = ? AND deleted_at IS NULL";
        Integer totalLessons = jdbcTemplate.queryForObject(totalSql, Integer.class, instructorId);
        stats.put("totalLessons", totalLessons != null ? totalLessons : 0);

        // Aulas completadas
        String completedSql = "SELECT COUNT(*) FROM lessons WHERE instructor_id = ? AND deleted_at IS NULL AND completed_at IS NOT NULL";
        Integer completedLessons = jdbcTemplate.queryForObject(completedSql, Integer.class, instructorId);
        stats.put("completedLessons", completedLessons != null ? completedLessons : 0);

        // Aulas agendadas
        String scheduledSql = "SELECT COUNT(*) FROM lessons WHERE instructor_id = ? AND deleted_at IS NULL AND completed_at IS NULL AND scheduled_at > NOW()";
        Integer scheduledLessons = jdbcTemplate.queryForObject(scheduledSql, Integer.class, instructorId);
        stats.put("scheduledLessons", scheduledLessons != null ? scheduledLessons : 0);

        // Aulas canceladas
        String cancelledSql = "SELECT COUNT(*) FROM lessons WHERE instructor_id = ? AND deleted_at IS NULL AND status_id = 3"; // Supondo que 3 é o status de cancelado
        Integer cancelledLessons = jdbcTemplate.queryForObject(cancelledSql, Integer.class, instructorId);
        stats.put("cancelledLessons", cancelledLessons != null ? cancelledLessons : 0);

        return stats;
    }

    public Map<String, Object> getStudentStats(UUID studentId) {
        Map<String, Object> stats = new HashMap<>();

        // Total de aulas
        String totalSql = "SELECT COUNT(*) FROM lessons WHERE student_id = ? AND deleted_at IS NULL";
        Integer totalLessons = jdbcTemplate.queryForObject(totalSql, Integer.class, studentId);
        stats.put("totalLessons", totalLessons != null ? totalLessons : 0);

        // Aulas completadas
        String completedSql = "SELECT COUNT(*) FROM lessons WHERE student_id = ? AND deleted_at IS NULL AND completed_at IS NOT NULL";
        Integer completedLessons = jdbcTemplate.queryForObject(completedSql, Integer.class, studentId);
        stats.put("completedLessons", completedLessons != null ? completedLessons : 0);

        // Aulas agendadas
        String scheduledSql = "SELECT COUNT(*) FROM lessons WHERE student_id = ? AND deleted_at IS NULL AND completed_at IS NULL AND scheduled_at > NOW()";
        Integer scheduledLessons = jdbcTemplate.queryForObject(scheduledSql, Integer.class, studentId);
        stats.put("scheduledLessons", scheduledLessons != null ? scheduledLessons : 0);

        // Horas totais de aula
        String hoursSql = "SELECT COALESCE(SUM(duration_minutes), 0) / 60.0 FROM lessons WHERE student_id = ? AND deleted_at IS NULL AND completed_at IS NOT NULL";
        Double totalHours = jdbcTemplate.queryForObject(hoursSql, Double.class, studentId);
        stats.put("totalHours", totalHours != null ? totalHours : 0.0);

        return stats;
    }

    public BigDecimal getInstructorRevenue(UUID instructorId) {
        String sql = """
            SELECT COALESCE(SUM(price), 0) 
            FROM lessons 
            WHERE instructor_id = ? 
            AND deleted_at IS NULL 
            AND completed_at IS NOT NULL 
            AND payment_status_id = 2
        """;

        BigDecimal revenue = jdbcTemplate.queryForObject(sql, BigDecimal.class, instructorId);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    private RowMapper<LessonModel> getRowMapper() {
        return new RowMapper<LessonModel>() {
            @Override
            public LessonModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                LessonModel lesson = new LessonModel();

                // IDs
                lesson.setId(UUID.fromString(rs.getString("id")));

                String instructorId = rs.getString("instructor_id");
                if (instructorId != null) {
                    lesson.setInstructorId(UUID.fromString(instructorId));
                }

                String studentId = rs.getString("student_id");
                if (studentId != null) {
                    lesson.setStudentId(UUID.fromString(studentId));
                }

                String vehicleId = rs.getString("vehicle_id");
                if (vehicleId != null) {
                    lesson.setVehicleId(UUID.fromString(vehicleId));
                }

                String addressId = rs.getString("address_id");
                if (addressId != null) {
                    lesson.setAddressId(UUID.fromString(addressId));
                }

                // Datas e horários
                lesson.setScheduledAt(rs.getTimestamp("scheduled_at").toLocalDateTime());
                lesson.setDurationMinutes(rs.getInt("duration_minutes"));

                // IDs de classificação
                lesson.setStatusId(rs.getObject("status_id", Integer.class));
                lesson.setPaymentStatusId(rs.getObject("payment_status_id", Integer.class));
                lesson.setPaymentMethodId(rs.getObject("payment_method_id", Integer.class));

                // Valores monetários
                lesson.setPrice(rs.getBigDecimal("price"));

                // Timestamps
                lesson.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                lesson.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                java.sql.Timestamp completedTimestamp = rs.getTimestamp("completed_at");
                if (completedTimestamp != null) {
                    lesson.setCompletedAt(completedTimestamp.toLocalDateTime());
                }

                java.sql.Timestamp deletedTimestamp = rs.getTimestamp("deleted_at");
                if (deletedTimestamp != null) {
                    lesson.setDeletedAt(deletedTimestamp.toLocalDateTime());
                }

                return lesson;
            }
        };
    }
}