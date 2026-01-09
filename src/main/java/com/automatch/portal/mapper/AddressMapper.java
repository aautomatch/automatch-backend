package com.automatch.portal.mapper;

import com.automatch.portal.model.AddressModel;
import com.automatch.portal.records.AddressRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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

    public static RowMapper<AddressModel> getRowMapper() {
        return new RowMapper<AddressModel>() {
            @Override
            public AddressModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                AddressModel address = new AddressModel();
                address.setId(UUID.fromString(rs.getString("id")));
                address.setStreet(rs.getString("street"));
                address.setNumber(rs.getString("number"));
                address.setNeighborhood(rs.getString("neighborhood"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setZipCode(rs.getString("zip_code"));
                address.setCountry(rs.getString("country"));

                address.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                address.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                if (rs.getTimestamp("deleted_at") != null) {
                    address.setDeletedAt(rs.getTimestamp("deleted_at").toLocalDateTime());
                }

                return address;
            }
        };
    }
}