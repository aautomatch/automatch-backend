package com.automatch.portal.dao;

import com.automatch.portal.mapper.ClassifierMapper;
import com.automatch.portal.model.ClassifierModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClassifierDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_FIELDS = "id, type, value, description";

    public ClassifierModel save(ClassifierModel classifier) {
        if (classifier.getId() == null) {
            return insert(classifier);
        } else {
            return update(classifier);
        }
    }

    private ClassifierModel insert(ClassifierModel classifier) {
        String sql = """
            INSERT INTO classifier (type, value, description)
            VALUES (:type, :value, :description)
            RETURNING id, type, value, description
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("type", classifier.getType())
                .addValue("value", classifier.getValue())
                .addValue("description", classifier.getDescription());

        return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            ClassifierModel saved = new ClassifierModel();
            saved.setId(rs.getInt("id"));
            saved.setType(rs.getString("type"));
            saved.setValue(rs.getString("value"));
            saved.setDescription(rs.getString("description"));
            return saved;
        });
    }

    private ClassifierModel update(ClassifierModel classifier) {
        String sql = """
            UPDATE classifier 
            SET type = :type,
                value = :value,
                description = :description
            WHERE id = :id
            RETURNING id, type, value, description
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", classifier.getId())
                .addValue("type", classifier.getType())
                .addValue("value", classifier.getValue())
                .addValue("description", classifier.getDescription());

        return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            ClassifierModel updated = new ClassifierModel();
            updated.setId(rs.getInt("id"));
            updated.setType(rs.getString("type"));
            updated.setValue(rs.getString("value"));
            updated.setDescription(rs.getString("description"));
            return updated;
        });
    }

    public Optional<ClassifierModel> findById(Integer id) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM classifier WHERE id = ?";

        try {
            ClassifierModel classifier = jdbcTemplate.queryForObject(sql, ClassifierMapper.getRowMapper(), id);
            return Optional.ofNullable(classifier);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ClassifierModel> findAll() {
        String sql = "SELECT " + SELECT_FIELDS + " FROM classifier ORDER BY type, value";
        return jdbcTemplate.query(sql, ClassifierMapper.getRowMapper());
    }

    public List<ClassifierModel> findByType(String type) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM classifier WHERE type = ? ORDER BY value";
        return jdbcTemplate.query(sql, ClassifierMapper.getRowMapper(), type);
    }

    public List<ClassifierModel> findByValue(String value) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM classifier WHERE value = ? ORDER BY type";
        return jdbcTemplate.query(sql, ClassifierMapper.getRowMapper(), value);
    }

    public Optional<ClassifierModel> findByTypeAndValue(String type, String value) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM classifier WHERE type = ? AND value = ?";

        try {
            ClassifierModel classifier = jdbcTemplate.queryForObject(sql, ClassifierMapper.getRowMapper(), type, value);
            return Optional.ofNullable(classifier);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM classifier WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        return deleted > 0;
    }

    public List<ClassifierModel> search(String type, String value, String description) {
        StringBuilder sql = new StringBuilder("SELECT " + SELECT_FIELDS + " FROM classifier WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (type != null && !type.trim().isEmpty()) {
            sql.append(" AND LOWER(type) LIKE LOWER(:type)");
            params.addValue("type", "%" + type + "%");
        }

        if (value != null && !value.trim().isEmpty()) {
            sql.append(" AND LOWER(value) LIKE LOWER(:value)");
            params.addValue("value", "%" + value + "%");
        }

        if (description != null && !description.trim().isEmpty()) {
            sql.append(" AND LOWER(description) LIKE LOWER(:description)");
            params.addValue("description", "%" + description + "%");
        }

        sql.append(" ORDER BY type, value");

        return namedParameterJdbcTemplate.query(sql.toString(), params, ClassifierMapper.getRowMapper());
    }

    public List<String> findAllTypes() {
        String sql = "SELECT DISTINCT type FROM classifier ORDER BY type";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public boolean existsByTypeAndValue(String type, String value) {
        String sql = "SELECT COUNT(*) FROM classifier WHERE type = ? AND value = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, type, value);
        return count != null && count > 0;
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM classifier WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public int countByType(String type) {
        String sql = "SELECT COUNT(*) FROM classifier WHERE type = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, type);
        return count != null ? count : 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM classifier";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}