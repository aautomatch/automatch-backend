package com.automatch.portal.records;

import com.automatch.portal.enums.DayOfWeek;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record InstructorAvailabilityRecord(
        String id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
