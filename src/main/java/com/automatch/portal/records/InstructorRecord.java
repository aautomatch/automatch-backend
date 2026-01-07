package com.automatch.portal.records;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InstructorRecord(
        UserRecord user,
        BigDecimal hourlyRate,
        String bio,
        Integer yearsExperience,
        Boolean isVerified,
        BigDecimal averageRating,
        Integer totalReviews,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
