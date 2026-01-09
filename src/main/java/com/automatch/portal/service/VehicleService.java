package com.automatch.portal.service;

import com.automatch.portal.dao.VehicleDAO;
import com.automatch.portal.mapper.VehicleMapper;
import com.automatch.portal.model.VehicleModel;
import com.automatch.portal.records.VehicleRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleDAO vehicleDAO;

    @Transactional
    public VehicleRecord save(VehicleRecord vehicleRecord) {
        validateVehicleRecord(vehicleRecord);

        VehicleModel vehicleModel = VehicleMapper.fromRecord(vehicleRecord);

        if (vehicleModel.getId() == null) {
            return createVehicle(vehicleModel);
        } else {
            return updateVehicle(vehicleRecord.id(), vehicleRecord);
        }
    }

    private VehicleRecord createVehicle(VehicleModel vehicleModel) {
        if (vehicleDAO.existsByLicensePlate(vehicleModel.getLicensePlate())) {
            throw new IllegalArgumentException("License plate already exists: " + vehicleModel.getLicensePlate());
        }

        if (vehicleModel.getInstructorId() == null) {
            throw new IllegalArgumentException("Instructor ID is required");
        }

        // Verificar se o instructor existe (opcional - adicione um service se necessário)
        // instructorService.validateInstructorExists(vehicleModel.getInstructorId());

        vehicleModel.setIsApproved(false);
        vehicleModel.setIsAvailable(true);
        vehicleModel.setCreatedAt(LocalDateTime.now());
        vehicleModel.setUpdatedAt(LocalDateTime.now());

        VehicleModel savedModel = vehicleDAO.save(vehicleModel);
        return VehicleMapper.toRecord(savedModel);
    }

    public VehicleRecord getById(String id) {
        UUID uuid = UUID.fromString(id);
        return vehicleDAO.findById(uuid)
                .map(VehicleMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));
    }

    public List<VehicleRecord> getAll() {
        return vehicleDAO.findAll().stream()
                .map(VehicleMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<VehicleRecord> getByInstructor(String instructorId) {
        UUID instructorUuid = UUID.fromString(instructorId);
        return vehicleDAO.findByInstructor(instructorUuid).stream()
                .map(VehicleMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<VehicleRecord> getAvailableVehicles() {
        return vehicleDAO.findAvailable().stream()
                .map(VehicleMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<VehicleRecord> getApprovedVehicles() {
        return vehicleDAO.findApproved().stream()
                .map(VehicleMapper::toRecord)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        VehicleModel vehicle = vehicleDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        if (vehicle.getDeletedAt() != null) {
            throw new IllegalArgumentException("Vehicle is already deleted");
        }

        boolean deleted = vehicleDAO.delete(uuid);
        if (!deleted) {
            throw new RuntimeException("Failed to delete vehicle with ID: " + id);
        }
    }

    @Transactional
    public VehicleRecord approveVehicle(String id) {
        UUID uuid = UUID.fromString(id);
        VehicleModel vehicle = vehicleDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        if (Boolean.TRUE.equals(vehicle.getIsApproved())) {
            throw new IllegalArgumentException("Vehicle is already approved");
        }

        vehicle.setIsApproved(true);
        vehicle.setUpdatedAt(LocalDateTime.now());

        VehicleModel updatedModel = vehicleDAO.save(vehicle);
        return VehicleMapper.toRecord(updatedModel);
    }

    @Transactional
    public VehicleRecord disapproveVehicle(String id) {
        UUID uuid = UUID.fromString(id);
        VehicleModel vehicle = vehicleDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        if (Boolean.FALSE.equals(vehicle.getIsApproved())) {
            throw new IllegalArgumentException("Vehicle is already disapproved");
        }

        vehicle.setIsApproved(false);
        vehicle.setUpdatedAt(LocalDateTime.now());

        VehicleModel updatedModel = vehicleDAO.save(vehicle);
        return VehicleMapper.toRecord(updatedModel);
    }

    @Transactional
    public VehicleRecord setVehicleAvailable(String id) {
        UUID uuid = UUID.fromString(id);
        VehicleModel vehicle = vehicleDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        if (Boolean.TRUE.equals(vehicle.getIsAvailable())) {
            throw new IllegalArgumentException("Vehicle is already available");
        }

        vehicle.setIsAvailable(true);
        vehicle.setUpdatedAt(LocalDateTime.now());

        VehicleModel updatedModel = vehicleDAO.save(vehicle);
        return VehicleMapper.toRecord(updatedModel);
    }

    @Transactional
    public VehicleRecord setVehicleUnavailable(String id) {
        UUID uuid = UUID.fromString(id);
        VehicleModel vehicle = vehicleDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        if (Boolean.FALSE.equals(vehicle.getIsAvailable())) {
            throw new IllegalArgumentException("Vehicle is already unavailable");
        }

        vehicle.setIsAvailable(false);
        vehicle.setUpdatedAt(LocalDateTime.now());

        VehicleModel updatedModel = vehicleDAO.save(vehicle);
        return VehicleMapper.toRecord(updatedModel);
    }

    @Transactional
    public VehicleRecord updateVehicle(String id, VehicleRecord vehicleRecord) {
        validateVehicleRecord(vehicleRecord);

        UUID uuid = UUID.fromString(id);
        VehicleModel existingVehicle = vehicleDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        if (existingVehicle.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted vehicle");
        }

        // Verificar se a nova placa já existe (exceto para este veículo)
        if (!existingVehicle.getLicensePlate().equals(vehicleRecord.licensePlate()) &&
                vehicleDAO.existsByLicensePlate(vehicleRecord.licensePlate())) {
            throw new IllegalArgumentException("License plate already exists: " + vehicleRecord.licensePlate());
        }

        VehicleModel updatedModel = VehicleMapper.fromRecord(vehicleRecord);
        updatedModel.setId(uuid);
        updatedModel.setCreatedAt(existingVehicle.getCreatedAt());
        updatedModel.setUpdatedAt(LocalDateTime.now());
        updatedModel.setInstructorId(existingVehicle.getInstructorId()); // Preserva instructorId

        // Preservar dados não fornecidos no update
        if (updatedModel.getIsApproved() == null) {
            updatedModel.setIsApproved(existingVehicle.getIsApproved());
        }

        if (updatedModel.getIsAvailable() == null) {
            updatedModel.setIsAvailable(existingVehicle.getIsAvailable());
        }

        if (updatedModel.getHasDualControls() == null) {
            updatedModel.setHasDualControls(existingVehicle.getHasDualControls());
        }

        if (updatedModel.getHasAirConditioning() == null) {
            updatedModel.setHasAirConditioning(existingVehicle.getHasAirConditioning());
        }

        if (updatedModel.getLastMaintenanceDate() == null) {
            updatedModel.setLastMaintenanceDate(existingVehicle.getLastMaintenanceDate());
        }

        VehicleModel savedModel = vehicleDAO.save(updatedModel);
        return VehicleMapper.toRecord(savedModel);
    }

    public VehicleRecord findByLicensePlate(String licensePlate) {
        return vehicleDAO.findByLicensePlate(licensePlate)
                .map(VehicleMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with license plate: " + licensePlate));
    }

    @Transactional
    public VehicleRecord updateMaintenanceDate(String id, LocalDate maintenanceDate) {
        UUID uuid = UUID.fromString(id);
        VehicleModel vehicle = vehicleDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        vehicle.setLastMaintenanceDate(maintenanceDate);
        vehicle.setUpdatedAt(LocalDateTime.now());

        VehicleModel updatedModel = vehicleDAO.save(vehicle);
        return VehicleMapper.toRecord(updatedModel);
    }

    public List<VehicleRecord> getVehiclesByTransmission(Integer transmissionTypeId) {
        return vehicleDAO.findByTransmissionType(transmissionTypeId).stream()
                .map(VehicleMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<VehicleRecord> getVehiclesByCategory(Integer categoryId) {
        return vehicleDAO.findByCategory(categoryId).stream()
                .map(VehicleMapper::toRecord)
                .collect(Collectors.toList());
    }

    private void validateVehicleRecord(VehicleRecord vehicleRecord) {
        if (vehicleRecord == null) {
            throw new IllegalArgumentException("Vehicle record cannot be null");
        }

        if (vehicleRecord.licensePlate() == null || vehicleRecord.licensePlate().trim().isEmpty()) {
            throw new IllegalArgumentException("License plate is required");
        }

        if (vehicleRecord.model() == null || vehicleRecord.model().trim().isEmpty()) {
            throw new IllegalArgumentException("Model is required");
        }

        if (vehicleRecord.instructorId() == null || vehicleRecord.instructorId().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID is required");
        }
    }
}