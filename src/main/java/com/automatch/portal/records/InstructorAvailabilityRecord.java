package com.automatch.portal.records;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record InstructorAvailabilityRecord(
        String id,
        InstructorRecord instructor,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
