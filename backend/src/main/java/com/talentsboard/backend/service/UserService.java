package com.talentsboard.backend.service;

import com.talentsboard.backend.model.User;
import com.talentsboard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Service métier User — orchestrateur simple :
 * - fait appel au repository Firestore
 * - ne gère pas la création d'auth (FirebaseAuth) : fait par AuthService
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) throws ExecutionException, InterruptedException {
        return userRepository.save(user);
    }

    public User getUserById(String id) throws ExecutionException, InterruptedException {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return userRepository.findAll();
    }

    public User updateUser(User user) throws ExecutionException, InterruptedException {
        return userRepository.update(user);
    }

    public void deleteUser(String id) throws ExecutionException, InterruptedException {
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) throws ExecutionException, InterruptedException {
        return userRepository.findByEmail(email);
    }
}
