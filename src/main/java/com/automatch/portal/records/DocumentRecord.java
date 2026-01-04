package com.automatch.portal.records;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.example.records.UserRecord;

public record DocumentRecord(
        String id,
        UserRecord user,
        ClassifierRecord documentType,
        String documentNumber,
        String documentImageUrl,
        LocalDate issueDate,
        LocalDate expiryDate,
        Boolean isVerified,
        UserRecord verifiedBy,
        LocalDateTime verifiedAt,
        String verificationNotes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
