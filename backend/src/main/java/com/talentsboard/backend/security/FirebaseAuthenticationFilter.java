package com.talentsboard.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
// importez votre propre classe UnauthorizedException si vous l'utilisez ailleurs
// import com.talentsboard.backend.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Filtre qui :
 * - vérifie la présence du header Authorization: Bearer <idToken>
 * - décodifie l'idToken via FirebaseAuth.verifyIdToken()
 * - injecte l'Authentication avec ROLE_<CLAIM>
 * - renvoie des erreurs JSON structurées en cas de problème
 */
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        // Exclure les routes publiques (Swagger et Auth) du filtrage par jeton
        return p.startsWith("/v3/api-docs") ||
                p.startsWith("/swagger-ui") ||
                p.startsWith("/api/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        // 1. Vérification du header
        if (header == null || !header.startsWith("Bearer ")) {
            // Permettre la suite si l'authentification est facultative (p.ex. pour les ressources publiques)
            // Mais puisque votre SecurityConfig.anyRequest().authenticated(), nous bloquons ici.
            // Cependant, le filtre AnonymousAuthenticationFilter gère souvent ce cas.
            // Ici, nous renvoyons une erreur pour forcer une réponse JSON en cas de token manquant.
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token d'authentification manquant");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        FirebaseToken decoded;

        // 2. Vérification et décodage du token
        try {
            decoded = FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token invalide ou expiré: " + e.getMessage());
            return;
        }

        // 3. Extraction du rôle et création de l'objet Authentication
        Object roleObj = decoded.getClaims().get("role");
        // Rôle par défaut si le claim est manquant
        String role = roleObj != null ? roleObj.toString().toUpperCase() : "CANDIDAT";

        // S'assurer que le rôle est préfixé par ROLE_
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                decoded.getUid(),
                null, // Le jeton n'est pas stocké dans le Principal pour des raisons de sécurité
                List.of(new SimpleGrantedAuthority(authority))
        );

        // 4. Mettre l'objet Authentication dans le SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Continuer la chaîne de filtres Spring Security
        chain.doFilter(request, response);
    }

    /**
     * Envoie une réponse d'erreur formatée en JSON.
     * Cette méthode est cruciale pour éviter que Tomcat/Spring ne renvoie des erreurs HTML.
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        // La méthode setStatus doit être appelée AVANT que l'écriture ne commence
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String,Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", status == HttpServletResponse.SC_UNAUTHORIZED ? "Unauthorized" : "Forbidden");
        body.put("message", message);

        // Utilisation de ObjectMapper pour écrire le JSON
        String json = mapper.writeValueAsString(body);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}