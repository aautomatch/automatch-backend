package com.automatch.portal.controller;

import com.automatch.portal.records.VehicleRecord;
import com.automatch.portal.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/protected/vehicle")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleRecord> createVehicle(@RequestBody VehicleRecord vehicleRecord) {
        VehicleRecord createdVehicle = vehicleService.save(vehicleRecord);
        return ResponseEntity.ok(createdVehicle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleRecord> getVehicleById(@PathVariable String id) {
        VehicleRecord vehicle = vehicleService.getById(id);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<VehicleRecord>> getAllVehicles() {
        List<VehicleRecord> vehicles = vehicleService.getAll();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<VehicleRecord>> getVehiclesByInstructor(@PathVariable String instructorId) {
        List<VehicleRecord> vehicles = vehicleService.getByInstructor(instructorId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/available")
    public ResponseEntity<List<VehicleRecord>> getAvailableVehicles() {
        List<VehicleRecord> vehicles = vehicleService.getAvailableVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<VehicleRecord>> getApprovedVehicles() {
        List<VehicleRecord> vehicles = vehicleService.getApprovedVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleRecord> updateVehicle(@PathVariable String id, @RequestBody VehicleRecord vehicleRecord) {
        VehicleRecord updatedVehicle = vehicleService.updateVehicle(id, vehicleRecord);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<VehicleRecord> approveVehicle(@PathVariable String id) {
        VehicleRecord approvedVehicle = vehicleService.approveVehicle(id);
        return ResponseEntity.ok(approvedVehicle);
    }

    @PutMapping("/{id}/disapprove")
    public ResponseEntity<VehicleRecord> disapproveVehicle(@PathVariable String id) {
        VehicleRecord disapprovedVehicle = vehicleService.disapproveVehicle(id);
        return ResponseEntity.ok(disapprovedVehicle);
    }

    @PutMapping("/{id}/available")
    public ResponseEntity<VehicleRecord> setVehicleAvailable(@PathVariable String id) {
        VehicleRecord updatedVehicle = vehicleService.setVehicleAvailable(id);
        return ResponseEntity.ok(updatedVehicle);
    }

    @PutMapping("/{id}/unavailable")
    public ResponseEntity<VehicleRecord> setVehicleUnavailable(@PathVariable String id) {
        VehicleRecord updatedVehicle = vehicleService.setVehicleUnavailable(id);
        return ResponseEntity.ok(updatedVehicle);
    }

    @GetMapping("/license-plate/{licensePlate}")
    public ResponseEntity<VehicleRecord> getVehicleByLicensePlate(@PathVariable String licensePlate) {
        VehicleRecord vehicle = vehicleService.findByLicensePlate(licensePlate);
        return ResponseEntity.ok(vehicle);
    }

    @PutMapping("/{id}/maintenance")
    public ResponseEntity<VehicleRecord> updateMaintenanceDate(@PathVariable String id, @RequestBody LocalDate maintenanceDate) {
        VehicleRecord updatedVehicle = vehicleService.updateMaintenanceDate(id, maintenanceDate);
        return ResponseEntity.ok(updatedVehicle);
    }
}