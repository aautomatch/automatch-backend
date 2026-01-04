package com.automatch.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonModel {
    private UUID id;
    private InstructorModel instructor;
    private UserModel student;
    private VehicleModel vehicle;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private ClassifierModel status;
    private AddressModel address;
    private BigDecimal price;
    private ClassifierModel paymentStatus;
    private ClassifierModel paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime deletedAt;
}
