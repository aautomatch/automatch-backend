package com.automatch.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorAvailabilityModel {
    private UUID id;
    private UUID instructorId;

    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
