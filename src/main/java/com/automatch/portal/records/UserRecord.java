package com.automatch.portal.records;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRecord(
        UUID id,
        String fullName,
        String email,
        String phone,
        ClassifierRecord userType,
        Boolean isActive,
        String profileImageUrl,
        AddressRecord address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
