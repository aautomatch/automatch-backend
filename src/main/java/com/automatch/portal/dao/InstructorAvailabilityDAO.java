package com.automatch.portal.dao;

import com.automatch.portal.enums.DayOfWeek;
import com.automatch.portal.model.InstructorAvailabilityModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InstructorAvailabilityDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public InstructorAvailabilityModel save(InstructorAvailabilityModel availability) {
        if (availability.getId() == null) {
            return insert(availability);
        } else {
            return update(availability);
        }
    }

    public List<InstructorAvailabilityModel> saveBatch(List<InstructorAvailabilityModel> availabilities) {
        String sql = """
            INSERT INTO instructor_availability (id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at)
            VALUES (:id, :instructorId, :dayOfWeek, :startTime, :endTime, :createdAt, :updatedAt)
        """;

        MapSqlParameterSource[] batchParams = new MapSqlParameterSource[availabilities.size()];

        for (int i = 0; i < availabilities.size(); i++) {
            InstructorAvailabilityModel availability = availabilities.get(i);
            batchParams[i] = new MapSqlParameterSource()
                    .addValue("id", availability.getId())
                    .addValue("instructorId", availability.getInstructorId())
                    .addValue("dayOfWeek", availability.getDayOfWeek().getValue())
                    .addValue("startTime", availability.getStartTime())
                    .addValue("endTime", availability.getEndTime())
                    .addValue("createdAt", availability.getCreatedAt())
                    .addValue("updatedAt", availability.getUpdatedAt());
        }

        namedParameterJdbcTemplate.batchUpdate(sql, batchParams);
        return availabilities;
    }

    private InstructorAvailabilityModel insert(InstructorAvailabilityModel availability) {
        String sql = """
            INSERT INTO instructor_availability (id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at)
            VALUES (:id, :instructorId, :dayOfWeek, :startTime, :endTime, :createdAt, :updatedAt)
        """;

        UUID id = UUID.randomUUID();
        availability.setId(id);
        availability.setCreatedAt(LocalDateTime.now());
        availability.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("instructorId", availability.getInstructorId())
                .addValue("dayOfWeek", availability.getDayOfWeek().getValue())
                .addValue("startTime", availability.getStartTime())
                .addValue("endTime", availability.getEndTime())
                .addValue("createdAt", availability.getCreatedAt())
                .addValue("updatedAt", availability.getUpdatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(id).orElse(null);
    }

    private InstructorAvailabilityModel update(InstructorAvailabilityModel availability) {
        String sql = """
            UPDATE instructor_availability 
            SET day_of_week = :dayOfWeek,
                start_time = :startTime,
                end_time = :endTime,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NULL
        """;

        availability.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", availability.getId())
                .addValue("dayOfWeek", availability.getDayOfWeek().getValue())
                .addValue("startTime", availability.getStartTime())
                .addValue("endTime", availability.getEndTime())
                .addValue("updatedAt", availability.getUpdatedAt());

        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated > 0) {
            return findById(availability.getId()).orElse(null);
        }
        return null;
    }

    public Optional<InstructorAvailabilityModel> findById(UUID id) {
        String sql = """
            SELECT id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at, deleted_at
            FROM instructor_availability 
            WHERE id = ? AND deleted_at IS NULL
        """;

        try {
            InstructorAvailabilityModel availability = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(availability);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<InstructorAvailabilityModel> findByIdWithDeleted(UUID id) {
        String sql = """
            SELECT id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at, deleted_at
            FROM instructor_availability 
            WHERE id = ?
        """;

        try {
            InstructorAvailabilityModel availability = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(availability);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<InstructorAvailabilityModel> findAll() {
        String sql = """
            SELECT id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at, deleted_at
            FROM instructor_availability 
            WHERE deleted_at IS NULL ORDER BY day_of_week, start_time
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<InstructorAvailabilityModel> findByInstructor(UUID instructorId) {
        String sql = """
            SELECT id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at, deleted_at
            FROM instructor_availability 
            WHERE instructor_id = ? AND deleted_at IS NULL 
            ORDER BY day_of_week, start_time
        """;

        return jdbcTemplate.query(sql, getRowMapper(), instructorId);
    }

    public List<InstructorAvailabilityModel> findByInstructorAndDay(UUID instructorId, DayOfWeek dayOfWeek) {
        String sql = """
            SELECT id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at, deleted_at
            FROM instructor_availability 
            WHERE instructor_id = ? AND day_of_week = ? AND deleted_at IS NULL 
            ORDER BY start_time
        """;

        return jdbcTemplate.query(sql, getRowMapper(), instructorId, dayOfWeek.getValue());
    }

    public List<InstructorAvailabilityModel> findByDay(DayOfWeek dayOfWeek) {
        String sql = """
            SELECT id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at, deleted_at
            FROM instructor_availability 
            WHERE day_of_week = ? AND deleted_at IS NULL 
            ORDER BY start_time
        """;

        return jdbcTemplate.query(sql, getRowMapper(), dayOfWeek.getValue());
    }

    public List<InstructorAvailabilityModel> findWeeklySchedule(UUID instructorId) {
        String sql = """
            SELECT id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at, deleted_at
            FROM instructor_availability 
            WHERE instructor_id = ? AND deleted_at IS NULL 
            ORDER BY day_of_week, start_time
        """;

        return jdbcTemplate.query(sql, getRowMapper(), instructorId);
    }

    public List<InstructorAvailabilityModel> findNextAvailableSlot(UUID instructorId, DayOfWeek dayOfWeek) {
        StringBuilder sql = new StringBuilder("""
            SELECT id, instructor_id, day_of_week, start_time, end_time, created_at, updated_at, deleted_at
            FROM instructor_availability 
            WHERE instructor_id = ? AND deleted_at IS NULL 
        """);

        if (dayOfWeek != null) {
            sql.append("AND day_of_week >= ? ");
        }

        sql.append("ORDER BY day_of_week, start_time LIMIT 1");

        if (dayOfWeek != null) {
            return jdbcTemplate.query(sql.toString(), getRowMapper(), instructorId, dayOfWeek.getValue());
        } else {
            return jdbcTemplate.query(sql.toString(), getRowMapper(), instructorId);
        }
    }

    public boolean checkAvailability(UUID instructorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        String sql = """
            SELECT COUNT(*) FROM instructor_availability 
            WHERE instructor_id = ? 
            AND day_of_week = ? 
            AND deleted_at IS NULL
            AND start_time <= ? 
            AND end_time >= ?
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
                instructorId, dayOfWeek.getValue(), startTime, endTime);
        return count != null && count > 0;
    }

    public List<UUID> findAvailableInstructors(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        String sql = """
            SELECT DISTINCT instructor_id 
            FROM instructor_availability 
            WHERE day_of_week = ? 
            AND deleted_at IS NULL
            AND start_time <= ? 
            AND end_time >= ?
        """;

        return jdbcTemplate.queryForList(sql, UUID.class,
                dayOfWeek.getValue(), startTime, endTime);
    }

    public boolean delete(UUID id) {
        String sql = """
            UPDATE instructor_availability 
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

    public int deleteAllByInstructor(UUID instructorId) {
        String sql = """
            UPDATE instructor_availability 
            SET deleted_at = :deletedAt,
                updated_at = :updatedAt
            WHERE instructor_id = :instructorId AND deleted_at IS NULL
        """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("instructorId", instructorId)
                .addValue("deletedAt", now)
                .addValue("updatedAt", now);

        return namedParameterJdbcTemplate.update(sql, params);
    }

    public int deleteByInstructorAndDay(UUID instructorId, DayOfWeek dayOfWeek) {
        String sql = """
            UPDATE instructor_availability 
            SET deleted_at = :deletedAt,
                updated_at = :updatedAt
            WHERE instructor_id = :instructorId AND day_of_week = :dayOfWeek AND deleted_at IS NULL
        """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("instructorId", instructorId)
                .addValue("dayOfWeek", dayOfWeek.getValue())
                .addValue("deletedAt", now)
                .addValue("updatedAt", now);

        return namedParameterJdbcTemplate.update(sql, params);
    }

    public boolean restore(UUID id) {
        String sql = """
            UPDATE instructor_availability 
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

    public boolean hasOverlap(UUID instructorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, UUID excludeId) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) FROM instructor_availability 
            WHERE instructor_id = ? 
            AND day_of_week = ? 
            AND deleted_at IS NULL
            AND ((start_time <= ? AND end_time > ?)
                 OR (start_time < ? AND end_time >= ?)
                 OR (start_time >= ? AND start_time < ?))
        """);

        if (excludeId != null) {
            sql.append(" AND id != ?");
            Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class,
                    instructorId, dayOfWeek.getValue(), endTime, startTime,
                    endTime, startTime, startTime, endTime, excludeId);
            return count != null && count > 0;
        } else {
            Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class,
                    instructorId, dayOfWeek.getValue(), endTime, startTime,
                    endTime, startTime, startTime, endTime);
            return count != null && count > 0;
        }
    }

    public int countByInstructor(UUID instructorId) {
        String sql = "SELECT COUNT(*) FROM instructor_availability WHERE instructor_id = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, instructorId);
        return count != null ? count : 0;
    }

    public int countByInstructorAndDay(UUID instructorId, DayOfWeek dayOfWeek) {
        String sql = "SELECT COUNT(*) FROM instructor_availability WHERE instructor_id = ? AND day_of_week = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, instructorId, dayOfWeek.getValue());
        return count != null ? count : 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM instructor_availability WHERE deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int countByDay(DayOfWeek dayOfWeek) {
        String sql = "SELECT COUNT(*) FROM instructor_availability WHERE day_of_week = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, dayOfWeek.getValue());
        return count != null ? count : 0;
    }

    private RowMapper<InstructorAvailabilityModel> getRowMapper() {
        return new RowMapper<InstructorAvailabilityModel>() {
            @Override
            public InstructorAvailabilityModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                InstructorAvailabilityModel availability = new InstructorAvailabilityModel();
                availability.setId(UUID.fromString(rs.getString("id")));

                String instructorId = rs.getString("instructor_id");
                if (instructorId != null) {
                    availability.setInstructorId(UUID.fromString(instructorId));
                }

                int dayValue = rs.getInt("day_of_week");
                availability.setDayOfWeek(DayOfWeek.fromValue(dayValue));

                availability.setStartTime(rs.getTime("start_time").toLocalTime());
                availability.setEndTime(rs.getTime("end_time").toLocalTime());
                availability.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                availability.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                if (rs.getTimestamp("deleted_at") != null) {
                    availability.setDeletedAt(rs.getTimestamp("deleted_at").toLocalDateTime());
                }

                return availability;
            }
        };
    }
}