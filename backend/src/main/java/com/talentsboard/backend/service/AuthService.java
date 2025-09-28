package com.talentsboard.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.talentsboard.backend.dto.AuthRequest;
import com.talentsboard.backend.dto.AuthResponse;
import com.talentsboard.backend.model.User;
import org.springframework.stereotype.Service;

/**
 * Service pour gérer l’authentification via Firebase.
 */
@Service
public class AuthService {

    /**
     * Inscription d’un nouvel utilisateur.
     * @param user Objet utilisateur (Candidat ou Entreprise)
     * @param motDePasse Mot de passe
     * @return Utilisateur avec son UID Firebase
     */
    public User register(User user, String motDePasse) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(motDePasse)
                .setDisplayName(user.getNom());

        UserRecord firebaseUser = FirebaseAuth.getInstance().createUser(request);

        user.setId(firebaseUser.getUid());
        return user;
    }

    /**
     * Connexion / login.
     * @param request Requête d’authentification (email, motDePasse)
     * @return AuthResponse avec token et UID
     */
    public AuthResponse login(AuthRequest request) throws Exception {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(request.getEmail());
        String token = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

        return new AuthResponse(token, userRecord.getUid());
    }

    /**
     * Génération d’un token custom (pour tests ou services internes).
     * @param uid UID utilisateur Firebase
     * @return JWT Firebase
     */
    public String generateToken(String uid) throws Exception {
        return FirebaseAuth.getInstance().createCustomToken(uid);
    }
}
