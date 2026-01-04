package com.automatch.portal.mapper;

import com.automatch.portal.model.UserModel;
import com.example.records.UserRecord;
public class UserMapper {

    public static UserRecord toRecord(UserModel model) {
        if (model == null) return null;
        return new UserRecord(
                model.getId(),
                model.getFullName(),
                model.getEmail(),
                model.getPhone(),
                ClassifierMapper.toRecord(model.getUserType()),
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
        model.setPhone(record.phone());
        model.setUserType(ClassifierMapper.fromRecord(record.userType()));
        model.setIsActive(record.isActive());
        model.setProfileImageUrl(record.profileImageUrl());
        model.setAddress(AddressMapper.fromRecord(record.address()));
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());
        return model;
    }
}
