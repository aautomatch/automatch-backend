package com.automatch.portal.service;

import com.automatch.portal.dao.AddressDAO;
import com.automatch.portal.mapper.AddressMapper;
import com.automatch.portal.model.AddressModel;
import com.automatch.portal.records.AddressRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressDAO addressDAO;

    @Transactional
    public AddressRecord save(AddressRecord addressRecord) {
        validateAddressRecord(addressRecord);

        AddressModel addressModel = AddressMapper.fromRecord(addressRecord);

        if (addressModel.getId() == null) {
            return createAddress(addressModel);
        } else {
            return updateAddress(addressRecord.id(), addressRecord);
        }
    }

    private AddressRecord createAddress(AddressModel addressModel) {
        addressModel.setCreatedAt(LocalDateTime.now());
        addressModel.setUpdatedAt(LocalDateTime.now());

        AddressModel savedModel = addressDAO.save(addressModel);
        return AddressMapper.toRecord(savedModel);
    }

    public AddressRecord getById(UUID id) {
        return addressDAO.findById(id)
                .map(AddressMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with ID: " + id));
    }

    public List<AddressRecord> getAll() {
        return addressDAO.findAll().stream()
                .map(AddressMapper::toRecord)
                .collect(Collectors.toList());
    }

    public AddressRecord getByUserId(UUID userId) {
        return addressDAO.findByUserId(userId)
                .map(AddressMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Address not found for user ID: " + userId));
    }

    public List<AddressRecord> getByCity(String city) {
        return addressDAO.findByCity(city).stream()
                .map(AddressMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<AddressRecord> getByState(String state) {
        return addressDAO.findByState(state).stream()
                .map(AddressMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<AddressRecord> getByZipCode(String zipCode) {
        return addressDAO.findByZipCode(zipCode).stream()
                .map(AddressMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<AddressRecord> getByCountry(String country) {
        return addressDAO.findByCountry(country).stream()
                .map(AddressMapper::toRecord)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id) {
        AddressModel address = addressDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with ID: " + id));

        if (address.getDeletedAt() != null) {
            throw new IllegalArgumentException("Address is already deleted");
        }

        boolean deleted = addressDAO.delete(id);
        if (!deleted) {
            throw new RuntimeException("Failed to delete address with ID: " + id);
        }
    }

    @Transactional
    public void restore(UUID id) {
        AddressModel address = addressDAO.findByIdWithDeleted(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with ID: " + id));

        if (address.getDeletedAt() == null) {
            throw new IllegalArgumentException("Address is not deleted");
        }

        boolean restored = addressDAO.restore(id);
        if (!restored) {
            throw new RuntimeException("Failed to restore address with ID: " + id);
        }
    }

    @Transactional
    public AddressRecord updateAddress(UUID id, AddressRecord addressRecord) {
        validateAddressRecord(addressRecord);

        AddressModel existingAddress = addressDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with ID: " + id));

        if (existingAddress.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted address");
        }

        AddressModel updatedModel = AddressMapper.fromRecord(addressRecord);
        updatedModel.setId(id);
        updatedModel.setCreatedAt(existingAddress.getCreatedAt());
        updatedModel.setUpdatedAt(LocalDateTime.now());

        AddressModel savedModel = addressDAO.save(updatedModel);
        return AddressMapper.toRecord(savedModel);
    }

    public List<AddressRecord> searchAddresses(String street, String neighborhood, String city, String state) {
        return addressDAO.search(street, neighborhood, city, state).stream()
                .map(AddressMapper::toRecord)
                .collect(Collectors.toList());
    }

    public boolean existsById(UUID id) {
        return addressDAO.existsById(id);
    }

    public int countActiveAddresses() {
        return addressDAO.countActive();
    }

    public int countByCity(String city) {
        return addressDAO.countByCity(city);
    }

    public int countByState(String state) {
        return addressDAO.countByState(state);
    }

    public int countByCountry(String country) {
        return addressDAO.countByCountry(country);
    }

    private void validateAddressRecord(AddressRecord addressRecord) {
        if (addressRecord == null) {
            throw new IllegalArgumentException("Address record cannot be null");
        }

        if (addressRecord.street() == null || addressRecord.street().trim().isEmpty()) {
            throw new IllegalArgumentException("Street is required");
        }

        if (addressRecord.city() == null || addressRecord.city().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }

        if (addressRecord.state() == null || addressRecord.state().trim().isEmpty()) {
            throw new IllegalArgumentException("State is required");
        }

        if (addressRecord.zipCode() == null || addressRecord.zipCode().trim().isEmpty()) {
            throw new IllegalArgumentException("ZIP code is required");
        }

        if (addressRecord.country() == null || addressRecord.country().trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
    }
}