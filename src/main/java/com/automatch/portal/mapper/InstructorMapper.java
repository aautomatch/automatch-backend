package com.automatch.portal.mapper;

import com.automatch.portal.model.InstructorModel;
import com.automatch.portal.records.InstructorRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class InstructorMapper {

    public static InstructorRecord toRecord(InstructorModel model) {
        if (model == null) return null;
        return new InstructorRecord(
                UserMapper.toRecord(model.getUser()),
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
        model.setUser(UserMapper.fromRecord(record.user()));
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

                // Mapear atributos básicos
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

                return instructor;
            }
        };
    }
}