package com.talentsboard.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.talentsboard.backend.exception.UnauthorizedException;
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
        return p.startsWith("/v3/api-docs") ||
                p.startsWith("/swagger-ui") ||
                p.startsWith("/api/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token manquant");
            return;
        }
        String token = header.substring(7);
        FirebaseToken decoded;
        try {
            decoded = FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token invalide ou expiré");
            return;
        }

        // récupérer le role des custom claims
        Object roleObj = decoded.getClaims().get("role");
        String role = roleObj != null ? roleObj.toString() : "CANDIDAT";
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                decoded.getUid(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        Map<String,Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", status == HttpServletResponse.SC_UNAUTHORIZED ? "Unauthorized" : "Forbidden");
        body.put("message", message);
        String json = mapper.writeValueAsString(body).replaceAll("\\p{Cntrl}", ""); // retrait ctrl chars
        response.getWriter().write(json);
    }
}
