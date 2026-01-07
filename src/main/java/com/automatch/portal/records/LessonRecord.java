package com.automatch.portal.records;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LessonRecord(
        String id,
        InstructorRecord instructor,
        UserRecord student,
        VehicleRecord vehicle,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        ClassifierRecord status,
        AddressRecord address,
        BigDecimal price,
        ClassifierRecord paymentStatus,
        ClassifierRecord paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt,
        LocalDateTime deletedAt
) {}
