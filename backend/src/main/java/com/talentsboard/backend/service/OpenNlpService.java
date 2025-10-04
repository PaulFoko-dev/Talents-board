package com.talentsboard.backend.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import opennlp.tools.namefind.*;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.Span;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service d'enveloppe autour d'OpenNLP.
 * - charge les modèles depuis /resources/models
 * - expose méthodes pour retrouver organisations, lieux, dates, tokens...
 *
 * Conception :
 * - on charge les TokenizerModel et SentenceModel une seule fois.
 * - pour NameFinder on conserve les TokenNameFinderModel et crée NameFinderME à la demande
 *   (safe et évite problèmes d'état adaptatif).
 *
 * Si un modèle manque, on loggue et on retourne une liste vide (fallback).
 */
@Service
public class OpenNlpService {

    private final Log log = LogFactory.getLog(OpenNlpService.class);

    private final TokenizerModel tokenizerModel;
    private final SentenceModel sentenceModel;
    private final TokenNameFinderModel locModel;
    private final TokenNameFinderModel orgModel;
    private final TokenNameFinderModel dateModel;

    public OpenNlpService() {
        this.tokenizerModel = loadTokenizerModel("/models/fr-token.bin");
        this.sentenceModel  = loadSentenceModel("/models/fr-sent.bin");
        this.locModel       = loadNameFinderModel("/models/fr-ner-location.bin");
        this.orgModel       = loadNameFinderModel("/models/fr-ner-organization.bin");
        this.dateModel      = loadNameFinderModel("/models/fr-ner-date.bin");
    }

    private TokenizerModel loadTokenizerModel(String resource) {
        try (InputStream is = getClass().getResourceAsStream(resource)) {
            if (is == null) {
                log.warn("Tokenizer model absent: " + resource);
                return null;
            }
            return new TokenizerModel(is);
        } catch (Exception e) {
            log.error("Erreur chargement tokenizer model " + resource, e);
            return null;
        }
    }

    private SentenceModel loadSentenceModel(String resource) {
        try (InputStream is = getClass().getResourceAsStream(resource)) {
            if (is == null) {
                log.warn("Sentence model absent: " + resource);
                return null;
            }
            return new SentenceModel(is);
        } catch (Exception e) {
            log.error("Erreur chargement sentence model " + resource, e);
            return null;
        }
    }

    private TokenNameFinderModel loadNameFinderModel(String resource) {
        try (InputStream is = getClass().getResourceAsStream(resource)) {
            if (is == null) {
                log.warn("NameFinder model absent: " + resource);
                return null;
            }
            return new TokenNameFinderModel(is);
        } catch (Exception e) {
            log.error("Erreur chargement namefinder model " + resource, e);
            return null;
        }
    }

    /**
     * Tokenize un texte en jetons. Si le tokenizer est absent, fallback naïf (split on whitespace).
     */
    public String[] tokenize(String text) {
        if (tokenizerModel != null) {
            TokenizerME tokenizer = new TokenizerME(tokenizerModel);
            return tokenizer.tokenize(text);
        } else {
            return text == null ? new String[0] : text.split("\\s+");
        }
    }

    /**
     * Extrait des entités via NameFinder model (ex: LOCATION, ORGANIZATION, DATE).
     * @param text texte
     * @param model token name finder model (peut être null)
     */
    private List<String> findByModel(String text, TokenNameFinderModel model) {
        if (text == null || model == null) return Collections.emptyList();
        String[] tokens = tokenize(text);
        NameFinderME finder = new NameFinderME(model);
        Span[] spans = finder.find(tokens);
        // reconstruire la string depuis tokens
        List<String> found = new ArrayList<>();
        for (Span s : spans) {
            StringBuilder sb = new StringBuilder();
            for (int i = s.getStart(); i < s.getEnd(); i++) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(tokens[i]);
            }
            found.add(sb.toString());
        }
        // clear adaptive data (bonnes pratiques)
        finder.clearAdaptiveData();
        // return distinct preserving order
        return found.stream().distinct().collect(Collectors.toList());
    }

    public List<String> findLocations(String text) {
        return findByModel(text, locModel);
    }

    public List<String> findOrganizations(String text) {
        return findByModel(text, orgModel);
    }

    public List<String> findDates(String text) {
        return findByModel(text, dateModel);
    }

    // utile pour debug : retourne sentence list si sentenceModel présent
    public List<String> splitSentences(String text) {
        if (text == null) return Collections.emptyList();
        if (sentenceModel != null) {
            SentenceDetectorME sd = new SentenceDetectorME(sentenceModel);
            return Arrays.asList(sd.sentDetect(text));
        }
        return Arrays.asList(text.split("[\\n\\.]"));
    }
}
