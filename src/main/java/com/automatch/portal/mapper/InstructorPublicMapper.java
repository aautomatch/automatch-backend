package com.automatch.portal.mapper;

import com.automatch.portal.records.InstructorPublicRecord;
import com.automatch.portal.records.UserPublicRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class InstructorPublicMapper {

    public static RowMapper<InstructorPublicRecord> getRowMapper() {
        return new RowMapper<InstructorPublicRecord>() {
            @Override
            public InstructorPublicRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
                // Mapear UserPublicRecord (apenas campos públicos)
                UserPublicRecord user = new UserPublicRecord(
                        UUID.fromString(rs.getString("user_id_full")),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("profile_image_url"),
                        rs.getTimestamp("user_created_at").toLocalDateTime(),
                        rs.getTimestamp("user_updated_at").toLocalDateTime(),
                        rs.getTimestamp("user_deleted_at") != null ?
                                rs.getTimestamp("user_deleted_at").toLocalDateTime() : null
                );

                // Mapear InstructorPublicRecord
                return new InstructorPublicRecord(
                        user,
                        rs.getBigDecimal("hourly_rate"),
                        rs.getString("bio"),
                        rs.getInt("years_experience"),
                        rs.getBoolean("is_verified"),
                        rs.getBigDecimal("average_rating"),
                        rs.getInt("total_reviews"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getTimestamp("deleted_at") != null ?
                                rs.getTimestamp("deleted_at").toLocalDateTime() : null,
                        rs.getString("city") // Novo campo
                );
            }
        };
    }
}