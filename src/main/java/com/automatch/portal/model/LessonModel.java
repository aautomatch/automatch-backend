package com.automatch.portal.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter

public class LessonModel {

    private UUID id;

    private UUID instructorId;

    private UUID studentId;

    private UUID vehicleId;

    private LocalDateTime scheduledAt;

    private Integer durationMinutes;

    private Integer statusId;

    private UUID addressId;

    private BigDecimal price;

    private Integer paymentStatusId;

    private Integer paymentMethodId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    private LocalDateTime deletedAt;
}