package com.talentsboard.backend.service;

import com.talentsboard.backend.dto.UserDTO;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserDTO registerUser(UserDTO dto) {
        Optional<User> existing = userRepository.findByFirebaseUid(dto.getFirebaseUid());
        if (existing.isPresent()) {
            return toDTO(existing.get());
        }
        User user = new User();
        user.setFirebaseUid(dto.getFirebaseUid());
        user.setEmail(dto.getEmail());
        user.setDisplayName(dto.getDisplayName());
        user.setRole(dto.getRole());
        user.setCompany(dto.getCompany());
        return toDTO(userRepository.save(user));
    }

    public UserDTO getUserProfile(String firebaseUid) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("Profil non trouvé pour uid: " + firebaseUid));
        return toDTO(user);
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
        if (user.getCreatedAt() != null) dto.setCreatedAt(user.getCreatedAt().toString());
        return dto;
    }
}
