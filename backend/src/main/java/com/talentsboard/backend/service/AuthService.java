package com.talentsboard.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.talentsboard.backend.dto.AuthRequest;
import com.talentsboard.backend.dto.AuthResponse;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.model.UserType;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service d'authentification qui interagit avec Firebase Admin SDK.
 *
 * Note : la vérification exacte du mot de passe est généralement faite côté client via Firebase Client SDK.
 * Ici on crée des utilisateurs et on génère des tokens custom si nécessaire.
 */
@Service
public class AuthService {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    /**
     * Inscrit un nouvel utilisateur sur Firebase Auth et retourne le profil User (avec id mis à jour).
     *
     * @param user       Profil utilisateur (nom, email, etc.)
     * @param motDePasse Mot de passe plain-text (sera envoyé à Firebase)
     * @return User mis à jour (id = uid Firebase)
     * @throws FirebaseAuthException si erreur Firebase
     */
    public User register(User user, String motDePasse) throws FirebaseAuthException {
        // Création de la requête pour Firebase
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(motDePasse)
                .setDisplayName(user.getNom());

        // Crée l'utilisateur dans Firebase Auth
        UserRecord firebaseUser = firebaseAuth.createUser(request);

        // Récupère l'UID Firebase et l'assigne au profil local
        user.setId(firebaseUser.getUid());

        // TODO : Persister le profil User dans Firestore / BDD (ex : collection "users")
        // Ex: firestore.collection("users").document(user.getId()).set(user);

        return user;
    }

    /**
     * "Login" : récupère l'utilisateur par email et génère un token custom Firebase
     * (dans les architectures utilisant Firebase Client SDK, la vérification motDePasse
     * est faite côté client; ici on génère un token custom côté serveur).
     *
     * @param request AuthRequest contenant email et motDePasse
     * @return AuthResponse avec token et userId
     * @throws FirebaseAuthException si erreur avec Firebase
     */
    public AuthResponse login(AuthRequest request) throws FirebaseAuthException {
        // Récupérer l'utilisateur par email
        UserRecord userRecord = firebaseAuth.getUserByEmail(request.getEmail());

        // Ici, si tu veux vérifier le mot de passe côté serveur, tu devrais utiliser une solution
        // d'auth interne ou verifier via Firebase REST API (non recommandé — privilégier client SDK).
        // Nous générons un token custom pour ce user.
        String customToken = firebaseAuth.createCustomToken(userRecord.getUid());

        return new AuthResponse(customToken, userRecord.getUid());
    }

    /**
     * Génère un token custom pour l'UID donné (utile pour tests ou intégrations).
     *
     * @param uid UID Firebase
     * @return token custom
     * @throws FirebaseAuthException si erreur
     */
    public String generateToken(String uid) throws FirebaseAuthException {
        return firebaseAuth.createCustomToken(uid);
    }
}
