package com.automatch.portal.records;

import java.time.LocalDateTime;

public record StudentFavoriteRecord(
        String id,
        UserRecord student,
        InstructorRecord instructor,
        LocalDateTime createdAt
) {}
