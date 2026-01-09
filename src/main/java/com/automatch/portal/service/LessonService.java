package com.automatch.portal.service;

import com.automatch.portal.dao.LessonDAO;
import com.automatch.portal.mapper.LessonMapper;
import com.automatch.portal.model.LessonModel;
import com.automatch.portal.records.LessonRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonDAO lessonDAO;

    @Transactional
    public LessonRecord save(LessonRecord lessonRecord) {
        validateLessonRecord(lessonRecord);

        LessonModel lessonModel = LessonMapper.fromRecord(lessonRecord);

        if (lessonModel.getId() == null) {
            return createLesson(lessonModel);
        } else {
            return updateLesson(lessonRecord.id(), lessonRecord);
        }
    }

    private LessonRecord createLesson(LessonModel lessonModel) {
        // Verificar conflitos de horário
        if (hasScheduleConflict(lessonModel)) {
            throw new IllegalArgumentException("Schedule conflict detected for instructor");
        }

        // Validar que a data/hora não está no passado
        if (lessonModel.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule lesson in the past");
        }

        // Validar duração mínima
        if (lessonModel.getDurationMinutes() == null || lessonModel.getDurationMinutes() < 30) {
            throw new IllegalArgumentException("Lesson duration must be at least 30 minutes");
        }

        lessonModel.setCreatedAt(LocalDateTime.now());
        lessonModel.setUpdatedAt(LocalDateTime.now());

        // Definir valores padrão se não fornecidos
        if (lessonModel.getStatusId() == null) {
            lessonModel.setStatusId(1); // Status: Agendada
        }

        if (lessonModel.getPaymentStatusId() == null) {
            lessonModel.setPaymentStatusId(1); // Status de pagamento: Pendente
        }

        LessonModel savedModel = lessonDAO.save(lessonModel);
        return LessonMapper.toRecord(savedModel);
    }

    public LessonRecord getById(String id) {
        UUID uuid = UUID.fromString(id);
        return lessonDAO.findById(uuid)
                .map(LessonMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + id));
    }

    public List<LessonRecord> getAll() {
        return lessonDAO.findAll().stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return lessonDAO.findByInstructor(instructorUuid).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getByStudent(String studentId) {
        UUID studentUuid = UUID.fromString(studentId);
        return lessonDAO.findByStudent(studentUuid).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getByVehicle(String vehicleId) {
        UUID vehicleUuid = UUID.fromString(vehicleId);
        return lessonDAO.findByVehicle(vehicleUuid).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getByStatus(Integer statusId) {
        return lessonDAO.findByStatus(statusId).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getByPaymentStatus(Integer paymentStatusId) {
        return lessonDAO.findByPaymentStatus(paymentStatusId).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getUpcomingLessons() {
        return lessonDAO.findUpcoming().stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getCompletedLessons() {
        return lessonDAO.findCompleted().stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return lessonDAO.findByDateRange(startOfDay, endOfDay).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return lessonDAO.findByDateRange(startDate, endDate).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getInstructorLessonsByDate(String instructorId, LocalDate date) {
        UUID instructorUuid = UUID.fromString(instructorId);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return lessonDAO.findInstructorLessonsByDate(instructorUuid, startOfDay, endOfDay).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<LessonRecord> getStudentLessonsByDate(String studentId, LocalDate date) {
        UUID studentUuid = UUID.fromString(studentId);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return lessonDAO.findStudentLessonsByDate(studentUuid, startOfDay, endOfDay).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        LessonModel lesson = lessonDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + id));

        if (lesson.getDeletedAt() != null) {
            throw new IllegalArgumentException("Lesson is already deleted");
        }

        // Não permitir excluir aulas que já foram completadas
        if (lesson.getCompletedAt() != null) {
            throw new IllegalArgumentException("Cannot delete a completed lesson");
        }

        boolean deleted = lessonDAO.delete(uuid);
        if (!deleted) {
            throw new RuntimeException("Failed to delete lesson with ID: " + id);
        }
    }

    @Transactional
    public LessonRecord completeLesson(String id) {
        UUID uuid = UUID.fromString(id);
        LessonModel lesson = lessonDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + id));

        if (lesson.getCompletedAt() != null) {
            throw new IllegalArgumentException("Lesson is already completed");
        }

        if (lesson.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot complete a deleted lesson");
        }

        // Verificar se a aula já deveria ter acontecido
        if (lesson.getScheduledAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot complete a lesson that hasn't happened yet");
        }

        lesson.setCompletedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());

        LessonModel updatedModel = lessonDAO.save(lesson);
        return LessonMapper.toRecord(updatedModel);
    }

    @Transactional
    public LessonRecord cancelLesson(String id) {
        UUID uuid = UUID.fromString(id);
        LessonModel lesson = lessonDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + id));

        if (lesson.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot cancel a deleted lesson");
        }

        if (lesson.getCompletedAt() != null) {
            throw new IllegalArgumentException("Cannot cancel a completed lesson");
        }

        // Verificar se pode ser cancelado (ex: não pode cancelar após início)
        if (lesson.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot cancel a lesson that has already started");
        }

        // Alterar status para cancelado (assumindo que 3 é o código para cancelado)
        lesson.setStatusId(3);
        lesson.setUpdatedAt(LocalDateTime.now());

        LessonModel updatedModel = lessonDAO.save(lesson);
        return LessonMapper.toRecord(updatedModel);
    }

    @Transactional
    public LessonRecord rescheduleLesson(String id, LocalDateTime newDateTime) {
        UUID uuid = UUID.fromString(id);
        LessonModel lesson = lessonDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + id));

        if (lesson.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot reschedule a deleted lesson");
        }

        if (lesson.getCompletedAt() != null) {
            throw new IllegalArgumentException("Cannot reschedule a completed lesson");
        }

        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot reschedule to a past date/time");
        }

        // Verificar conflito de horário para o novo horário
        LocalDateTime newEndTime = newDateTime.plusMinutes(lesson.getDurationMinutes());
        if (lessonDAO.hasScheduleConflict(lesson.getInstructorId(), newDateTime, newEndTime, uuid)) {
            throw new IllegalArgumentException("Schedule conflict detected for the new time");
        }

        lesson.setScheduledAt(newDateTime);
        lesson.setUpdatedAt(LocalDateTime.now());

        LessonModel updatedModel = lessonDAO.save(lesson);
        return LessonMapper.toRecord(updatedModel);
    }

    @Transactional
    public LessonRecord updatePaymentStatus(String id, Integer paymentStatusId) {
        UUID uuid = UUID.fromString(id);
        LessonModel lesson = lessonDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + id));

        if (lesson.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update payment status of a deleted lesson");
        }

        lesson.setPaymentStatusId(paymentStatusId);
        lesson.setUpdatedAt(LocalDateTime.now());

        LessonModel updatedModel = lessonDAO.save(lesson);
        return LessonMapper.toRecord(updatedModel);
    }

    @Transactional
    public LessonRecord updateStatus(String id, Integer statusId) {
        UUID uuid = UUID.fromString(id);
        LessonModel lesson = lessonDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + id));

        if (lesson.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update status of a deleted lesson");
        }

        lesson.setStatusId(statusId);
        lesson.setUpdatedAt(LocalDateTime.now());

        LessonModel updatedModel = lessonDAO.save(lesson);
        return LessonMapper.toRecord(updatedModel);
    }

    @Transactional
    public LessonRecord updateLesson(String id, LessonRecord lessonRecord) {
        validateLessonRecord(lessonRecord);

        UUID uuid = UUID.fromString(id);
        LessonModel existingLesson = lessonDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + id));

        if (existingLesson.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted lesson");
        }

        // Não permitir alterar aulas já completadas
        if (existingLesson.getCompletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a completed lesson");
        }

        LessonModel updatedModel = LessonMapper.fromRecord(lessonRecord);
        updatedModel.setId(uuid);
        updatedModel.setCreatedAt(existingLesson.getCreatedAt());
        updatedModel.setUpdatedAt(LocalDateTime.now());

        // Preservar campos que não devem ser alterados diretamente
        if (updatedModel.getCompletedAt() == null) {
            updatedModel.setCompletedAt(existingLesson.getCompletedAt());
        }

        // Preservar IDs que não devem ser alterados se não foram fornecidos
        if (updatedModel.getInstructorId() == null) {
            updatedModel.setInstructorId(existingLesson.getInstructorId());
        }

        if (updatedModel.getStudentId() == null) {
            updatedModel.setStudentId(existingLesson.getStudentId());
        }

        if (updatedModel.getVehicleId() == null) {
            updatedModel.setVehicleId(existingLesson.getVehicleId());
        }

        if (updatedModel.getAddressId() == null) {
            updatedModel.setAddressId(existingLesson.getAddressId());
        }

        if (updatedModel.getStatusId() == null) {
            updatedModel.setStatusId(existingLesson.getStatusId());
        }

        if (updatedModel.getPaymentStatusId() == null) {
            updatedModel.setPaymentStatusId(existingLesson.getPaymentStatusId());
        }

        if (updatedModel.getPaymentMethodId() == null) {
            updatedModel.setPaymentMethodId(existingLesson.getPaymentMethodId());
        }

        if (updatedModel.getPrice() == null) {
            updatedModel.setPrice(existingLesson.getPrice());
        }

        // Verificar conflito de horário se a data/hora mudou
        if (!existingLesson.getScheduledAt().equals(updatedModel.getScheduledAt()) ||
                !existingLesson.getDurationMinutes().equals(updatedModel.getDurationMinutes())) {
            if (hasScheduleConflict(updatedModel)) {
                throw new IllegalArgumentException("Schedule conflict detected for instructor");
            }
        }

        LessonModel savedModel = lessonDAO.save(updatedModel);
        return LessonMapper.toRecord(savedModel);
    }

    public Map<String, Object> getInstructorStats(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return lessonDAO.getInstructorStats(instructorUuid);
    }

    public Map<String, Object> getStudentStats(String studentId) {
        UUID studentUuid = UUID.fromString(studentId);
        return lessonDAO.getStudentStats(studentUuid);
    }

    public BigDecimal getInstructorRevenue(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return lessonDAO.getInstructorRevenue(instructorUuid);
    }

    public List<LessonRecord> getInstructorPendingPayment(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return lessonDAO.findInstructorPendingPayment(instructorUuid).stream()
                .map(LessonMapper::toRecord)
                .collect(Collectors.toList());
    }

    public boolean checkScheduleConflict(String instructorId, LocalDateTime startTime, Integer durationMinutes) {
        UUID instructorUuid = UUID.fromString(instructorId);
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        return lessonDAO.hasScheduleConflict(instructorUuid, startTime, endTime, null);
    }

    private boolean hasScheduleConflict(LessonModel lesson) {
        LocalDateTime startTime = lesson.getScheduledAt();
        LocalDateTime endTime = startTime.plusMinutes(lesson.getDurationMinutes());
        return lessonDAO.hasScheduleConflict(lesson.getInstructorId(), startTime, endTime, lesson.getId());
    }

    private void validateLessonRecord(LessonRecord lessonRecord) {
        if (lessonRecord == null) {
            throw new IllegalArgumentException("Lesson record cannot be null");
        }

        if (lessonRecord.scheduledAt() == null) {
            throw new IllegalArgumentException("Scheduled date/time is required");
        }

        if (lessonRecord.durationMinutes() == null || lessonRecord.durationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number");
        }

        if (lessonRecord.instructorId() == null || lessonRecord.instructorId().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID is required");
        }

        if (lessonRecord.studentId() == null || lessonRecord.studentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }

        if (lessonRecord.price() == null || lessonRecord.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
    }
}