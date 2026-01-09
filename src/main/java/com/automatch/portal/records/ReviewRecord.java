package com.automatch.portal.records;

import java.time.LocalDateTime;

public record ReviewRecord(
        String id,
        String lessonId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
