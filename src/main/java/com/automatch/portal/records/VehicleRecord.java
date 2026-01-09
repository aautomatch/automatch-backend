package com.automatch.portal.records;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VehicleRecord(
        String id,
        String instructorId,  // Alterado: de InstructorRecord para String (ID do instructor)
        String licensePlate,
        String model,
        String brand,
        Integer year,
        String color,
        String vehicleImageUrl,
        Integer transmissionTypeId,  // Alterado: de ClassifierRecord para Integer (ID)
        Integer categoryId,          // Alterado: de ClassifierRecord para Integer (ID)
        Boolean hasDualControls,
        Boolean hasAirConditioning,
        Boolean isApproved,
        Boolean isAvailable,
        LocalDate lastMaintenanceDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}