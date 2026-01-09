package com.automatch.portal.dao;

import com.automatch.portal.model.DocumentModel;
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
public class DocumentDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DocumentModel save(DocumentModel document) {
        if (document.getId() == null) {
            return insert(document);
        } else {
            return update(document);
        }
    }

    private DocumentModel insert(DocumentModel document) {
        String sql = """
            INSERT INTO user_documents (id, user_id, document_type_id, document_number, 
                                      document_image_url, issue_date, expiry_date, is_verified,
                                      verified_by_user_id, verified_at, verification_notes,
                                      created_at, updated_at)
            VALUES (:id, :userId, :documentTypeId, :documentNumber, 
                    :documentImageUrl, :issueDate, :expiryDate, :isVerified,
                    :verifiedByUserId, :verifiedAt, :verificationNotes,
                    :createdAt, :updatedAt)
        """;

        UUID id = UUID.randomUUID();
        document.setId(id);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", document.getUserId())
                .addValue("documentTypeId", document.getDocumentTypeId())
                .addValue("documentNumber", document.getDocumentNumber())
                .addValue("documentImageUrl", document.getDocumentImageUrl())
                .addValue("issueDate", document.getIssueDate())
                .addValue("expiryDate", document.getExpiryDate())
                .addValue("isVerified", document.getIsVerified())
                .addValue("verifiedByUserId", document.getVerifiedByUserId())
                .addValue("verifiedAt", document.getVerifiedAt())
                .addValue("verificationNotes", document.getVerificationNotes())
                .addValue("createdAt", document.getCreatedAt())
                .addValue("updatedAt", document.getUpdatedAt());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(id).orElse(null);
    }

    private DocumentModel update(DocumentModel document) {
        String sql = """
            UPDATE user_documents 
            SET document_number = :documentNumber,
                document_image_url = :documentImageUrl,
                issue_date = :issueDate,
                expiry_date = :expiryDate,
                is_verified = :isVerified,
                verified_by_user_id = :verifiedByUserId,
                verified_at = :verifiedAt,
                verification_notes = :verificationNotes,
                updated_at = :updatedAt
            WHERE id = :id AND deleted_at IS NULL
        """;

        document.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", document.getId())
                .addValue("documentNumber", document.getDocumentNumber())
                .addValue("documentImageUrl", document.getDocumentImageUrl())
                .addValue("issueDate", document.getIssueDate())
                .addValue("expiryDate", document.getExpiryDate())
                .addValue("isVerified", document.getIsVerified())
                .addValue("verifiedByUserId", document.getVerifiedByUserId())
                .addValue("verifiedAt", document.getVerifiedAt())
                .addValue("verificationNotes", document.getVerificationNotes())
                .addValue("updatedAt", document.getUpdatedAt());

        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated > 0) {
            return findById(document.getId()).orElse(null);
        }
        return null;
    }

    public Optional<DocumentModel> findById(UUID id) {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE id = ? AND deleted_at IS NULL
        """;

        try {
            DocumentModel document = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(document);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<DocumentModel> findByIdWithDeleted(UUID id) {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE id = ?
        """;

        try {
            DocumentModel document = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(document);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<DocumentModel> findAll() {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE deleted_at IS NULL 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<DocumentModel> findByUser(UUID userId) {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE user_id = ? AND deleted_at IS NULL 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }

    public Optional<DocumentModel> findByUserAndType(UUID userId, Integer documentTypeId) {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE user_id = ? AND document_type_id = ? AND deleted_at IS NULL
        """;

        try {
            DocumentModel document = jdbcTemplate.queryForObject(sql, getRowMapper(), userId, documentTypeId);
            return Optional.ofNullable(document);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<DocumentModel> findByType(Integer documentTypeId) {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE document_type_id = ? AND deleted_at IS NULL 
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), documentTypeId);
    }

    public List<DocumentModel> findVerified() {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE deleted_at IS NULL AND is_verified = true 
            ORDER BY verified_at DESC
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<DocumentModel> findPendingVerification() {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE deleted_at IS NULL AND is_verified = false 
            ORDER BY created_at ASC
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<DocumentModel> findExpiringSoon(LocalDate thresholdDate) {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE deleted_at IS NULL AND expiry_date IS NOT NULL 
            AND expiry_date <= ? AND expiry_date >= CURRENT_DATE 
            ORDER BY expiry_date ASC
        """;

        return jdbcTemplate.query(sql, getRowMapper(), thresholdDate);
    }

    public List<DocumentModel> findExpired() {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE deleted_at IS NULL AND expiry_date IS NOT NULL 
            AND expiry_date < CURRENT_DATE 
            ORDER BY expiry_date ASC
        """;

        return jdbcTemplate.query(sql, getRowMapper());
    }

    public Optional<DocumentModel> findByDocumentNumber(String documentNumber) {
        String sql = """
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE document_number = ? AND deleted_at IS NULL
        """;

        try {
            DocumentModel document = jdbcTemplate.queryForObject(sql, getRowMapper(), documentNumber);
            return Optional.ofNullable(document);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean delete(UUID id) {
        String sql = """
            UPDATE user_documents 
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
            UPDATE user_documents 
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

    public boolean existsByUserAndType(UUID userId, Integer documentTypeId) {
        String sql = "SELECT COUNT(*) FROM user_documents WHERE user_id = ? AND document_type_id = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, documentTypeId);
        return count != null && count > 0;
    }

    public boolean existsByDocumentNumber(String documentNumber) {
        String sql = "SELECT COUNT(*) FROM user_documents WHERE document_number = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, documentNumber);
        return count != null && count > 0;
    }

    public List<DocumentModel> search(String documentNumber, Integer documentTypeId, Boolean isVerified) {
        StringBuilder sql = new StringBuilder("""
            SELECT id, user_id, document_type_id, document_number, document_image_url,
                   issue_date, expiry_date, is_verified, verified_by_user_id, verified_at,
                   verification_notes, created_at, updated_at, deleted_at
            FROM user_documents 
            WHERE deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (documentNumber != null && !documentNumber.trim().isEmpty()) {
            sql.append(" AND LOWER(document_number) LIKE LOWER(:documentNumber)");
            params.addValue("documentNumber", "%" + documentNumber + "%");
        }

        if (documentTypeId != null) {
            sql.append(" AND document_type_id = :documentTypeId");
            params.addValue("documentTypeId", documentTypeId);
        }

        if (isVerified != null) {
            sql.append(" AND is_verified = :isVerified");
            params.addValue("isVerified", isVerified);
        }

        sql.append(" ORDER BY created_at DESC");

        return namedParameterJdbcTemplate.query(sql.toString(), params, getRowMapper());
    }

    public Object getUserDocumentStats(UUID userId) {
        String sql = """
            SELECT 
                COUNT(*) as total_documents,
                COUNT(CASE WHEN is_verified = true THEN 1 END) as verified_documents,
                COUNT(CASE WHEN is_verified = false THEN 1 END) as pending_documents,
                COUNT(CASE WHEN expiry_date IS NOT NULL AND expiry_date < CURRENT_DATE THEN 1 END) as expired_documents,
                COUNT(CASE WHEN expiry_date IS NOT NULL AND expiry_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 30 THEN 1 END) as expiring_soon_documents,
                MIN(created_at) as first_document_date,
                MAX(created_at) as last_document_date
            FROM user_documents 
            WHERE user_id = ? AND deleted_at IS NULL
            GROUP BY user_id
        """;

        try {
            return jdbcTemplate.queryForMap(sql, userId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Object getVerifierStats(UUID verifierId) {
        String sql = """
            SELECT 
                COUNT(*) as total_verified,
                COUNT(DISTINCT user_id) as unique_users_verified,
                MIN(verified_at) as first_verification_date,
                MAX(verified_at) as last_verification_date
            FROM user_documents 
            WHERE verified_by_user_id = ? AND deleted_at IS NULL AND is_verified = true
            GROUP BY verified_by_user_id
        """;

        try {
            return jdbcTemplate.queryForMap(sql, verifierId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int countByUser(UUID userId) {
        String sql = "SELECT COUNT(*) FROM user_documents WHERE user_id = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public int countVerifiedByUser(UUID userId) {
        String sql = "SELECT COUNT(*) FROM user_documents WHERE user_id = ? AND deleted_at IS NULL AND is_verified = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM user_documents WHERE deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int countVerified() {
        String sql = "SELECT COUNT(*) FROM user_documents WHERE deleted_at IS NULL AND is_verified = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int countPendingVerification() {
        String sql = "SELECT COUNT(*) FROM user_documents WHERE deleted_at IS NULL AND is_verified = false";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    private RowMapper<DocumentModel> getRowMapper() {
        return new RowMapper<DocumentModel>() {
            @Override
            public DocumentModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                DocumentModel document = new DocumentModel();
                document.setId(UUID.fromString(rs.getString("id")));

                String userId = rs.getString("user_id");
                if (userId != null) {
                    document.setUserId(UUID.fromString(userId));
                }

                document.setDocumentTypeId(rs.getObject("document_type_id", Integer.class));
                document.setDocumentNumber(rs.getString("document_number"));
                document.setDocumentImageUrl(rs.getString("document_image_url"));
                document.setIssueDate(rs.getObject("issue_date", LocalDate.class));
                document.setExpiryDate(rs.getObject("expiry_date", LocalDate.class));
                document.setIsVerified(rs.getBoolean("is_verified"));

                String verifiedByUserId = rs.getString("verified_by_user_id");
                if (verifiedByUserId != null) {
                    document.setVerifiedByUserId(UUID.fromString(verifiedByUserId));
                }

                java.sql.Timestamp verifiedAt = rs.getTimestamp("verified_at");
                if (verifiedAt != null) {
                    document.setVerifiedAt(verifiedAt.toLocalDateTime());
                }

                document.setVerificationNotes(rs.getString("verification_notes"));
                document.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                document.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                java.sql.Timestamp deletedAt = rs.getTimestamp("deleted_at");
                if (deletedAt != null) {
                    document.setDeletedAt(deletedAt.toLocalDateTime());
                }

                return document;
            }
        };
    }
}