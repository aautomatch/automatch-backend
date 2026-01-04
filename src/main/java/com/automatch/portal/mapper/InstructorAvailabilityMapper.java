package com.automatch.portal.mapper;

import com.automatch.portal.model.InstructorAvailabilityModel;
import com.automatch.portal.records.InstructorAvailabilityRecord;

import java.util.UUID;

public class InstructorAvailabilityMapper {

    public static InstructorAvailabilityRecord toRecord(InstructorAvailabilityModel model) {
        if (model == null) return null;
        return new InstructorAvailabilityRecord(
                model.getId().toString(),
                InstructorMapper.toRecord(model.getInstructor()),
                model.getDayOfWeek(),
                model.getStartTime(),
                model.getEndTime(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static InstructorAvailabilityModel fromRecord(InstructorAvailabilityRecord record) {
        if (record == null) return null;
        InstructorAvailabilityModel model = new InstructorAvailabilityModel();
        model.setId(UUID.fromString(record.id()));
        model.setInstructor(InstructorMapper.fromRecord(record.instructor()));
        model.setDayOfWeek(record.dayOfWeek());
        model.setStartTime(record.startTime());
        model.setEndTime(record.endTime());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());
        return model;
    }
}
