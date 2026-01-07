package com.automatch.portal.dao;

import com.automatch.portal.mapper.UserMapper;
import com.automatch.portal.model.UserModel;
import com.automatch.portal.enums.UserRole;
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
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_FIELDS = """
        u.id, u.full_name, u.email, u.phone, u.password, u.role, u.is_active, 
        u.profile_image_url, u.created_at, u.last_loggin, u.updated_at, u.deleted_at,
        a.id as address_id, a.street, a.number, a.neighborhood, a.city, a.state, 
        a.zip_code, a.country, a.created_at as address_created_at, 
        a.updated_at as address_updated_at, a.deleted_at as address_deleted_at
    """;

    private static final String FROM_CLAUSE = """
        FROM users u
        LEFT JOIN addresses a ON u.address_id = a.id
    """;

    public UserModel save(UserModel user) {
        if (user.getId() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private UserModel insert(UserModel user) {
        String sql = """
            INSERT INTO users (id, full_name, email, password, phone, role, is_active, 
                              profile_image_url, address_id, created_at, updated_at)
            VALUES (:id, :fullName, :email, :password, :phone, :role, :isActive, 
                    :profileImageUrl, :addressId, :createdAt, :updatedAt)
        """;

        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("fullName", user.getFullName())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("phone", user.getPhone())
                .addValue("role", user.getRole() != null ? user.getRole().name() : null)
                .addValue("isActive", user.getIsActive())
                .addValue("profileImageUrl", user.getProfileImageUrl())
                .addValue("addressId", user.getAddress() != null ? user.getAddress().getId() : null)
                .addValue("createdAt", user.getCreatedAt())
                .addValue("updatedAt", user.getUpdatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(id).orElse(null);
    }

    private UserModel update(UserModel user) {
        String sql = """
            UPDATE users 
            SET full_name = :fullName,
                email = :email,
                phone = :phone,
                role = :role,
                is_active = :isActive,
                profile_image_url = :profileImageUrl,
                address_id = :addressId,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NULL
        """;

        user.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("fullName", user.getFullName())
                .addValue("email", user.getEmail())
                .addValue("phone", user.getPhone())
                .addValue("role", user.getRole() != null ? user.getRole().name() : null)
                .addValue("isActive", user.getIsActive())
                .addValue("profileImageUrl", user.getProfileImageUrl())
                .addValue("addressId", user.getAddress() != null ? user.getAddress().getId() : null)
                .addValue("updatedAt", user.getUpdatedAt());

        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated > 0) {
            return findById(user.getId()).orElse(null);
        }
        return null;
    }

    public Optional<UserModel> findById(UUID id) {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE u.id = ? AND u.deleted_at IS NULL";

        try {
            UserModel user = jdbcTemplate.queryForObject(sql, UserMapper.getRowMapper(), id);
            return Optional.ofNullable(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<UserModel> findAll() {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE u.deleted_at IS NULL";

        return jdbcTemplate.query(sql, UserMapper.getRowMapper());
    }

    public List<UserModel> findAllActive() {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE u.deleted_at IS NULL AND u.is_active = true";

        return jdbcTemplate.query(sql, UserMapper.getRowMapper());
    }

    public boolean delete(UUID id) {
        String sql = """
            UPDATE users 
            SET is_active = false, 
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

    public boolean activate(UUID id) {
        String sql = """
            UPDATE users 
            SET is_active = true, 
                deleted_at = NULL,
                updated_at = :updatedAt
            WHERE id = :id
        """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("updatedAt", now);

        int updated = namedParameterJdbcTemplate.update(sql, params);
        return updated > 0;
    }

    public Optional<UserModel> findByEmail(String email) {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE u.email = ? AND u.deleted_at IS NULL";

        try {
            UserModel user = jdbcTemplate.queryForObject(sql, UserMapper.getRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean existsByEmailAndNotId(String email, UUID id) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email, id);
        return count != null && count > 0;
    }

    public void updateLastLogin(UUID userId) {
        String sql = "UPDATE users SET last_loggin = ? WHERE id = ?";
        jdbcTemplate.update(sql, LocalDateTime.now(), userId);
    }

    public List<UserModel> findByRole(UserRole role) {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE u.role = ? AND u.deleted_at IS NULL AND u.is_active = true";

        return jdbcTemplate.query(sql, UserMapper.getRowMapper(), role.name());
    }

    public List<UserModel> findByRoleAndActive(UserRole role, boolean active) {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE u.role = ? AND u.deleted_at IS NULL AND u.is_active = ?";

        return jdbcTemplate.query(sql, UserMapper.getRowMapper(), role.name(), active);
    }

    public int countActiveUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL AND is_active = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int countByRole(UserRole role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, role.name());
        return count != null ? count : 0;
    }
}