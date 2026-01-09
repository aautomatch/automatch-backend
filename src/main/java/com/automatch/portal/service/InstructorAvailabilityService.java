package com.automatch.portal.service;

import com.automatch.portal.dao.InstructorAvailabilityDAO;
import com.automatch.portal.mapper.InstructorAvailabilityMapper;
import com.automatch.portal.model.InstructorAvailabilityModel;
import com.automatch.portal.records.InstructorAvailabilityRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorAvailabilityService {

    private final InstructorAvailabilityDAO availabilityDAO;

    @Transactional
    public InstructorAvailabilityRecord save(InstructorAvailabilityRecord availabilityRecord) {
        validateAvailabilityRecord(availabilityRecord);

        InstructorAvailabilityModel availabilityModel = InstructorAvailabilityMapper.fromRecord(availabilityRecord);

        if (availabilityModel.getId() == null) {
            return createAvailability(availabilityModel);
        } else {
            return updateAvailability(availabilityRecord.id(), availabilityRecord);
        }
    }

    @Transactional
    public List<InstructorAvailabilityRecord> saveBatch(List<InstructorAvailabilityRecord> availabilityRecords) {
        if (availabilityRecords == null || availabilityRecords.isEmpty()) {
            throw new IllegalArgumentException("Availability records cannot be null or empty");
        }

        // Validar todos os registros primeiro
        for (InstructorAvailabilityRecord record : availabilityRecords) {
            validateAvailabilityRecord(record);
        }

        // Converter para modelos
        List<InstructorAvailabilityModel> models = availabilityRecords.stream()
                .map(InstructorAvailabilityMapper::fromRecord)
                .collect(Collectors.toList());

        // Verificar sobreposições em lote
        for (InstructorAvailabilityModel model : models) {
            if (hasOverlap(model, null)) {
                throw new IllegalArgumentException("Schedule overlap detected for instructor");
            }
        }

        // Configurar timestamps
        LocalDateTime now = LocalDateTime.now();
        for (InstructorAvailabilityModel model : models) {
            model.setId(UUID.randomUUID());
            model.setCreatedAt(now);
            model.setUpdatedAt(now);
        }

        List<InstructorAvailabilityModel> savedModels = availabilityDAO.saveBatch(models);
        return savedModels.stream()
                .map(InstructorAvailabilityMapper::toRecord)
                .collect(Collectors.toList());
    }

    private InstructorAvailabilityRecord createAvailability(InstructorAvailabilityModel availabilityModel) {
        // Verificar sobreposição de horários
        if (hasOverlap(availabilityModel, null)) {
            throw new IllegalArgumentException("Schedule overlap detected for instructor");
        }

        availabilityModel.setCreatedAt(LocalDateTime.now());
        availabilityModel.setUpdatedAt(LocalDateTime.now());

        InstructorAvailabilityModel savedModel = availabilityDAO.save(availabilityModel);
        return InstructorAvailabilityMapper.toRecord(savedModel);
    }

    public InstructorAvailabilityRecord getById(String id) {
        UUID uuid = UUID.fromString(id);
        return availabilityDAO.findById(uuid)
                .map(InstructorAvailabilityMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found with ID: " + id));
    }

    public List<InstructorAvailabilityRecord> getAll() {
        return availabilityDAO.findAll().stream()
                .map(InstructorAvailabilityMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<InstructorAvailabilityRecord> getByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.findByInstructor(instructorUuid).stream()
                .map(InstructorAvailabilityMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<InstructorAvailabilityRecord> getByInstructorAndDay(String instructorId, Integer dayOfWeek) {
        validateDayOfWeek(dayOfWeek);
        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.findByInstructorAndDay(instructorUuid, dayOfWeek).stream()
                .map(InstructorAvailabilityMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<InstructorAvailabilityRecord> getByDay(Integer dayOfWeek) {
        validateDayOfWeek(dayOfWeek);
        return availabilityDAO.findByDay(dayOfWeek).stream()
                .map(InstructorAvailabilityMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<InstructorAvailabilityRecord> getWeeklySchedule(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.findWeeklySchedule(instructorUuid).stream()
                .map(InstructorAvailabilityMapper::toRecord)
                .collect(Collectors.toList());
    }

    public boolean checkAvailability(String instructorId, Integer dayOfWeek, LocalTime startTime, LocalTime endTime) {
        validateDayOfWeek(dayOfWeek);
        validateTimeRange(startTime, endTime);

        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.checkAvailability(instructorUuid, dayOfWeek, startTime, endTime);
    }

    public List<String> findAvailableInstructors(Integer dayOfWeek, LocalTime startTime, LocalTime endTime) {
        validateDayOfWeek(dayOfWeek);
        validateTimeRange(startTime, endTime);

        return availabilityDAO.findAvailableInstructors(dayOfWeek, startTime, endTime).stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        InstructorAvailabilityModel availability = availabilityDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found with ID: " + id));

        if (availability.getDeletedAt() != null) {
            throw new IllegalArgumentException("Availability is already deleted");
        }

        boolean deleted = availabilityDAO.delete(uuid);
        if (!deleted) {
            throw new RuntimeException("Failed to delete availability with ID: " + id);
        }
    }

    @Transactional
    public void deleteAllByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        int deletedCount = availabilityDAO.deleteAllByInstructor(instructorUuid);

        if (deletedCount == 0) {
            throw new IllegalArgumentException("No availabilities found for instructor ID: " + instructorId);
        }
    }

    @Transactional
    public void deleteByInstructorAndDay(String instructorId, Integer dayOfWeek) {
        validateDayOfWeek(dayOfWeek);
        UUID instructorUuid = UUID.fromString(instructorId);
        int deletedCount = availabilityDAO.deleteByInstructorAndDay(instructorUuid, dayOfWeek);

        if (deletedCount == 0) {
            throw new IllegalArgumentException("No availabilities found for instructor ID: " + instructorId + " and day: " + dayOfWeek);
        }
    }

    @Transactional
    public void restore(String id) {
        UUID uuid = UUID.fromString(id);
        InstructorAvailabilityModel availability = availabilityDAO.findByIdWithDeleted(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found with ID: " + id));

        if (availability.getDeletedAt() == null) {
            throw new IllegalArgumentException("Availability is not deleted");
        }

        boolean restored = availabilityDAO.restore(uuid);
        if (!restored) {
            throw new RuntimeException("Failed to restore availability with ID: " + id);
        }
    }

    @Transactional
    public InstructorAvailabilityRecord updateAvailability(String id, InstructorAvailabilityRecord availabilityRecord) {
        validateAvailabilityRecord(availabilityRecord);

        UUID uuid = UUID.fromString(id);
        InstructorAvailabilityModel existingAvailability = availabilityDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found with ID: " + id));

        if (existingAvailability.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted availability");
        }

        // Verificar sobreposição (excluindo o próprio registro)
        InstructorAvailabilityModel updatedModel = InstructorAvailabilityMapper.fromRecord(availabilityRecord);
        updatedModel.setId(uuid);

        if (hasOverlap(updatedModel, uuid)) {
            throw new IllegalArgumentException("Schedule overlap detected for instructor");
        }

        updatedModel.setCreatedAt(existingAvailability.getCreatedAt());
        updatedModel.setUpdatedAt(LocalDateTime.now());
        updatedModel.setInstructorId(existingAvailability.getInstructorId()); // Preservar instructorId

        InstructorAvailabilityModel savedModel = availabilityDAO.save(updatedModel);
        return InstructorAvailabilityMapper.toRecord(savedModel);
    }

    public InstructorAvailabilityRecord getNextAvailableSlot(String instructorId, Integer dayOfWeek) {
        UUID instructorUuid = UUID.fromString(instructorId);

        if (dayOfWeek != null) {
            validateDayOfWeek(dayOfWeek);
        }

        List<InstructorAvailabilityModel> slots = availabilityDAO.findNextAvailableSlot(instructorUuid, dayOfWeek);

        if (slots.isEmpty()) {
            return null;
        }

        // Retorna o primeiro slot disponível
        return InstructorAvailabilityMapper.toRecord(slots.get(0));
    }

    public boolean checkForOverlap(String instructorId, Integer dayOfWeek, LocalTime startTime, LocalTime endTime, String excludeId) {
        validateDayOfWeek(dayOfWeek);
        validateTimeRange(startTime, endTime);

        UUID instructorUuid = UUID.fromString(instructorId);
        UUID excludeUuid = excludeId != null ? UUID.fromString(excludeId) : null;

        return availabilityDAO.hasOverlap(instructorUuid, dayOfWeek, startTime, endTime, excludeUuid);
    }

    private boolean hasOverlap(InstructorAvailabilityModel availability, UUID excludeId) {
        return availabilityDAO.hasOverlap(
                availability.getInstructorId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime(),
                excludeId
        );
    }

    public int countByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.countByInstructor(instructorUuid);
    }

    public int countByInstructorAndDay(String instructorId, Integer dayOfWeek) {
        validateDayOfWeek(dayOfWeek);
        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.countByInstructorAndDay(instructorUuid, dayOfWeek);
    }

    private void validateAvailabilityRecord(InstructorAvailabilityRecord availabilityRecord) {
        if (availabilityRecord == null) {
            throw new IllegalArgumentException("Availability record cannot be null");
        }

        if (availabilityRecord.instructorId() == null || availabilityRecord.instructorId().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID is required");
        }

        validateDayOfWeek(availabilityRecord.dayOfWeek());
        validateTimeRange(availabilityRecord.startTime(), availabilityRecord.endTime());
    }

    private void validateDayOfWeek(Integer dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek < 0 || dayOfWeek > 6) {
            throw new IllegalArgumentException("Day of week must be between 0 (Sunday) and 6 (Saturday)");
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Validar que o horário está em intervalos de 30 minutos (opcional)
        if (startTime.getMinute() % 30 != 0 || endTime.getMinute() % 30 != 0) {
            throw new IllegalArgumentException("Times must be in 30-minute intervals");
        }
    }
}