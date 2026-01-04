package com.automatch.portal.mapper;

import com.automatch.portal.model.LessonModel;
import com.automatch.portal.records.LessonRecord;

import java.util.UUID;

public class LessonMapper {

    public static LessonRecord toRecord(LessonModel model) {
        if (model == null) return null;
        return new LessonRecord(
                model.getId().toString(),
                InstructorMapper.toRecord(model.getInstructor()),
                UserMapper.toRecord(model.getStudent()),
                VehicleMapper.toRecord(model.getVehicle()),
                model.getScheduledAt(),
                model.getDurationMinutes(),
                ClassifierMapper.toRecord(model.getStatus()),
                AddressMapper.toRecord(model.getAddress()),
                model.getPrice(),
                ClassifierMapper.toRecord(model.getPaymentStatus()),
                ClassifierMapper.toRecord(model.getPaymentMethod()),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getCompletedAt(),
                model.getDeletedAt()
        );
    }

    public static LessonModel fromRecord(LessonRecord record) {
        if (record == null) return null;
        LessonModel model = new LessonModel();
        model.setId(UUID.fromString(record.id()));
        model.setInstructor(InstructorMapper.fromRecord(record.instructor()));
        model.setStudent(UserMapper.fromRecord(record.student()));
        model.setVehicle(VehicleMapper.fromRecord(record.vehicle()));
        model.setScheduledAt(record.scheduledAt());
        model.setDurationMinutes(record.durationMinutes());
        model.setStatus(ClassifierMapper.fromRecord(record.status()));
        model.setAddress(AddressMapper.fromRecord(record.address()));
        model.setPrice(record.price());
        model.setPaymentStatus(ClassifierMapper.fromRecord(record.paymentStatus()));
        model.setPaymentMethod(ClassifierMapper.fromRecord(record.paymentMethod()));
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setCompletedAt(record.completedAt());
        model.setDeletedAt(record.deletedAt());
        return model;
    }
}
