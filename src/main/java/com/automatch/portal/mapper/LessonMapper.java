package com.automatch.portal.mapper;

import com.automatch.portal.model.LessonModel;
import com.automatch.portal.records.LessonRecord;

import java.util.UUID;

public class LessonMapper {

    public static LessonRecord toRecord(LessonModel model) {
        if (model == null) return null;

        return new LessonRecord(
                model.getId() != null ? model.getId().toString() : null,
                model.getInstructorId() != null ? model.getInstructorId().toString() : null,
                model.getStudentId() != null ? model.getStudentId().toString() : null,
                model.getVehicleId() != null ? model.getVehicleId().toString() : null,
                model.getScheduledAt(),
                model.getDurationMinutes(),
                model.getStatusId(),
                model.getAddressId() != null ? model.getAddressId().toString() : null,
                model.getPrice(),
                model.getPaymentStatusId(),
                model.getPaymentMethodId(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getCompletedAt(),
                model.getDeletedAt()
        );
    }

    public static LessonModel fromRecord(LessonRecord record) {
        if (record == null) return null;

        LessonModel model = new LessonModel();

        if (record.id() != null) {
            model.setId(UUID.fromString(record.id()));
        }

        if (record.instructorId() != null) {
            model.setInstructorId(UUID.fromString(record.instructorId()));
        }

        if (record.studentId() != null) {
            model.setStudentId(UUID.fromString(record.studentId()));
        }

        if (record.vehicleId() != null) {
            model.setVehicleId(UUID.fromString(record.vehicleId()));
        }

        model.setScheduledAt(record.scheduledAt());
        model.setDurationMinutes(record.durationMinutes());
        model.setStatusId(record.statusId());

        if (record.addressId() != null) {
            model.setAddressId(UUID.fromString(record.addressId()));
        }

        model.setPrice(record.price());
        model.setPaymentStatusId(record.paymentStatusId());
        model.setPaymentMethodId(record.paymentMethodId());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setCompletedAt(record.completedAt());
        model.setDeletedAt(record.deletedAt());

        return model;
    }
}