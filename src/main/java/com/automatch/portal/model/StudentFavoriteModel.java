package com.automatch.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentFavoriteModel {
    private UUID id;
    private UserModel student;
    private InstructorModel instructor;

    private LocalDateTime createdAt;
}
