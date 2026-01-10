package com.automatch.portal.dao.unauthenticated;

import com.automatch.portal.mapper.InstructorPublicMapper;
import com.automatch.portal.records.InstructorPublicRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InstructorPublicDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // SELECT para dados públicos (agora inclui cidade)
    private static final String SELECT_FIELDS = """
        i.user_id, i.hourly_rate, i.bio, i.years_experience, i.is_verified,
        i.average_rating, i.total_reviews, i.created_at, i.updated_at, i.deleted_at,
        u.id as user_id_full, u.full_name, u.email,
        u.profile_image_url, u.created_at as user_created_at,
        u.updated_at as user_updated_at, u.deleted_at as user_deleted_at,
        a.city
    """;

    private static final String FROM_CLAUSE = """
        FROM instructors i
        JOIN users u ON i.user_id = u.id
        LEFT JOIN addresses a ON u.address_id = a.id
    """;

    // Busca todos os instrutores ativos (não deletados)
    public List<InstructorPublicRecord> findAll() {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE i.deleted_at IS NULL ORDER BY u.full_name";
        return jdbcTemplate.query(sql, InstructorPublicMapper.getRowMapper());
    }

    // Busca instrutores verificados
    public List<InstructorPublicRecord> findVerified() {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE i.deleted_at IS NULL AND i.is_verified = true ORDER BY u.full_name";
        return jdbcTemplate.query(sql, InstructorPublicMapper.getRowMapper());
    }

    // Busca com filtros (agora incluindo cidade)
    public List<InstructorPublicRecord> search(
            String term,
            Integer minYearsExperience,
            BigDecimal maxHourlyRate,
            BigDecimal minRating
    ) {
        StringBuilder sql = new StringBuilder(
                "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                        " WHERE i.deleted_at IS NULL"
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (term != null && !term.trim().isEmpty()) {
            sql.append("""
            AND (
                LOWER(u.full_name) LIKE LOWER(:term)
                OR LOWER(a.city) LIKE LOWER(:term)
            )
        """);
            params.addValue("term", "%" + term.trim() + "%");
        }

        if (minYearsExperience != null) {
            sql.append(" AND i.years_experience >= :minYearsExperience");
            params.addValue("minYearsExperience", minYearsExperience);
        }

        if (maxHourlyRate != null) {
            sql.append(" AND i.hourly_rate <= :maxHourlyRate");
            params.addValue("maxHourlyRate", maxHourlyRate);
        }

        if (minRating != null) {
            sql.append(" AND i.average_rating >= :minRating");
            params.addValue("minRating", minRating);
        }

        sql.append(" ORDER BY i.average_rating DESC");

        return namedParameterJdbcTemplate.query(
                sql.toString(),
                params,
                InstructorPublicMapper.getRowMapper()
        );
    }


    // Busca os melhores avaliados
    public List<InstructorPublicRecord> findTopRated(int limit) {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE i.deleted_at IS NULL AND i.total_reviews > 0 " +
                "ORDER BY i.average_rating DESC, i.total_reviews DESC LIMIT ?";
        return jdbcTemplate.query(sql, InstructorPublicMapper.getRowMapper(), limit);
    }

    // Busca instrutores disponíveis agora
    public List<InstructorPublicRecord> findAvailableNow() {
        String sql = """
            SELECT DISTINCT """ + SELECT_FIELDS + FROM_CLAUSE + """
            WHERE i.deleted_at IS NULL 
            AND u.is_active = true
            AND i.is_verified = true
            AND EXISTS (
                SELECT 1 FROM instructor_availability ia 
                WHERE ia.instructor_id = i.user_id 
                AND ia.deleted_at IS NULL
                AND ia.day_of_week = EXTRACT(DOW FROM CURRENT_DATE)
                AND ia.start_time <= CURRENT_TIME
                AND ia.end_time >= CURRENT_TIME
            )
            ORDER BY u.full_name
        """;
        return jdbcTemplate.query(sql, InstructorPublicMapper.getRowMapper());
    }

    // Busca por faixa de preço (com cidade)
    public List<InstructorPublicRecord> findByHourlyRateRange(BigDecimal minRate, BigDecimal maxRate, String city) {
        StringBuilder sql = new StringBuilder("SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE i.deleted_at IS NULL");

        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(a.city) = LOWER(?)");
        }

        if (minRate != null) {
            sql.append(" AND i.hourly_rate >= ?");
        }

        if (maxRate != null) {
            sql.append(" AND i.hourly_rate <= ?");
        }

        sql.append(" ORDER BY i.hourly_rate");

        // Lógica para adicionar os parâmetros na ordem correta
        if (city != null && !city.trim().isEmpty() && minRate != null && maxRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    city.trim(), minRate, maxRate);
        } else if (city != null && !city.trim().isEmpty() && minRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    city.trim(), minRate);
        } else if (city != null && !city.trim().isEmpty() && maxRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    city.trim(), maxRate);
        } else if (city != null && !city.trim().isEmpty()) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    city.trim());
        } else if (minRate != null && maxRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    minRate, maxRate);
        } else if (minRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    minRate);
        } else if (maxRate != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    maxRate);
        } else {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper());
        }
    }

    // Busca por faixa de experiência (com cidade)
    public List<InstructorPublicRecord> findByExperienceRange(Integer minYears, Integer maxYears, String city) {
        StringBuilder sql = new StringBuilder("SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE i.deleted_at IS NULL");

        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(a.city) = LOWER(?)");
        }

        if (minYears != null) {
            sql.append(" AND i.years_experience >= ?");
        }

        if (maxYears != null) {
            sql.append(" AND i.years_experience <= ?");
        }

        sql.append(" ORDER BY i.years_experience DESC");

        // Lógica para adicionar os parâmetros na ordem correta
        if (city != null && !city.trim().isEmpty() && minYears != null && maxYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    city.trim(), minYears, maxYears);
        } else if (city != null && !city.trim().isEmpty() && minYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    city.trim(), minYears);
        } else if (city != null && !city.trim().isEmpty() && maxYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    city.trim(), maxYears);
        } else if (city != null && !city.trim().isEmpty()) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    city.trim());
        } else if (minYears != null && maxYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    minYears, maxYears);
        } else if (minYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    minYears);
        } else if (maxYears != null) {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper(),
                    maxYears);
        } else {
            return jdbcTemplate.query(sql.toString(), InstructorPublicMapper.getRowMapper());
        }
    }

    // Buscar instrutores por cidade
    public List<InstructorPublicRecord> findByCity(String city) {
        String sql = "SELECT " + SELECT_FIELDS + FROM_CLAUSE +
                "WHERE i.deleted_at IS NULL AND LOWER(a.city) = LOWER(?) ORDER BY u.full_name";
        return jdbcTemplate.query(sql, InstructorPublicMapper.getRowMapper(), city.trim());
    }

    // Buscar todas as cidades disponíveis
    public List<String> findAllCities() {
        String sql = """
            SELECT DISTINCT LOWER(a.city) as city 
            FROM instructors i
            JOIN users u ON i.user_id = u.id
            JOIN addresses a ON u.address_id = a.id
            WHERE i.deleted_at IS NULL 
            AND a.city IS NOT NULL 
            AND a.city != ''
            ORDER BY city
        """;
        return jdbcTemplate.queryForList(sql, String.class);
    }

    // Contagem de instrutores
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM instructors WHERE deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // Contagem de instrutores verificados
    public int countVerified() {
        String sql = "SELECT COUNT(*) FROM instructors WHERE deleted_at IS NULL AND is_verified = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // Estatísticas de preço por hora
    public Object getHourlyRateStats() {
        String sql = """
            SELECT 
                COUNT(*) as total_instructors,
                COALESCE(AVG(hourly_rate), 0) as average_hourly_rate,
                COALESCE(MIN(hourly_rate), 0) as min_hourly_rate,
                COALESCE(MAX(hourly_rate), 0) as max_hourly_rate
            FROM instructors 
            WHERE deleted_at IS NULL
        """;
        return jdbcTemplate.queryForMap(sql);
    }
}