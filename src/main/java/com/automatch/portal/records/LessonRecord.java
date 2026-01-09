package com.automatch.portal.records;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LessonRecord(
        String id,
        String instructorId,          // Alterado: de InstructorRecord para String (ID)
        String studentId,             // Alterado: de UserRecord para String (ID)
        String vehicleId,             // Alterado: de VehicleRecord para String (ID)
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        Integer statusId,             // Alterado: de ClassifierRecord para Integer (ID)
        String addressId,             // Alterado: de AddressRecord para String (ID)
        BigDecimal price,
        Integer paymentStatusId,      // Alterado: de ClassifierRecord para Integer (ID)
        Integer paymentMethodId,      // Alterado: de ClassifierRecord para Integer (ID)
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt,
        LocalDateTime deletedAt
) {}