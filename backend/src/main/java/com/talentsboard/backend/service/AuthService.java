package com.talentsboard.backend.service;

import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.talentsboard.backend.config.FirebaseTokenExchange;
import com.talentsboard.backend.dto.AuthRequest;
import com.talentsboard.backend.dto.AuthResponse;
import com.talentsboard.backend.exception.EmailAlreadyExistsException;
import com.talentsboard.backend.model.User;
import com.talentsboard.backend.model.UserType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.talentsboard.backend.util.ApiKey.FIREBASE_WEB_API_KEY;

/**
 * AuthService :
 * - crée l'utilisateur dans FirebaseAuth,
 * - définit custom claims (role),
 * - stocke le profil dans Firestore via UserService.
 * - login() : vérifie email+password via Firebase Identity REST API et retourne un idToken.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    /**
     * Crée un utilisateur synchronisé (FirebaseAuth + Firestore).
     * @param user entité avec champs de base (type présent)
     * @param motDePasse mot de passe en clair (sera géré par Firebase Auth)
     */
    public User register(User user, String motDePasse) throws Exception {
        // protection : email unique côté Firestore (et FirebaseAuth renverra erreur si existant)
        if (userService.findByEmail(user.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email déjà utilisé");
        }
        String uid = null;
        try {
            // 1) créer l'utilisateur dans FirebaseAuth
            UserRecord.CreateRequest req = new UserRecord.CreateRequest()
                    .setEmail(user.getEmail())
                    .setPassword(motDePasse)
                    .setDisplayName(user.getNom());
            UserRecord created = FirebaseAuth.getInstance().createUser(req);
            uid = created.getUid();

            // 2) set custom claim role (string)
            Map<String,Object> claims = new HashMap<>();
            claims.put("role", user.getType() != null ? user.getType().name() : UserType.CANDIDAT.name());
            FirebaseAuth.getInstance().setCustomUserClaims(created.getUid(), claims);

            // 3) synchroniser Firestore : save profile avec l'uid
            user.setId(created.getUid());
            User saved = userService.createUser(user);
            return saved;
        } catch (Exception e) {
            // 3. Rollback FirebaseAuth si Firestore échoue
            if (uid != null) {
                FirebaseAuth.getInstance().deleteUser(uid);
            }
            System.out.println("Échec de l’inscription du candidat : " + e.getMessage());
            throw new BadRequestException("Échec de l’inscription du candidat : " + e.getMessage());
        }
    }

    /**
     * Login sécurisé : vérifie email+password via Firebase REST endpoint (signInWithPassword)
     * et retourne l'idToken (JWT) utilisable côté client/backend.
     */
    public AuthResponse login(AuthRequest request) {
        try {
            // utilisation directe de l'API Identity Toolkit : signInWithPassword
            // see: https://firebase.google.com/docs/reference/rest/auth
            // Implementation via helper class FirebaseTokenExchange (ou code similaire)
            // We call signInWithPassword endpoint:
            // https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=[API_KEY]
            // Here we reuse FirebaseTokenExchange for custom token exchange only; implement direct HTTP call:

            // Simple approach: reuse FirebaseTokenExchange with password flow inline:
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_WEB_API_KEY;
            java.util.Map<String,Object> body = new java.util.HashMap<>();
            body.put("email", request.getEmail());
            body.put("password", request.getMotDePasse());
            body.put("returnSecureToken", true);

            org.springframework.web.client.RestTemplate rt = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.writeValueAsString(body);

            org.springframework.http.HttpEntity<String> ent = new org.springframework.http.HttpEntity<>(json, headers);
            org.springframework.http.ResponseEntity<java.util.Map> resp = rt.postForEntity(url, ent, java.util.Map.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new RuntimeException("Authentification échouée");
            }

            String idToken = (String) resp.getBody().get("idToken");
            String uid = (String) resp.getBody().get("localId");

            return new AuthResponse(idToken, uid);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            throw new RuntimeException("Identifiants invalides");
        } catch (Exception e) {
            throw new RuntimeException("Erreur login : " + e.getMessage(), e);
        }
    }

    /**
     * Supprime l'utilisateur : synchronise FirebaseAuth + Firestore.
     */
    public void deleteUser(String uid) throws Exception {
        // delete from Firestore
        userService.deleteUser(uid);
        // delete from FirebaseAuth
        FirebaseAuth.getInstance().deleteUser(uid);
    }

    /**
     * Update profile : si e-mail changé, on met à jour FirebaseAuth également.
     * N'autorise pas la modification du role ici (utiliser endpoint Admin).
     */
    public User updateUserProfile(String uid, User updated) throws Exception {
        // charger existant
        User existing = userService.getUserById(uid);
        if (existing == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        // synchroniser certain champs dans FirebaseAuth si besoin
        if (updated.getEmail() != null && !updated.getEmail().equals(existing.getEmail())) {
            // update email in FirebaseAuth
            UserRecord.UpdateRequest ur = new UserRecord.UpdateRequest(uid).setEmail(updated.getEmail());
            FirebaseAuth.getInstance().updateUser(ur);
        }
        if (updated.getNom() != null && !updated.getNom().equals(existing.getNom())) {
            UserRecord.UpdateRequest ur = new UserRecord.UpdateRequest(uid).setDisplayName(updated.getNom());
            FirebaseAuth.getInstance().updateUser(ur);
        }

        // appliquer update côté Firestore
        existing.setNom(updated.getNom() != null ? updated.getNom() : existing.getNom());
        existing.setDescription(updated.getDescription() != null ? updated.getDescription() : existing.getDescription());
        existing.setPrenom(updated.getPrenom() != null ? updated.getPrenom() : existing.getPrenom());
        existing.setCompetences(updated.getCompetences() != null ? updated.getCompetences() : existing.getCompetences());
        existing.setCv(updated.getCv() != null ? updated.getCv() : existing.getCv());
        existing.setPhotoProfil(updated.getPhotoProfil() != null ? updated.getPhotoProfil() : existing.getPhotoProfil());
        existing.setLogo(updated.getLogo() != null ? updated.getLogo() : existing.getLogo());
        existing.setSecteur(updated.getSecteur() != null ? updated.getSecteur() : existing.getSecteur());
        existing.setLocalisation(updated.getLocalisation() != null ? updated.getLocalisation() : existing.getLocalisation());
        existing.setSiteWeb(updated.getSiteWeb() != null ? updated.getSiteWeb() : existing.getSiteWeb());

        return userService.updateUser(existing);
    }

    /**
     * Update role (Admin only) : set custom claim + revoke tokens.
     */
    public void updateUserRole(String uid, String role) throws Exception {
        Map<String,Object> claims = new HashMap<>();
        claims.put("role", role);
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
        // revoke tokens so client must refresh and receive updated claims
        FirebaseAuth.getInstance().revokeRefreshTokens(uid);
    }

    @PostConstruct
    public void initAdmin() {
        try {
            // Vérifie si un admin existe déjà
            boolean exists = !FirestoreClient.getFirestore().collection("users")
                    .whereEqualTo("type", "ADMIN")
                    .get()
                    .get()
                    .isEmpty();

            if (!exists) {
                // Création manuelle du compte ADMIN
                UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                        .setEmail("admin@talentsboard.com")
                        .setPassword("ChangeThisPassword123!") // ⚠️ à changer immédiatement
                        .setDisplayName("Default Admin");

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

                // Ajout du custom claim pour rôle ADMIN
                Map<String, Object> claims = new HashMap<>();
                claims.put("role", "ADMIN");
                FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);

                // Ajout dans Firestore
                User admin = new User();
                admin.setId(userRecord.getUid());
                admin.setEmail(userRecord.getEmail());
                admin.setNom("Default");
                admin.setPrenom("Admin");
                admin.setType(UserType.ADMIN); // ✅ rôle ADMIN
                admin.setDateInscription(Timestamp.now());

                FirestoreClient.getFirestore().collection("users").document(admin.getId()).set(admin).get();

                System.out.println("✅ ADMIN créé : " + admin.getEmail());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur création ADMIN : " + e.getMessage(), e);
        }
    }
    /**
     * Vérifie un idToken (JWT) et retourne le uid.
     * Lance une exception si le token est invalide/expiré.
     */
    public String verifyTokenAndGetUid(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return decodedToken.getUid();
    }
}
