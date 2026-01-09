package com.automatch.portal.dao;

import com.automatch.portal.model.VehicleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VehicleDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public VehicleModel save(VehicleModel vehicle) {
        if (vehicle.getId() == null) {
            return insert(vehicle);
        } else {
            return update(vehicle);
        }
    }

    private VehicleModel insert(VehicleModel vehicle) {
        String sql = """
            INSERT INTO vehicles (id, instructor_id, license_plate, model, brand, year, color, 
                                 vehicle_image_url, transmission_type_id, category_id, 
                                 has_dual_controls, has_air_conditioning, is_approved, 
                                 is_available, last_maintenance_date, created_at, updated_at)
            VALUES (:id, :instructorId, :licensePlate, :model, :brand, :year, :color, 
                    :vehicleImageUrl, :transmissionTypeId, :categoryId, 
                    :hasDualControls, :hasAirConditioning, :isApproved, 
                    :isAvailable, :lastMaintenanceDate, :createdAt, :updatedAt)
        """;

        UUID id = UUID.randomUUID();
        vehicle.setId(id);
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("instructorId", vehicle.getInstructorId())
                .addValue("licensePlate", vehicle.getLicensePlate())
                .addValue("model", vehicle.getModel())
                .addValue("brand", vehicle.getBrand())
                .addValue("year", vehicle.getYear())
                .addValue("color", vehicle.getColor())
                .addValue("vehicleImageUrl", vehicle.getVehicleImageUrl())
                .addValue("transmissionTypeId", vehicle.getTransmissionTypeId())
                .addValue("categoryId", vehicle.getCategoryId())
                .addValue("hasDualControls", vehicle.getHasDualControls())
                .addValue("hasAirConditioning", vehicle.getHasAirConditioning())
                .addValue("isApproved", vehicle.getIsApproved())
                .addValue("isAvailable", vehicle.getIsAvailable())
                .addValue("lastMaintenanceDate", vehicle.getLastMaintenanceDate())
                .addValue("createdAt", vehicle.getCreatedAt())
                .addValue("updatedAt", vehicle.getUpdatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(id).orElse(null);
    }

    private VehicleModel update(VehicleModel vehicle) {
        String sql = """
            UPDATE vehicles 
            SET license_plate = :licensePlate,
                model = :model,
                brand = :brand,
                year = :year,
                color = :color,
                vehicle_image_url = :vehicleImageUrl,
                transmission_type_id = :transmissionTypeId,
                category_id = :categoryId,
                has_dual_controls = :hasDualControls,
                has_air_conditioning = :hasAirConditioning,
                is_approved = :isApproved,
                is_available = :isAvailable,
                last_maintenance_date = :lastMaintenanceDate,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NULL
        """;

        vehicle.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", vehicle.getId())
                .addValue("licensePlate", vehicle.getLicensePlate())
                .addValue("model", vehicle.getModel())
                .addValue("brand", vehicle.getBrand())
                .addValue("year", vehicle.getYear())
                .addValue("color", vehicle.getColor())
                .addValue("vehicleImageUrl", vehicle.getVehicleImageUrl())
                .addValue("transmissionTypeId", vehicle.getTransmissionTypeId())
                .addValue("categoryId", vehicle.getCategoryId())
                .addValue("hasDualControls", vehicle.getHasDualControls())
                .addValue("hasAirConditioning", vehicle.getHasAirConditioning())
                .addValue("isApproved", vehicle.getIsApproved())
                .addValue("isAvailable", vehicle.getIsAvailable())
                .addValue("lastMaintenanceDate", vehicle.getLastMaintenanceDate())
                .addValue("updatedAt", vehicle.getUpdatedAt());

        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated > 0) {
            return findById(vehicle.getId()).orElse(null);
        }
        return null;
    }

    public Optional<VehicleModel> findById(UUID id) {
        String sql = """
            SELECT id, instructor_id, license_plate, model, brand, year, color, 
                   vehicle_image_url, transmission_type_id, category_id, has_dual_controls,
                   has_air_conditioning, is_approved, is_available, last_maintenance_date,
                   created_at, updated_at, deleted_at
            FROM vehicles 
            WHERE id = ? AND deleted_at IS NULL
        """;

        try {
            VehicleModel vehicle = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(vehicle);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<VehicleModel> findAll() {
        String sql = """
            SELECT id, instructor_id, license_plate, model, brand, year, color, 
                   vehicle_image_url, transmission_type_id, category_id, has_dual_controls,
                   has_air_conditioning, is_approved, is_available, last_maintenance_date,
                   created_at, updated_at, deleted_at
            FROM vehicles 
            WHERE deleted_at IS NULL
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<VehicleModel> findByInstructor(UUID instructorId) {
        String sql = """
            SELECT id, instructor_id, license_plate, model, brand, year, color, 
                   vehicle_image_url, transmission_type_id, category_id, has_dual_controls,
                   has_air_conditioning, is_approved, is_available, last_maintenance_date,
                   created_at, updated_at, deleted_at
            FROM vehicles 
            WHERE instructor_id = ? AND deleted_at IS NULL
        """;

        return jdbcTemplate.query(sql, getRowMapper(), instructorId);
    }

    public List<VehicleModel> findAvailable() {
        String sql = """
            SELECT id, instructor_id, license_plate, model, brand, year, color, 
                   vehicle_image_url, transmission_type_id, category_id, has_dual_controls,
                   has_air_conditioning, is_approved, is_available, last_maintenance_date,
                   created_at, updated_at, deleted_at
            FROM vehicles 
            WHERE deleted_at IS NULL AND is_available = true AND is_approved = true
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<VehicleModel> findApproved() {
        String sql = """
            SELECT id, instructor_id, license_plate, model, brand, year, color, 
                   vehicle_image_url, transmission_type_id, category_id, has_dual_controls,
                   has_air_conditioning, is_approved, is_available, last_maintenance_date,
                   created_at, updated_at, deleted_at
            FROM vehicles 
            WHERE deleted_at IS NULL AND is_approved = true
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public boolean delete(UUID id) {
        String sql = """
            UPDATE vehicles 
            SET is_available = false, 
                deleted_at = :deletedAt,
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

    public Optional<VehicleModel> findByLicensePlate(String licensePlate) {
        String sql = """
            SELECT id, instructor_id, license_plate, model, brand, year, color, 
                   vehicle_image_url, transmission_type_id, category_id, has_dual_controls,
                   has_air_conditioning, is_approved, is_available, last_maintenance_date,
                   created_at, updated_at, deleted_at
            FROM vehicles 
            WHERE license_plate = ? AND deleted_at IS NULL
        """;

        try {
            VehicleModel vehicle = jdbcTemplate.queryForObject(sql, getRowMapper(), licensePlate);
            return Optional.ofNullable(vehicle);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByLicensePlate(String licensePlate) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE license_plate = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, licensePlate);
        return count != null && count > 0;
    }

    public List<VehicleModel> findByTransmissionType(Integer transmissionTypeId) {
        String sql = """
            SELECT id, instructor_id, license_plate, model, brand, year, color, 
                   vehicle_image_url, transmission_type_id, category_id, has_dual_controls,
                   has_air_conditioning, is_approved, is_available, last_maintenance_date,
                   created_at, updated_at, deleted_at
            FROM vehicles 
            WHERE transmission_type_id = ? AND deleted_at IS NULL
        """;

        return jdbcTemplate.query(sql, getRowMapper(), transmissionTypeId);
    }

    public List<VehicleModel> findByCategory(Integer categoryId) {
        String sql = """
            SELECT id, instructor_id, license_plate, model, brand, year, color, 
                   vehicle_image_url, transmission_type_id, category_id, has_dual_controls,
                   has_air_conditioning, is_approved, is_available, last_maintenance_date,
                   created_at, updated_at, deleted_at
            FROM vehicles 
            WHERE category_id = ? AND deleted_at IS NULL
        """;

        return jdbcTemplate.query(sql, getRowMapper(), categoryId);
    }

    public int countAvailableVehicles() {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE deleted_at IS NULL AND is_available = true AND is_approved = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int countByInstructor(UUID instructorId) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE instructor_id = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, instructorId);
        return count != null ? count : 0;
    }

    private RowMapper<VehicleModel> getRowMapper() {
        return new RowMapper<VehicleModel>() {
            @Override
            public VehicleModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                VehicleModel vehicle = new VehicleModel();
                vehicle.setId(UUID.fromString(rs.getString("id")));

                String instructorId = rs.getString("instructor_id");
                if (instructorId != null) {
                    vehicle.setInstructorId(UUID.fromString(instructorId));
                }

                vehicle.setLicensePlate(rs.getString("license_plate"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setBrand(rs.getString("brand"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setVehicleImageUrl(rs.getString("vehicle_image_url"));
                vehicle.setTransmissionTypeId(rs.getObject("transmission_type_id", Integer.class));
                vehicle.setCategoryId(rs.getObject("category_id", Integer.class));
                vehicle.setHasDualControls(rs.getBoolean("has_dual_controls"));
                vehicle.setHasAirConditioning(rs.getBoolean("has_air_conditioning"));
                vehicle.setIsApproved(rs.getBoolean("is_approved"));
                vehicle.setIsAvailable(rs.getBoolean("is_available"));
                vehicle.setLastMaintenanceDate(rs.getObject("last_maintenance_date", LocalDate.class));
                vehicle.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                vehicle.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                vehicle.setDeletedAt(rs.getTimestamp("deleted_at") != null ?
                        rs.getTimestamp("deleted_at").toLocalDateTime() : null);

                return vehicle;
            }
        };
    }
}