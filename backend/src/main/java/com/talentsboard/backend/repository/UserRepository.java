package com.talentsboard.backend.repository;

import com.talentsboard.backend.model.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Interface repository pour abstraction Firestore.
 */
public interface UserRepository {
    User save(User user) throws ExecutionException, InterruptedException;
    User findById(String id) throws ExecutionException, InterruptedException;
    List<User> findAll() throws ExecutionException, InterruptedException;
    User update(User user) throws ExecutionException, InterruptedException;
    void deleteById(String id) throws ExecutionException, InterruptedException;
    User findByEmail(String email) throws ExecutionException, InterruptedException;
}
