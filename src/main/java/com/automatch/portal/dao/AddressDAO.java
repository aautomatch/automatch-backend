package com.automatch.portal.dao;

import com.automatch.portal.mapper.AddressMapper;
import com.automatch.portal.model.AddressModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AddressDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_FIELDS = """
        id, street, number, neighborhood, city, state, zip_code, country,
        created_at, updated_at, deleted_at
    """;

    public AddressModel save(AddressModel address) {
        if (address.getId() == null) {
            return insert(address);
        } else {
            return update(address);
        }
    }

    private AddressModel insert(AddressModel address) {
        String sql = """
            INSERT INTO addresses (id, street, number, neighborhood, city, state, 
                                 zip_code, country, created_at, updated_at)
            VALUES (:id, :street, :number, :neighborhood, :city, :state, 
                    :zipCode, :country, :createdAt, :updatedAt)
        """;

        UUID id = UUID.randomUUID();
        address.setId(id);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("street", address.getStreet())
                .addValue("number", address.getNumber())
                .addValue("neighborhood", address.getNeighborhood())
                .addValue("city", address.getCity())
                .addValue("state", address.getState())
                .addValue("zipCode", address.getZipCode())
                .addValue("country", address.getCountry())
                .addValue("createdAt", address.getCreatedAt())
                .addValue("updatedAt", address.getUpdatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(id).orElse(null);
    }

    private AddressModel update(AddressModel address) {
        String sql = """
            UPDATE addresses 
            SET street = :street,
                number = :number,
                neighborhood = :neighborhood,
                city = :city,
                state = :state,
                zip_code = :zipCode,
                country = :country,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NULL
        """;

        address.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", address.getId())
                .addValue("street", address.getStreet())
                .addValue("number", address.getNumber())
                .addValue("neighborhood", address.getNeighborhood())
                .addValue("city", address.getCity())
                .addValue("state", address.getState())
                .addValue("zipCode", address.getZipCode())
                .addValue("country", address.getCountry())
                .addValue("updatedAt", address.getUpdatedAt());

        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated > 0) {
            return findById(address.getId()).orElse(null);
        }
        return null;
    }

    public Optional<AddressModel> findById(UUID id) {
        String sql = "SELECT " + SELECT_FIELDS +
                " FROM addresses WHERE id = ? AND deleted_at IS NULL";

        try {
            AddressModel address = jdbcTemplate.queryForObject(sql, AddressMapper.getRowMapper(), id);
            return Optional.ofNullable(address);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<AddressModel> findByIdWithDeleted(UUID id) {
        String sql = "SELECT " + SELECT_FIELDS +
                " FROM addresses WHERE id = ?";

        try {
            AddressModel address = jdbcTemplate.queryForObject(sql, AddressMapper.getRowMapper(), id);
            return Optional.ofNullable(address);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<AddressModel> findAll() {
        String sql = "SELECT " + SELECT_FIELDS +
                " FROM addresses WHERE deleted_at IS NULL";

        return jdbcTemplate.query(sql, AddressMapper.getRowMapper());
    }

    public Optional<AddressModel> findByUserId(UUID userId) {
        String sql = """
            SELECT a.id, a.street, a.number, a.neighborhood, a.city, a.state, 
                   a.zip_code, a.country, a.created_at, a.updated_at, a.deleted_at
            FROM addresses a
            JOIN users u ON a.id = u.address_id
            WHERE u.id = ? AND a.deleted_at IS NULL
        """;

        try {
            AddressModel address = jdbcTemplate.queryForObject(sql, AddressMapper.getRowMapper(), userId);
            return Optional.ofNullable(address);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<AddressModel> findByCity(String city) {
        String sql = "SELECT " + SELECT_FIELDS +
                " FROM addresses WHERE city = ? AND deleted_at IS NULL";

        return jdbcTemplate.query(sql, AddressMapper.getRowMapper(), city);
    }

    public List<AddressModel> findByState(String state) {
        String sql = "SELECT " + SELECT_FIELDS +
                " FROM addresses WHERE state = ? AND deleted_at IS NULL";

        return jdbcTemplate.query(sql, AddressMapper.getRowMapper(), state);
    }

    public List<AddressModel> findByZipCode(String zipCode) {
        String sql = "SELECT " + SELECT_FIELDS +
                " FROM addresses WHERE zip_code = ? AND deleted_at IS NULL";

        return jdbcTemplate.query(sql, AddressMapper.getRowMapper(), zipCode);
    }

    public List<AddressModel> findByCountry(String country) {
        String sql = "SELECT " + SELECT_FIELDS +
                " FROM addresses WHERE country = ? AND deleted_at IS NULL";

        return jdbcTemplate.query(sql, AddressMapper.getRowMapper(), country);
    }

    public boolean delete(UUID id) {
        String sql = """
            UPDATE addresses 
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
            UPDATE addresses 
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

    public List<AddressModel> search(String street, String neighborhood, String city, String state) {
        StringBuilder sql = new StringBuilder("SELECT " + SELECT_FIELDS + " FROM addresses WHERE deleted_at IS NULL");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (street != null && !street.trim().isEmpty()) {
            sql.append(" AND LOWER(street) LIKE LOWER(:street)");
            params.addValue("street", "%" + street + "%");
        }

        if (neighborhood != null && !neighborhood.trim().isEmpty()) {
            sql.append(" AND LOWER(neighborhood) LIKE LOWER(:neighborhood)");
            params.addValue("neighborhood", "%" + neighborhood + "%");
        }

        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(city) LIKE LOWER(:city)");
            params.addValue("city", "%" + city + "%");
        }

        if (state != null && !state.trim().isEmpty()) {
            sql.append(" AND LOWER(state) LIKE LOWER(:state)");
            params.addValue("state", "%" + state + "%");
        }

        return namedParameterJdbcTemplate.query(sql.toString(), params, AddressMapper.getRowMapper());
    }

    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM addresses WHERE id = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public int countActive() {
        String sql = "SELECT COUNT(*) FROM addresses WHERE deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int countByCity(String city) {
        String sql = "SELECT COUNT(*) FROM addresses WHERE city = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, city);
        return count != null ? count : 0;
    }

    public int countByState(String state) {
        String sql = "SELECT COUNT(*) FROM addresses WHERE state = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, state);
        return count != null ? count : 0;
    }

    public int countByCountry(String country) {
        String sql = "SELECT COUNT(*) FROM addresses WHERE country = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, country);
        return count != null ? count : 0;
    }
}