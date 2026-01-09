package com.automatch.portal.mapper;

import com.automatch.portal.model.StudentFavoriteModel;
import com.automatch.portal.records.StudentFavoriteRecord;

import java.time.LocalDateTime;
import java.util.UUID;

public class StudentFavoriteMapper {

    public static StudentFavoriteRecord toRecord(StudentFavoriteModel model) {
        if (model == null) return null;
        return new StudentFavoriteRecord(
                model.getId() != null ? model.getId().toString() : null,
                model.getStudentId() != null ? model.getStudentId().toString() : null,
                model.getInstructorId() != null ? model.getInstructorId().toString() : null,
                model.getCreatedAt()
        );
    }

    public static StudentFavoriteModel fromRecord(StudentFavoriteRecord record) {
        if (record == null) return null;
        StudentFavoriteModel model = new StudentFavoriteModel();

        if (record.id() != null) {
            model.setId(UUID.fromString(record.id()));
        }

        if (record.studentId() != null) {
            model.setStudentId(UUID.fromString(record.studentId()));
        }

        if (record.instructorId() != null) {
            model.setInstructorId(UUID.fromString(record.instructorId()));
        }

        model.setCreatedAt(record.createdAt());

        return model;
    }
}