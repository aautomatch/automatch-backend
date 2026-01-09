package com.automatch.portal.mapper;

import com.automatch.portal.model.VehicleModel;
import com.automatch.portal.records.VehicleRecord;

import java.util.UUID;

public class VehicleMapper {

    public static VehicleRecord toRecord(VehicleModel model) {
        if (model == null) return null;

        return new VehicleRecord(
                model.getId() != null ? model.getId().toString() : null,
                model.getInstructorId() != null ? model.getInstructorId().toString() : null,
                model.getLicensePlate(),
                model.getModel(),
                model.getBrand(),
                model.getYear(),
                model.getColor(),
                model.getVehicleImageUrl(),
                model.getTransmissionTypeId(),
                model.getCategoryId(),
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

        if (record.id() != null) {
            model.setId(UUID.fromString(record.id()));
        }

        if (record.instructorId() != null) {
            model.setInstructorId(UUID.fromString(record.instructorId()));
        }

        model.setLicensePlate(record.licensePlate());
        model.setModel(record.model());
        model.setBrand(record.brand());
        model.setYear(record.year());
        model.setColor(record.color());
        model.setVehicleImageUrl(record.vehicleImageUrl());
        model.setTransmissionTypeId(record.transmissionTypeId());
        model.setCategoryId(record.categoryId());
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