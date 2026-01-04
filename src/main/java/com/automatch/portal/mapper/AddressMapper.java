package com.automatch.portal.mapper;


import com.automatch.portal.model.AddressModel;
import com.automatch.portal.records.AddressRecord;

public class AddressMapper {

    public static AddressRecord toRecord(AddressModel model) {
        if (model == null) return null;
        return new AddressRecord(
                model.getId(),
                model.getStreet(),
                model.getNumber(),
                model.getNeighborhood(),
                model.getCity(),
                model.getState(),
                model.getZipCode(),
                model.getCountry(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static AddressModel fromRecord(AddressRecord record) {
        if (record == null) return null;
        return new AddressModel(
                record.id(),
                record.street(),
                record.number(),
                record.neighborhood(),
                record.city(),
                record.state(),
                record.zipCode(),
                record.country(),
                record.createdAt(),
                record.updatedAt(),
                record.deletedAt()
        );
    }
}
