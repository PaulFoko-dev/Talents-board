package com.talentsboard.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.talentsboard.backend.dto.AuthRequest;
import com.talentsboard.backend.dto.AuthResponse;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.repository.UserRepository;
import com.talentsboard.backend.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AuthService.
 * Vérifie l'enregistrement et la connexion via Firebase.
 */
class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        UserService userService = Mockito.mock(UserService.class);
        authService = new AuthService(userService);
    }

    @Test
    void testRegisterUser() throws Exception {
        User user = new User();
        user.setNom("Doe");
        user.setPrenom("Alice");
        user.setEmail("alice@example.com");

        UserRecord fakeRecord = mock(UserRecord.class);
        when(fakeRecord.getUid()).thenReturn("uid123");

        try (MockedStatic<FirebaseAuth> firebaseMock = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockAuth = mock(FirebaseAuth.class);
            firebaseMock.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
            when(mockAuth.createUser(any(UserRecord.CreateRequest.class))).thenReturn(fakeRecord);

            User createdUser = authService.register(user, "password");

            assertNotNull(createdUser);
            assertEquals("uid123", createdUser.getId());
            assertEquals("Doe", createdUser.getNom());
            assertEquals("alice@example.com", createdUser.getEmail());
        }
    }

    @Test
    void testLogin() throws Exception {
        AuthRequest request = new AuthRequest("alice@example.com", "password");

        UserRecord fakeRecord = mock(UserRecord.class);
        when(fakeRecord.getUid()).thenReturn("uid123");

        try (MockedStatic<FirebaseAuth> firebaseMock = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockAuth = mock(FirebaseAuth.class);
            firebaseMock.when(FirebaseAuth::getInstance).thenReturn(mockAuth);

            when(mockAuth.getUserByEmail("alice@example.com")).thenReturn(fakeRecord);
            when(mockAuth.createCustomToken("uid123")).thenReturn("fake-token-123");

            AuthResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("fake-token-123", response.getToken());
            assertEquals("uid123", response.getUserId());
        }
    }
}
