package com.automatch.portal.records;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRecord(
        String id,
        LessonRecord lesson,
        BigDecimal amount,
        ClassifierRecord status,
        ClassifierRecord paymentMethod,
        String transactionId,
        LocalDateTime paidAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
