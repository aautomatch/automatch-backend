package com.automatch.portal.records;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InstructorRecord(
        String userId, // Apenas o ID do usuário como String (UUID)
        BigDecimal hourlyRate,
        String bio,
        Integer yearsExperience,
        Boolean isVerified,
        BigDecimal averageRating,
        Integer totalReviews,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    // Construtor adicional para compatibilidade
    public InstructorRecord(UserRecord user, BigDecimal hourlyRate, String bio,
                            Integer yearsExperience, Boolean isVerified,
                            BigDecimal averageRating, Integer totalReviews,
                            LocalDateTime createdAt, LocalDateTime updatedAt,
                            LocalDateTime deletedAt) {
        this(user != null ? user.id().toString() : null,
                hourlyRate, bio, yearsExperience, isVerified,
                averageRating, totalReviews, createdAt, updatedAt, deletedAt);
    }

    // Método para verificar se tem user completo
    public boolean hasFullUser() {
        return false; // Agora sempre retorna false pois só tem userId
    }
}