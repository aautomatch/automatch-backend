package com.automatch.portal.service;

import com.automatch.portal.dao.UserDAO;
import com.automatch.portal.mapper.UserMapper;
import com.automatch.portal.model.UserModel;
import com.automatch.portal.records.LoginRequestRecord;
import com.automatch.portal.records.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserRecord save(UserRecord userRecord) {
        validateUserRecord(userRecord);

        UserModel userModel = UserMapper.fromRecord(userRecord);

        if (userModel.getId() == null) {
            return createUser(userModel);
        } else {
            return updateUser(userModel.getId(), userRecord);
        }
    }

    private UserRecord createUser(UserModel userModel) {
        if (userDAO.existsByEmail(userModel.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userModel.getEmail());
        }

        if (userModel.getPassword() != null && !userModel.getPassword().isBlank()) {
            String hash = passwordEncoder.encode(userModel.getPassword());
            userModel.setPassword(hash);
        } else {
            throw new IllegalArgumentException("Password is required");
        }

        // Validação básica de role
        if (userModel.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        userModel.setIsActive(true);
        userModel.setCreatedAt(LocalDateTime.now());
        userModel.setUpdatedAt(LocalDateTime.now());

        UserModel savedModel = userDAO.save(userModel);
        return UserMapper.toRecord(savedModel);
    }

    public UserRecord login(LoginRequestRecord loginRequest) {
        UserModel user = userDAO.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("User account is inactive");
        }

        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("User account is deleted");
        }

        if (user.getPassword() == null || loginRequest.password() == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        userDAO.updateLastLogin(user.getId());

        return UserMapper.toRecord(user);
    }

    public UserRecord getById(UUID id) {
        return userDAO.findById(id)
                .map(UserMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public List<UserRecord> getAll() {
        return userDAO.findAll().stream()
                .map(UserMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<UserRecord> getActiveUsers() {
        return userDAO.findAllActive().stream()
                .map(UserMapper::toRecord)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id) {
        UserModel user = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("User is already deleted");
        }

        boolean deleted = userDAO.delete(id);
        if (!deleted) {
            throw new RuntimeException("Failed to delete user with ID: " + id);
        }
    }

    @Transactional
    public void activate(UUID id) {
        UserModel user = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("User is already active");
        }

        boolean activated = userDAO.activate(id);
        if (!activated) {
            throw new RuntimeException("Failed to activate user with ID: " + id);
        }
    }

    @Transactional
    public void deactivate(UUID id) {
        UserModel user = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("User is already inactive");
        }

        boolean deactivated = userDAO.delete(id); // Soft delete também desativa
        if (!deactivated) {
            throw new RuntimeException("Failed to deactivate user with ID: " + id);
        }
    }

    @Transactional
    public UserRecord updateUser(UUID id, UserRecord userRecord) {
        validateUserRecord(userRecord);

        UserModel existingUser = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (existingUser.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted user");
        }

        if (!existingUser.getEmail().equals(userRecord.email()) &&
                userDAO.existsByEmail(userRecord.email())) {
            throw new IllegalArgumentException("Email already exists: " + userRecord.email());
        }

        UserModel updatedModel = UserMapper.fromRecord(userRecord);
        updatedModel.setId(id);

        if (userRecord.password() != null && !userRecord.password().isBlank()) {
            updatedModel.setPassword(passwordEncoder.encode(userRecord.password()));
        } else {
            updatedModel.setPassword(existingUser.getPassword());
        }

        updatedModel.setCreatedAt(existingUser.getCreatedAt());
        updatedModel.setLastLoggin(existingUser.getLastLoggin());
        updatedModel.setUpdatedAt(LocalDateTime.now());

        if (updatedModel.getIsActive() == null) {
            updatedModel.setIsActive(existingUser.getIsActive());
        }

        if (updatedModel.getRole() == null) {
            updatedModel.setRole(existingUser.getRole());
        }

        UserModel savedModel = userDAO.save(updatedModel);
        return UserMapper.toRecord(savedModel);
    }

    public UserRecord findByEmail(String email) {
        return userDAO.findByEmail(email)
                .map(UserMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Transactional
    public void updateLastLogin(UUID userId) {
        if (!userDAO.findById(userId).isPresent()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        userDAO.updateLastLogin(userId);
    }

    private void validateUserRecord(UserRecord userRecord) {
        if (userRecord == null) {
            throw new IllegalArgumentException("User record cannot be null");
        }

        if (userRecord.fullName() == null || userRecord.fullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }

        if (userRecord.email() == null || userRecord.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Validação básica de email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!userRecord.email().matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Para novos registros, password é obrigatório
        if (userRecord.id() == null && (userRecord.password() == null || userRecord.password().isBlank())) {
            throw new IllegalArgumentException("Password is required for new users");
        }

        // Role é obrigatório
        if (userRecord.role() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }
}