package com.automatch.portal.records;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        UserRecord user
) {}