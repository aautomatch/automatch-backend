package com.automatch.portal.records;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VehicleRecord(
        String id,
        InstructorRecord instructor,
        String licensePlate,
        String model,
        String brand,
        Integer year,
        String color,
        String vehicleImageUrl,
        ClassifierRecord transmissionType,
        ClassifierRecord category,
        Boolean hasDualControls,
        Boolean hasAirConditioning,
        Boolean isApproved,
        Boolean isAvailable,
        LocalDate lastMaintenanceDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
