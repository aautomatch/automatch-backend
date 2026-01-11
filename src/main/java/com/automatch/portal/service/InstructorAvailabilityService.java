package com.automatch.portal.service;

import com.automatch.portal.dao.InstructorAvailabilityDAO;
import com.automatch.portal.enums.DayOfWeek;
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
    public InstructorAvailabilityRecord save(InstructorAvailabilityRecord availabilityRecord, String instructorId) {
        validateAvailabilityRecord(availabilityRecord);
        UUID instructorUuid = UUID.fromString(instructorId);

        InstructorAvailabilityModel availabilityModel = InstructorAvailabilityMapper.fromRecord(availabilityRecord, instructorUuid);

        if (availabilityModel.getId() == null) {
            return createAvailability(availabilityModel);
        } else {
            return updateAvailability(availabilityRecord.id(), availabilityModel);
        }
    }

    @Transactional
    public List<InstructorAvailabilityRecord> saveBatch(List<InstructorAvailabilityRecord> availabilityRecords, String instructorId) {
        if (availabilityRecords == null || availabilityRecords.isEmpty()) {
            throw new IllegalArgumentException("Availability records cannot be null or empty");
        }

        UUID instructorUuid = UUID.fromString(instructorId);

        for (InstructorAvailabilityRecord record : availabilityRecords) {
            validateAvailabilityRecord(record);
        }

        List<InstructorAvailabilityModel> models = availabilityRecords.stream()
                .map(record -> InstructorAvailabilityMapper.fromRecord(record, instructorUuid))
                .collect(Collectors.toList());

        for (InstructorAvailabilityModel model : models) {
            if (hasOverlap(model, null)) {
                throw new IllegalArgumentException("Schedule overlap detected for instructor");
            }
        }

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

    public List<InstructorAvailabilityRecord> getByInstructorAndDay(String instructorId, DayOfWeek dayOfWeek) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.findByInstructorAndDay(instructorUuid, dayOfWeek).stream()
                .map(InstructorAvailabilityMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<InstructorAvailabilityRecord> getByDay(DayOfWeek dayOfWeek) {
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

    public boolean checkAvailability(String instructorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        validateTimeRange(startTime, endTime);
        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.checkAvailability(instructorUuid, dayOfWeek, startTime, endTime);
    }

    public List<String> findAvailableInstructors(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
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
    public void deleteByInstructorAndDay(String instructorId, DayOfWeek dayOfWeek) {
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

    private InstructorAvailabilityRecord updateAvailability(String id, InstructorAvailabilityModel updatedModel) {
        UUID uuid = UUID.fromString(id);
        InstructorAvailabilityModel existingAvailability = availabilityDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found with ID: " + id));

        if (existingAvailability.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted availability");
        }

        if (hasOverlap(updatedModel, uuid)) {
            throw new IllegalArgumentException("Schedule overlap detected for instructor");
        }

        updatedModel.setCreatedAt(existingAvailability.getCreatedAt());
        updatedModel.setUpdatedAt(LocalDateTime.now());
        updatedModel.setInstructorId(existingAvailability.getInstructorId());

        InstructorAvailabilityModel savedModel = availabilityDAO.save(updatedModel);
        return InstructorAvailabilityMapper.toRecord(savedModel);
    }

    public InstructorAvailabilityRecord getNextAvailableSlot(String instructorId, DayOfWeek dayOfWeek) {
        UUID instructorUuid = UUID.fromString(instructorId);

        List<InstructorAvailabilityModel> slots = availabilityDAO.findNextAvailableSlot(instructorUuid, dayOfWeek);

        if (slots.isEmpty()) {
            return null;
        }

        return InstructorAvailabilityMapper.toRecord(slots.get(0));
    }

    public boolean checkForOverlap(String instructorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String excludeId) {
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

    public int countByInstructorAndDay(String instructorId, DayOfWeek dayOfWeek) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return availabilityDAO.countByInstructorAndDay(instructorUuid, dayOfWeek);
    }

    private void validateAvailabilityRecord(InstructorAvailabilityRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Availability record cannot be null");
        }

        validateTimeRange(record.startTime(), record.endTime());

        if (record.dayOfWeek() == null) {
            throw new IllegalArgumentException("Day of week is required");
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (startTime.getMinute() % 30 != 0 || endTime.getMinute() % 30 != 0) {
            throw new IllegalArgumentException("Times must be in 30-minute intervals");
        }
    }
}