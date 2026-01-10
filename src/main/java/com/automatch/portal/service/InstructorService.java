package com.automatch.portal.service;

import com.automatch.portal.dao.InstructorDAO;
import com.automatch.portal.dao.UserDAO;
import com.automatch.portal.mapper.InstructorMapper;
import com.automatch.portal.model.InstructorModel;
import com.automatch.portal.model.UserModel;
import com.automatch.portal.records.InstructorRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorDAO instructorDAO;
    private final UserDAO userDAO;

    @Transactional
    public InstructorRecord save(InstructorRecord instructorRecord) {
        validateInstructorRecord(instructorRecord);

        InstructorModel instructorModel = InstructorMapper.fromRecord(instructorRecord);
        UUID userUuid = instructorModel.getUser().getId();

        // Verificar se o usuário existe
        UserModel user = userDAO.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userUuid));

        // Verificar se já existe um instrutor com este user_id
        if (instructorDAO.existsById(userUuid)) {
            // Se já existe, retornar o existente
            return instructorDAO.findById(userUuid)
                    .map(InstructorMapper::toRecord)
                    .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        }

        // Validações específicas
        if (instructorModel.getHourlyRate() == null || instructorModel.getHourlyRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hourly rate must be greater than zero");
        }

        if (instructorModel.getYearsExperience() != null && instructorModel.getYearsExperience() < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }

        // Setar valores padrão
        instructorModel.setIsVerified(instructorRecord.isVerified() != null ? instructorRecord.isVerified() : false);
        instructorModel.setAverageRating(instructorRecord.averageRating() != null ?
                instructorRecord.averageRating() : BigDecimal.ZERO);
        instructorModel.setTotalReviews(instructorRecord.totalReviews() != null ?
                instructorRecord.totalReviews() : 0);
        instructorModel.setCreatedAt(instructorRecord.createdAt() != null ?
                instructorRecord.createdAt() : LocalDateTime.now());
        instructorModel.setUpdatedAt(LocalDateTime.now());

        InstructorModel savedModel = instructorDAO.save(instructorModel);
        return InstructorMapper.toRecord(savedModel);
    }

    public InstructorRecord getById(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return instructorDAO.findById(userUuid)
                .map(InstructorMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with user ID: " + userId));
    }

    public List<InstructorRecord> getActiveInstructors() {
        return instructorDAO.findActive().stream()
                .map(InstructorMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Object getInstructorStats(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return instructorDAO.getInstructorStats(userUuid);
    }

    @Transactional
    public void delete(String userId) {
        UUID userUuid = UUID.fromString(userId);
        InstructorModel instructor = instructorDAO.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with user ID: " + userId));

        if (instructor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Instructor is already deleted");
        }

        boolean deleted = instructorDAO.delete(userUuid);
        if (!deleted) {
            throw new RuntimeException("Failed to delete instructor with user ID: " + userId);
        }
    }

    @Transactional
    public InstructorRecord verifyInstructor(String userId) {
        UUID userUuid = UUID.fromString(userId);
        InstructorModel instructor = instructorDAO.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with user ID: " + userId));

        if (instructor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot verify a deleted instructor");
        }

        if (Boolean.TRUE.equals(instructor.getIsVerified())) {
            throw new IllegalArgumentException("Instructor is already verified");
        }

        instructor.setIsVerified(true);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorModel updatedModel = instructorDAO.save(instructor);
        return InstructorMapper.toRecord(updatedModel);
    }

    @Transactional
    public InstructorRecord unverifyInstructor(String userId) {
        UUID userUuid = UUID.fromString(userId);
        InstructorModel instructor = instructorDAO.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with user ID: " + userId));

        if (instructor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot unverify a deleted instructor");
        }

        if (Boolean.FALSE.equals(instructor.getIsVerified())) {
            throw new IllegalArgumentException("Instructor is already unverified");
        }

        instructor.setIsVerified(false);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorModel updatedModel = instructorDAO.save(instructor);
        return InstructorMapper.toRecord(updatedModel);
    }

    @Transactional
    public InstructorRecord updateRating(String userId, BigDecimal newRating) {
        UUID userUuid = UUID.fromString(userId);
        InstructorModel instructor = instructorDAO.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with user ID: " + userId));

        if (instructor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update rating of a deleted instructor");
        }

        if (newRating == null || newRating.compareTo(BigDecimal.ZERO) < 0 || newRating.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        instructor.setAverageRating(newRating);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorModel updatedModel = instructorDAO.save(instructor);
        return InstructorMapper.toRecord(updatedModel);
    }

    @Transactional
    public InstructorRecord addReview(String userId, Integer rating) {
        UUID userUuid = UUID.fromString(userId);
        InstructorModel instructor = instructorDAO.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with user ID: " + userId));

        if (instructor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot add review to a deleted instructor");
        }

        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Calcular nova média
        int newTotalReviews = instructor.getTotalReviews() + 1;
        BigDecimal currentTotal = instructor.getAverageRating().multiply(BigDecimal.valueOf(instructor.getTotalReviews()));
        BigDecimal newTotal = currentTotal.add(BigDecimal.valueOf(rating));
        BigDecimal newAverage = newTotal.divide(BigDecimal.valueOf(newTotalReviews), 2, RoundingMode.HALF_UP);

        instructor.setTotalReviews(newTotalReviews);
        instructor.setAverageRating(newAverage);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorModel updatedModel = instructorDAO.save(instructor);
        return InstructorMapper.toRecord(updatedModel);
    }

    @Transactional
    public InstructorRecord updateInstructor(String userId, InstructorRecord instructorRecord) {
        validateInstructorRecord(instructorRecord);

        UUID userUuid = UUID.fromString(userId);
        InstructorModel existingInstructor = instructorDAO.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with user ID: " + userId));

        if (existingInstructor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted instructor");
        }

        InstructorModel updatedModel = InstructorMapper.fromRecord(instructorRecord);
        updatedModel.getUser().setId(userUuid); // Garantir que o ID do usuário não mude
        updatedModel.setCreatedAt(existingInstructor.getCreatedAt());
        updatedModel.setUpdatedAt(LocalDateTime.now());

        // Preservar campos calculados se não fornecidos
        if (updatedModel.getAverageRating() == null) {
            updatedModel.setAverageRating(existingInstructor.getAverageRating());
        }

        if (updatedModel.getTotalReviews() == null) {
            updatedModel.setTotalReviews(existingInstructor.getTotalReviews());
        }

        if (updatedModel.getIsVerified() == null) {
            updatedModel.setIsVerified(existingInstructor.getIsVerified());
        }

        InstructorModel savedModel = instructorDAO.save(updatedModel);
        return InstructorMapper.toRecord(savedModel);
    }

    public List<Integer> getInstructorLicenseTypes(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return instructorDAO.findLicenseTypesByInstructor(userUuid);
    }

    @Transactional
    public void addLicenseType(String userId, Integer licenseTypeId) {
        UUID userUuid = UUID.fromString(userId);

        // Verificar se o instrutor existe
        if (!instructorDAO.existsById(userUuid)) {
            throw new IllegalArgumentException("Instructor not found with user ID: " + userId);
        }

        // Verificar se já tem esta licença
        if (instructorDAO.hasLicenseType(userUuid, licenseTypeId)) {
            throw new IllegalArgumentException("Instructor already has this license type");
        }

        instructorDAO.addLicenseType(userUuid, licenseTypeId);
    }

    @Transactional
    public void removeLicenseType(String userId, Integer licenseTypeId) {
        UUID userUuid = UUID.fromString(userId);
        int removed = instructorDAO.removeLicenseType(userUuid, licenseTypeId);

        if (removed == 0) {
            throw new IllegalArgumentException("License type not found for instructor");
        }
    }

    public List<Map<String, Object>> getInstructorVehicles(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return instructorDAO.findVehiclesByInstructor(userUuid);
    }

    public List<Map<String, Object>> getInstructorSchedule(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return instructorDAO.findScheduleByInstructor(userUuid);
    }

    public List<Map<String, Object>> getInstructorReviews(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return instructorDAO.findReviewsByInstructor(userUuid);
    }

    public List<InstructorRecord> getInstructorsByHourlyRateRange(BigDecimal minRate, BigDecimal maxRate) {
        if (minRate != null && maxRate != null && minRate.compareTo(maxRate) > 0) {
            throw new IllegalArgumentException("Minimum rate cannot be greater than maximum rate");
        }

        return instructorDAO.findByHourlyRateRange(minRate, maxRate).stream()
                .map(InstructorMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<InstructorRecord> getInstructorsByExperienceRange(Integer minYears, Integer maxYears) {
        if (minYears != null && maxYears != null && minYears > maxYears) {
            throw new IllegalArgumentException("Minimum years cannot be greater than maximum years");
        }

        return instructorDAO.findByExperienceRange(minYears, maxYears).stream()
                .map(InstructorMapper::toRecord)
                .collect(Collectors.toList());
    }

    private void validateInstructorRecord(InstructorRecord instructorRecord) {
        if (instructorRecord == null) {
            throw new IllegalArgumentException("Instructor record cannot be null");
        }

        if (instructorRecord.userId() == null || instructorRecord.userId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        try {
            UUID.fromString(instructorRecord.userId()); // Validar formato UUID
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format. Must be a valid UUID");
        }

        if (instructorRecord.hourlyRate() == null || instructorRecord.hourlyRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hourly rate must be greater than zero");
        }

        if (instructorRecord.yearsExperience() != null && instructorRecord.yearsExperience() < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }

        if (instructorRecord.averageRating() != null &&
                (instructorRecord.averageRating().compareTo(BigDecimal.ZERO) < 0 ||
                        instructorRecord.averageRating().compareTo(BigDecimal.valueOf(5)) > 0)) {
            throw new IllegalArgumentException("Average rating must be between 0 and 5");
        }

        if (instructorRecord.totalReviews() != null && instructorRecord.totalReviews() < 0) {
            throw new IllegalArgumentException("Total reviews cannot be negative");
        }
    }
}