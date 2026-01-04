package com.automatch.portal.records;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddressRecord(
        UUID id,
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
