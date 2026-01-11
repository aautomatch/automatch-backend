package com.automatch.portal.mapper;

import com.automatch.portal.enums.DayOfWeek;
import com.automatch.portal.model.InstructorAvailabilityModel;
import com.automatch.portal.records.InstructorAvailabilityRecord;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class InstructorAvailabilityMapper {

    public static InstructorAvailabilityRecord toRecord(InstructorAvailabilityModel model) {
        if (model == null) return null;
        return new InstructorAvailabilityRecord(
                model.getId() != null ? model.getId().toString() : null,
                model.getDayOfWeek(),
                model.getStartTime(),
                model.getEndTime(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static InstructorAvailabilityModel fromRecord(InstructorAvailabilityRecord record, UUID instructorId) {
        if (record == null) return null;

        InstructorAvailabilityModel model = new InstructorAvailabilityModel();

        if (record.id() != null && !record.id().isBlank()) {
            model.setId(UUID.fromString(record.id()));
        }

        model.setInstructorId(instructorId);
        model.setDayOfWeek(record.dayOfWeek());
        model.setStartTime(record.startTime());
        model.setEndTime(record.endTime());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());

        return model;
    }

    public static InstructorAvailabilityModel fromRecord(InstructorAvailabilityRecord record) {
        return fromRecord(record, null);
    }
}