package com.automatch.portal.records;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentRecord(
        String id,
        String userId,
        Integer documentTypeId,
        String documentNumber,
        String documentImageUrl,
        LocalDate issueDate,
        LocalDate expiryDate,
        Boolean isVerified,
        String verifiedByUserId,
        LocalDateTime verifiedAt,
        String verificationNotes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
