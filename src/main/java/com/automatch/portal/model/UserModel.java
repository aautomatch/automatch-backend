package com.automatch.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;

    private ClassifierModel userType;

    private Boolean isActive;
    private String profileImageUrl;
    private AddressModel address;

    private LocalDateTime createdAt;
    private LocalDateTime lastLoggin;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
