package com.automatch.portal.mapper;

import com.automatch.portal.model.StudentFavoriteModel;
import com.automatch.portal.records.StudentFavoriteRecord;

import java.util.UUID;

public class StudentFavoriteMapper {

    public static StudentFavoriteRecord toRecord(StudentFavoriteModel model) {
        if (model == null) return null;
        return new StudentFavoriteRecord(
                model.getId().toString(),
                UserMapper.toRecord(model.getStudent()),
                InstructorMapper.toRecord(model.getInstructor()),
                model.getCreatedAt()
        );
    }

    public static StudentFavoriteModel fromRecord(StudentFavoriteRecord record) {
        if (record == null) return null;
        StudentFavoriteModel model = new StudentFavoriteModel();
        model.setId(UUID.fromString(record.id()));
        model.setStudent(UserMapper.fromRecord(record.student()));
        model.setInstructor(InstructorMapper.fromRecord(record.instructor()));
        model.setCreatedAt(record.createdAt());
        return model;
    }
}
