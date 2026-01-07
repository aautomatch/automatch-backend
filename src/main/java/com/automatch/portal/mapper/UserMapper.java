package com.automatch.portal.mapper;

import com.automatch.portal.model.AddressModel;
import com.automatch.portal.model.UserModel;
import com.automatch.portal.enums.UserRole;
import com.automatch.portal.records.UserRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserMapper {

    public static UserRecord toRecord(UserModel model) {
        if (model == null) return null;
        return new UserRecord(
                model.getId(),
                model.getFullName(),
                model.getEmail(),
                model.getPassword(),
                model.getPhone(),
                model.getRole(), // Agora é UserRole, não ClassifierRecord
                model.getIsActive(),
                model.getProfileImageUrl(),
                AddressMapper.toRecord(model.getAddress()),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static UserModel fromRecord(UserRecord record) {
        if (record == null) return null;
        UserModel model = new UserModel();
        model.setId(record.id());
        model.setFullName(record.fullName());
        model.setEmail(record.email());
        model.setPassword(record.password());
        model.setPhone(record.phone());
        model.setRole((UserRole) record.role()); // Cast para UserRole
        model.setIsActive(record.isActive());
        model.setProfileImageUrl(record.profileImageUrl());
        model.setAddress(AddressMapper.fromRecord(record.address()));
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());
        return model;
    }

    public static RowMapper<UserModel> getRowMapper() {
        return (rs, rowNum) -> mapRow(rs);
    }

    private static UserModel mapRow(ResultSet rs) throws SQLException {
        UserModel user = new UserModel();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPassword(rs.getString("password"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                user.setRole(UserRole.valueOf(roleStr));
            } catch (IllegalArgumentException e) {
                user.setRole(UserRole.STUDENT);
            }
        }

        user.setIsActive(rs.getBoolean("is_active"));
        user.setProfileImageUrl(rs.getString("profile_image_url"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp lastLoggin = rs.getTimestamp("last_loggin");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        Timestamp deletedAt = rs.getTimestamp("deleted_at");

        user.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        user.setLastLoggin(lastLoggin != null ? lastLoggin.toLocalDateTime() : null);
        user.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        user.setDeletedAt(deletedAt != null ? deletedAt.toLocalDateTime() : null);

        String addressId = rs.getString("address_id");
        if (addressId != null && !rs.wasNull()) {
            AddressModel address = new AddressModel();
            address.setId(UUID.fromString(addressId));
            address.setStreet(rs.getString("street"));
            address.setNumber(rs.getString("number"));
            address.setNeighborhood(rs.getString("neighborhood"));
            address.setCity(rs.getString("city"));
            address.setState(rs.getString("state"));
            address.setZipCode(rs.getString("zip_code"));
            address.setCountry(rs.getString("country"));

            Timestamp addrCreatedAt = rs.getTimestamp("address_created_at");
            Timestamp addrUpdatedAt = rs.getTimestamp("address_updated_at");
            Timestamp addrDeletedAt = rs.getTimestamp("address_deleted_at");

            address.setCreatedAt(addrCreatedAt != null ? addrCreatedAt.toLocalDateTime() : null);
            address.setUpdatedAt(addrUpdatedAt != null ? addrUpdatedAt.toLocalDateTime() : null);
            address.setDeletedAt(addrDeletedAt != null ? addrDeletedAt.toLocalDateTime() : null);

            user.setAddress(address);
        }

        return user;
    }
}