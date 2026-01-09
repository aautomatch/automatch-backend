package com.automatch.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleModel {
    private UUID id;
    private UUID instructorId;
    private String licensePlate;
    private String model;
    private String brand;
    private Integer year;
    private String color;
    private String vehicleImageUrl;
    private Integer transmissionTypeId;
    private Integer categoryId;
    private Boolean hasDualControls = true;
    private Boolean hasAirConditioning = true;
    private Boolean isApproved = false;

    private Boolean isAvailable = true;
    private LocalDate lastMaintenanceDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
