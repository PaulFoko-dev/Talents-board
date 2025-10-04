package com.talentsboard.backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Objects;

@Slf4j
@Service
public class FileStorageService {

    private final Path rootDir;
    // limite par fichier (ex. 5 MB)
    private final long MAX_FILE_SIZE;

    public FileStorageService(@Value("${file.upload.dir}") String uploadDir,
                              @Value("${file.upload.max-size-bytes:5242880}") long maxSizeBytes) {
        this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.MAX_FILE_SIZE = maxSizeBytes;
        try {
            Files.createDirectories(this.rootDir);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier d'upload: " + this.rootDir, e);
        }
    }

    /**
     * Validate and store CV under {root}/{ownerUid}/{ticketOrUid}.pdf
     * Returns the absolute path string to store in Firestore (User.cvPath).
     *
     * Throws IOException on IO errors, IllegalArgumentException on validation failure.
     */
    public String storeCv(MultipartFile file, String ownerUid) throws IOException {
        validateFileIsPdfAndSize(file);

        // Chaque utilisateur a son propre dossier
        Path userDir = this.rootDir.resolve(ownerUid);
        Files.createDirectories(userDir);

        // Nom du fichier CV standardisé : uid.pdf
        String filename = ownerUid + ".pdf";
        Path target = userDir.resolve(filename).toAbsolutePath().normalize();

        // Protection contre Path Traversal
        if (!target.startsWith(userDir.toAbsolutePath())) {
            throw new IllegalArgumentException("Invalid filename leading to path traversal");
        }

        // Copie sécurisée avec remplacement si un CV existe déjà
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        Path relativePath = this.rootDir.relativize(target);

        log.info("CV stored for uid={} at absolute path: {}, recording relative path: {}",
                ownerUid, target, relativePath);

        return relativePath.toString();
    }


    /**
     * Returns InputStream for streaming; throws FileNotFoundException if absent.
     */
    public InputStream getCvStream(String cvPath) throws IOException {
        // 1. Reconstruit le chemin absolu à partir de la racine d'upload et du chemin relatif stocké
        Path p = this.rootDir.resolve(cvPath).normalize();

        // 2. Vérification de sécurité supplémentaire (s'assurer que le chemin est bien dans rootDir)
        if (!p.startsWith(this.rootDir)) {
            throw new SecurityException("Tentative d'accès en dehors du répertoire d'upload: " + cvPath);
        }

        if (!Files.exists(p)) throw new FileNotFoundException("CV not found: " + cvPath);

        return Files.newInputStream(p, StandardOpenOption.READ);
    }

    /**
     * Delete file at path; returns true if deleted.
     */
    public boolean deleteCv(String cvPath) throws IOException {
        Path p = this.rootDir.resolve(cvPath).normalize();

        // 2. Vérification de sécurité
        if (!p.startsWith(this.rootDir)) return false;

        boolean result = Files.deleteIfExists(p);
        if (result) log.info("CV deleted: {}", cvPath);
        return result;
    }

    /**
     * Simple validation:
     * - MultiparFile not empty
     * - size <= MAX_FILE_SIZE
     * - contentType application/pdf
     * - and magic bytes start with "%PDF"
     */
    public void validateFileIsPdfAndSize(MultipartFile file) throws IOException {
        Objects.requireNonNull(file, "file must not be null");
        if (file.isEmpty()) throw new IllegalArgumentException("File vide");
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Fichier trop volumineux (max " + MAX_FILE_SIZE + " bytes)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            // Some clients may send application/octet-stream; still we do magic check below
            log.warn("contentType non standard pour upload: {}", contentType);
        }

        // Magic bytes check: first 4 bytes should be %PDF
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int read = is.read(header);
            if (read < 4) throw new IllegalArgumentException("Fichier non reconnu comme PDF");
            String magic = new String(header);
            if (!magic.startsWith("%PDF")) {
                throw new IllegalArgumentException("Fichier non reconnu comme PDF (magic mismatch)");
            }
        }
    }
}
