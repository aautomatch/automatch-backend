package com.automatch.portal.records;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserPublicRecord(
        UUID id,
        String fullName,
        String email,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}