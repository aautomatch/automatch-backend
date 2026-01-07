package com.automatch.portal.records;

import com.automatch.portal.enums.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserRecord(
        UUID id,
        String fullName,
        String email,
        String password,
        String phone,
        UserRole role,
        Boolean isActive,
        String profileImageUrl,
        AddressRecord address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}