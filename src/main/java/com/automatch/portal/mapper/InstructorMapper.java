package com.automatch.portal.mapper;

import com.automatch.portal.model.InstructorModel;
import com.automatch.portal.records.InstructorRecord;

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
}
