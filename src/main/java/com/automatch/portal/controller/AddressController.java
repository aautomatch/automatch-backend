package com.automatch.portal.controller;

import com.automatch.portal.records.AddressRecord;
import com.automatch.portal.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/protected/address")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressRecord> createAddress(@RequestBody AddressRecord addressRecord) {
        AddressRecord createdAddress = addressService.save(addressRecord);
        return ResponseEntity.ok(createdAddress);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressRecord> getAddressById(@PathVariable UUID id) {
        AddressRecord address = addressService.getById(id);
        return ResponseEntity.ok(address);
    }

    @GetMapping
    public ResponseEntity<List<AddressRecord>> getAllAddresses() {
        List<AddressRecord> addresses = addressService.getAll();
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AddressRecord> getAddressByUserId(@PathVariable UUID userId) {
        AddressRecord address = addressService.getByUserId(userId);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<AddressRecord>> getAddressesByCity(@PathVariable String city) {
        List<AddressRecord> addresses = addressService.getByCity(city);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<AddressRecord>> getAddressesByState(@PathVariable String state) {
        List<AddressRecord> addresses = addressService.getByState(state);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/zip-code/{zipCode}")
    public ResponseEntity<List<AddressRecord>> getAddressesByZipCode(@PathVariable String zipCode) {
        List<AddressRecord> addresses = addressService.getByZipCode(zipCode);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<AddressRecord>> getAddressesByCountry(@PathVariable String country) {
        List<AddressRecord> addresses = addressService.getByCountry(country);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressRecord> updateAddress(@PathVariable UUID id, @RequestBody AddressRecord addressRecord) {
        AddressRecord updatedAddress = addressService.updateAddress(id, addressRecord);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restoreAddress(@PathVariable UUID id) {
        addressService.restore(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AddressRecord>> searchAddresses(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state) {
        List<AddressRecord> addresses = addressService.searchAddresses(street, neighborhood, city, state);
        return ResponseEntity.ok(addresses);
    }
}