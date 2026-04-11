package com.talentsboard.backend.service;

import com.talentsboard.backend.dto.UserDTO;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + id));
        return toDTO(user);
    }

    public UserDTO getUserByFirebaseUid(String uid) {
        User user = userRepository.findByFirebaseUid(uid)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + uid));
        return toDTO(user);
    }

    public UserDTO createUser(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé: " + dto.getEmail());
        }
        User user = toEntity(dto);
        return toDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + id));
        user.setDisplayName(dto.getDisplayName());
        user.setBio(dto.getBio());
        user.setCompany(dto.getCompany());
        user.setCvUrl(dto.getCvUrl());
        user.setSkills(dto.getSkills());
        return toDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserDTO> getCandidates() {
        return userRepository.findByRole(User.UserRole.CANDIDATE)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<UserDTO> getEnterprises() {
        return userRepository.findByRole(User.UserRole.ENTERPRISE)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirebaseUid(user.getFirebaseUid());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setRole(user.getRole());
        dto.setCompany(user.getCompany());
        dto.setBio(user.getBio());
        dto.setCvUrl(user.getCvUrl());
        dto.setSkills(user.getSkills());
        if (user.getCreatedAt() != null) dto.setCreatedAt(user.getCreatedAt().toString());
        return dto;
    }

    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setFirebaseUid(dto.getFirebaseUid());
        user.setEmail(dto.getEmail());
        user.setDisplayName(dto.getDisplayName());
        user.setRole(dto.getRole());
        user.setCompany(dto.getCompany());
        user.setBio(dto.getBio());
        user.setCvUrl(dto.getCvUrl());
        user.setSkills(dto.getSkills());
        return user;
    }
}
