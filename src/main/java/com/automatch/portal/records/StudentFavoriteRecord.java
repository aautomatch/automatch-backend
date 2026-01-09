package com.automatch.portal.records;

import java.time.LocalDateTime;

public record StudentFavoriteRecord(
        String id,
        String studentId,
        String instructorId,
        LocalDateTime createdAt
) {}
