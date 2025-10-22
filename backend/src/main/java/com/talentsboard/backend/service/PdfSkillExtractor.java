package com.talentsboard.backend.service;

import com.talentsboard.backend.util.TechDictionary;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import opennlp.tools.tokenize.SimpleTokenizer;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service d'extraction de texte et détection de technologies / skills
 * depuis un CV PDF.
 * Délègue l'extraction des SKILLS, LANGUES et BENEFITS au service orchestrateur.
 */
@Slf4j
@Service
@AllArgsConstructor
public class
PdfSkillExtractor {

    private final Tika tika = new Tika();
    private final ExtractionCoreService extractionCoreService; // 💡 Injection

    /**
     * Extrait texte et compétences (maintenant via l'orchestrateur) depuis un PDF.
     */
    public Map<String, Object> extract(InputStream pdfStream) {
        Map<String, Object> res = new HashMap<>();
        try {
            // ... (Logique d'extraction Tika inchangée) ...
            AutoDetectParser parser = new AutoDetectParser();
            PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setExtractInlineImages(false);
            pdfConfig.setExtractUniqueInlineImagesOnly(true);

            ParseContext context = new ParseContext();
            context.set(PDFParserConfig.class, pdfConfig);

            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();

            parser.parse(pdfStream, handler, metadata, context);

            String text = handler.toString();
            text = normalizeExtractedText(text);
            res.put("text", text);

            // ------------------ 💡 DÉLÉGATION À L'ORCHESTRATEUR ------------------

            // Extraction des compétences (mutualisée)
            List<String> skills = extractionCoreService.extractSkills(text);

            // Extraction des avantages (mutualisée)
//            List<String> benefits = extractionCoreService.extractBenefits(text);

            // Extraction des langues (mutualisée et AMÉLIORÉE)
            List<String> languages = extractionCoreService.extractLanguages(text);

            // Extraction type de contrat
            String typeContrat = extractionCoreService.extractTypeContrat(text);
            res.put("typeContrat", typeContrat);

            // ---------------------------------------------------------------------

            res.put("skills", skills);
//            res.put("benefits", benefits);
            res.put("languages", languages); // Ajout des langues à la sortie du CV

        } catch (Exception e) {
            log.error("Erreur extraction PDF", e);
            res.put("text", "");
            res.put("skills", Collections.emptyList());
            res.put("benefits", Collections.emptyList());
            res.put("languages", Collections.emptyList());
        }
        return res;
    }

    public static String normalizeExtractedText(String raw) {
        // ... (Méthode inchangée) ...
        if (raw == null) return "";

        String text = raw;
        text = text.replaceAll("\\p{Cntrl}", " ");
        text = text.replaceAll("\\n+", "\n");
        text = Arrays.stream(text.split("\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.joining("\n"));
        text = text.replaceAll("\\s{2,}", " ");
        text = text.replaceAll("(?<=\\w)-\\s+(?=\\w)", "");
        return text.trim();
    }
}