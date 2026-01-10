package com.automatch.portal.mapper;

import com.automatch.portal.enums.UserRole;
import com.automatch.portal.model.InstructorModel;
import com.automatch.portal.model.UserModel;
import com.automatch.portal.records.InstructorRecord;
import com.automatch.portal.records.UserRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class InstructorMapper {

    public static InstructorRecord toRecord(InstructorModel model) {
        if (model == null) return null;
        return new InstructorRecord(
                UserMapper.toRecord(model.getUser()), // Mantém para compatibilidade com construtor antigo
                model.getHourlyRate(),
                model.getBio(),
                model.getYearsExperience(),
                model.getIsVerified(),
                model.getAverageRating(),
                model.getTotalReviews(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static InstructorModel fromRecord(InstructorRecord record) {
        if (record == null) return null;

        InstructorModel model = new InstructorModel();

        // Criar UserModel básico apenas com ID
        UserModel user = new UserModel();
        try {
            user.setId(UUID.fromString(record.userId()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format: " + record.userId());
        }
        model.setUser(user);

        model.setHourlyRate(record.hourlyRate());
        model.setBio(record.bio());
        model.setYearsExperience(record.yearsExperience());
        model.setIsVerified(record.isVerified());
        model.setAverageRating(record.averageRating());
        model.setTotalReviews(record.totalReviews());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());

        return model;
    }

    public static RowMapper<InstructorModel> getRowMapper() {
        return new RowMapper<InstructorModel>() {
            @Override
            public InstructorModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                InstructorModel instructor = new InstructorModel();

                // Mapear atributos básicos do instrutor
                instructor.setHourlyRate(rs.getBigDecimal("hourly_rate"));
                instructor.setBio(rs.getString("bio"));
                instructor.setYearsExperience(rs.getInt("years_experience"));
                instructor.setIsVerified(rs.getBoolean("is_verified"));
                instructor.setAverageRating(rs.getBigDecimal("average_rating"));
                instructor.setTotalReviews(rs.getInt("total_reviews"));
                instructor.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                instructor.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                if (rs.getTimestamp("deleted_at") != null) {
                    instructor.setDeletedAt(rs.getTimestamp("deleted_at").toLocalDateTime());
                }

                // Mapear o usuário
                UserModel user = new UserModel();
                user.setId(UUID.fromString(rs.getString("user_id_full")));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));

                String roleStr = rs.getString("role");
                if (roleStr != null) {
                    try {
                        user.setRole(UserRole.valueOf(roleStr));
                    } catch (IllegalArgumentException e) {
                        user.setRole(UserRole.STUDENT);
                    }
                }

                user.setIsActive(rs.getBoolean("is_active"));
                user.setProfileImageUrl(rs.getString("profile_image_url"));
                user.setCreatedAt(rs.getTimestamp("user_created_at").toLocalDateTime());

                if (rs.getTimestamp("last_loggin") != null) {
                    user.setLastLoggin(rs.getTimestamp("last_loggin").toLocalDateTime());
                }

                user.setUpdatedAt(rs.getTimestamp("user_updated_at").toLocalDateTime());

                if (rs.getTimestamp("user_deleted_at") != null) {
                    user.setDeletedAt(rs.getTimestamp("user_deleted_at").toLocalDateTime());
                }

                instructor.setUser(user);
                return instructor;
            }
        };
    }
}