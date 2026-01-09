package com.automatch.portal.service;

import com.automatch.portal.dao.StudentFavoriteDAO;
import com.automatch.portal.mapper.StudentFavoriteMapper;
import com.automatch.portal.model.StudentFavoriteModel;
import com.automatch.portal.records.StudentFavoriteRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentFavoriteService {

    private final StudentFavoriteDAO studentFavoriteDAO;

    @Transactional
    public StudentFavoriteRecord save(StudentFavoriteRecord favoriteRecord) {
        validateFavoriteRecord(favoriteRecord);

        StudentFavoriteModel favoriteModel = StudentFavoriteMapper.fromRecord(favoriteRecord);

        if (favoriteModel.getId() == null) {
            return addFavorite(favoriteModel);
        } else {
            throw new IllegalArgumentException("Cannot update existing favorites. Remove and add again.");
        }
    }

    private StudentFavoriteRecord addFavorite(StudentFavoriteModel favoriteModel) {
        // Verificar se já existe este favorito
        if (studentFavoriteDAO.existsByStudentAndInstructor(
                favoriteModel.getStudentId(),
                favoriteModel.getInstructorId())) {
            throw new IllegalArgumentException("Instructor is already in student's favorites");
        }

        // Verificar se o estudante está tentando se favoritar (se for instrutor também)
        if (favoriteModel.getStudentId().equals(favoriteModel.getInstructorId())) {
            throw new IllegalArgumentException("Student cannot favorite themselves");
        }

        favoriteModel.setCreatedAt(LocalDateTime.now());

        StudentFavoriteModel savedModel = studentFavoriteDAO.save(favoriteModel);
        return StudentFavoriteMapper.toRecord(savedModel);
    }

    public StudentFavoriteRecord getById(String id) {
        UUID uuid = UUID.fromString(id);
        return studentFavoriteDAO.findById(uuid)
                .map(StudentFavoriteMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Favorite not found with ID: " + id));
    }

    public List<StudentFavoriteRecord> getAll() {
        return studentFavoriteDAO.findAll().stream()
                .map(StudentFavoriteMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<StudentFavoriteRecord> getByStudent(String studentId) {
        UUID studentUuid = UUID.fromString(studentId);
        return studentFavoriteDAO.findByStudent(studentUuid).stream()
                .map(StudentFavoriteMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<StudentFavoriteRecord> getByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return studentFavoriteDAO.findByInstructor(instructorUuid).stream()
                .map(StudentFavoriteMapper::toRecord)
                .collect(Collectors.toList());
    }

    public boolean isFavorite(String studentId, String instructorId) {
        UUID studentUuid = UUID.fromString(studentId);
        UUID instructorUuid = UUID.fromString(instructorId);
        return studentFavoriteDAO.existsByStudentAndInstructor(studentUuid, instructorUuid);
    }

    public int countByStudent(String studentId) {
        UUID studentUuid = UUID.fromString(studentId);
        return studentFavoriteDAO.countByStudent(studentUuid);
    }

    public int countByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return studentFavoriteDAO.countByInstructor(instructorUuid);
    }

    public int getInstructorPopularity(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return studentFavoriteDAO.countByInstructor(instructorUuid);
    }

    public List<StudentFavoriteRecord> getRecentByStudent(String studentId, int limit) {
        if (limit <= 0 || limit > 50) {
            throw new IllegalArgumentException("Limit must be between 1 and 50");
        }

        UUID studentUuid = UUID.fromString(studentId);
        return studentFavoriteDAO.findRecentByStudent(studentUuid, limit).stream()
                .map(StudentFavoriteMapper::toRecord)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        StudentFavoriteModel favorite = studentFavoriteDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Favorite not found with ID: " + id));

        boolean deleted = studentFavoriteDAO.delete(uuid);
        if (!deleted) {
            throw new RuntimeException("Failed to delete favorite with ID: " + id);
        }
    }

    @Transactional
    public void deleteByStudentAndInstructor(String studentId, String instructorId) {
        UUID studentUuid = UUID.fromString(studentId);
        UUID instructorUuid = UUID.fromString(instructorId);

        int deleted = studentFavoriteDAO.deleteByStudentAndInstructor(studentUuid, instructorUuid);
        if (deleted == 0) {
            throw new IllegalArgumentException("Favorite not found for student ID: " + studentId + " and instructor ID: " + instructorId);
        }
    }

    @Transactional
    public void deleteAllByStudent(String studentId) {
        UUID studentUuid = UUID.fromString(studentId);
        int deletedCount = studentFavoriteDAO.deleteAllByStudent(studentUuid);

        if (deletedCount == 0) {
            throw new IllegalArgumentException("No favorites found for student ID: " + studentId);
        }
    }

    public List<String> getTopFavoriteInstructors(int limit) {
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        return studentFavoriteDAO.findTopFavoriteInstructors(limit).stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    public List<StudentFavoriteRecord> getByStudentAndInstructor(String studentId, String instructorId) {
        UUID studentUuid = UUID.fromString(studentId);
        UUID instructorUuid = UUID.fromString(instructorId);

        return studentFavoriteDAO.findByStudentAndInstructor(studentUuid, instructorUuid).stream()
                .map(StudentFavoriteMapper::toRecord)
                .collect(Collectors.toList());
    }

    public int countAllFavorites() {
        return studentFavoriteDAO.countAll();
    }

    public List<StudentFavoriteRecord> getRecentFavorites(int limit) {
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        return studentFavoriteDAO.findRecent(limit).stream()
                .map(StudentFavoriteMapper::toRecord)
                .collect(Collectors.toList());
    }

    private void validateFavoriteRecord(StudentFavoriteRecord favoriteRecord) {
        if (favoriteRecord == null) {
            throw new IllegalArgumentException("Favorite record cannot be null");
        }

        if (favoriteRecord.studentId() == null || favoriteRecord.studentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }

        if (favoriteRecord.instructorId() == null || favoriteRecord.instructorId().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID is required");
        }
    }
}