package com.automatch.portal.mapper;

import com.automatch.portal.model.VehicleModel;
import com.automatch.portal.records.VehicleRecord;

import java.util.UUID;

public class VehicleMapper {

    public static VehicleRecord toRecord(VehicleModel model) {
        if (model == null) return null;
        return new VehicleRecord(
                model.getId().toString(),
                InstructorMapper.toRecord(model.getInstructor()),
                model.getLicensePlate(),
                model.getModel(),
                model.getBrand(),
                model.getYear(),
                model.getColor(),
                model.getVehicleImageUrl(),
                ClassifierMapper.toRecord(model.getTransmissionType()),
                ClassifierMapper.toRecord(model.getCategory()),
                model.getHasDualControls(),
                model.getHasAirConditioning(),
                model.getIsApproved(),
                model.getIsAvailable(),
                model.getLastMaintenanceDate(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static VehicleModel fromRecord(VehicleRecord record) {
        if (record == null) return null;
        VehicleModel model = new VehicleModel();
        model.setId(UUID.fromString(record.id()));
        model.setInstructor(InstructorMapper.fromRecord(record.instructor()));
        model.setLicensePlate(record.licensePlate());
        model.setModel(record.model());
        model.setBrand(record.brand());
        model.setYear(record.year());
        model.setColor(record.color());
        model.setVehicleImageUrl(record.vehicleImageUrl());
        model.setTransmissionType(ClassifierMapper.fromRecord(record.transmissionType()));
        model.setCategory(ClassifierMapper.fromRecord(record.category()));
        model.setHasDualControls(record.hasDualControls());
        model.setHasAirConditioning(record.hasAirConditioning());
        model.setIsApproved(record.isApproved());
        model.setIsAvailable(record.isAvailable());
        model.setLastMaintenanceDate(record.lastMaintenanceDate());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());
        return model;
    }
}
