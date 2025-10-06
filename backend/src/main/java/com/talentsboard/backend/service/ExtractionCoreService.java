package com.talentsboard.backend.service;

import com.talentsboard.backend.util.TechDictionary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;
        import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service central d'orchestration et de mutualisation de la logique d'extraction
 * (Langues, Compétences, Télétravail, Salaire, Avantages).
 * Utilisé à la fois par l'extraction d'Annonce et l'extraction de CV/PDF.
 */
@Slf4j
@Service
public class ExtractionCoreService {

    // REGEX pour Télétravail, Salaire, Avantages (mutalisés ici pour éviter la redondance)
    private static final Pattern SALARY_RANGE_REGEX = Pattern.compile(
            "(\\d[\\d\\s\\.,kK]+)\\s?(?:-|to|–|—)\\s?(\\d[\\d\\s\\.,kK]+)\\s?(?:€|EUR|euros?)?",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern SALAIRE_SIMPLE_REGEX = Pattern.compile(
            "(\\d{2,3}(?:[\\s\\.,']?\\d{3})*)(?:\\s?(k|K))?\\s?(?:€|EUR|euros?|brut|annuel|/an|/mois|mensuel|anual)?",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern AVANTAGES_REGEX = Pattern.compile(
            "\\b(Mutuelle|Tickets? Restaurant|RTT|Pr[iî]me|Participation|Int[eé]ressement|CE|Comit[eé] d['’]?entreprise|Transport|Budget formation|Paniers? Repas|Carte\\s+cadeau)\\b", // Élargi un peu
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);

    // Télétravail
    private static final Pattern NO_REMOTE_REGEX = Pattern.compile(
            "\\b(pas de t[eé]l[eé]travail|t[eé]l[eé]travail non possible|sans t[eé]l[eé]travail|t[eé]l[eé]travail: non)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern FULL_REMOTE_REGEX = Pattern.compile(
            "\\b(?:100%\\s*(?:t[eé]l[eé]travail|remote)|(?:full|total|compl(?:et|e))\\s*(?:remote|t[eé]l[eé]travail)|remote\\s*full|full remote accept[eé])\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern PERCENT_REMOTE_REGEX = Pattern.compile(
            "\\b(\\d{1,3})\\s?%\\s*(?:t[eé]l[eé]travail|remote)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern DAYS_PER_WEEK_REGEX = Pattern.compile(
            "\\b(?:(?:t[eé]l[eé]travail)[:\\s]*)?(?:jusqu'?\\s*à\\s*)?(\\d{1,2})\\s*(?:j|jours?)(?:/semaine|\\s+semaine)?\\b", // amélioré pour capter "/semaine"
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern HYBRID_REGEX = Pattern.compile(
            "\\b(hybride|hybrid|mixte|mixte\\s*présentiel)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern OPTIONAL_REMOTE_REGEX = Pattern.compile(
            "\\b(t[eé]l[eé]travail possible|t[eé]l[eé]travail envisageable|remote possible|remote? (?:possible|envisageable|selon))\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern CONTRAT_REGEX = Pattern.compile(
            "\\b(CDI|CDD|Stage|Alternance|Apprentissage|Freelance|Ind[eé]pendant|Int[eé]rim|Temps\\s+Plein|Temps\\s+Partiel|Dur[eé]e\\s+ind[eé]termin[eé]e|Dur[eé]e\\s+d[eé]termin[eé]e)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);

    private static final Map<String, String> LEVEL_MAPPING = new HashMap<>();
    static {
        LEVEL_MAPPING.put("bilingue|natif|maternelle", "Natif/Bilingue");
        LEVEL_MAPPING.put("courant|professionnel|business", "Courant/Professionnel (C1/C2)");
        LEVEL_MAPPING.put("bon niveau|intermédiaire|avancé|c1|b2|upper-intermediate|fluent", "Avancé/Intermédiaire (B2/C1)");
        LEVEL_MAPPING.put("notions|scolaire|débutant|debutant|a1|a2|b1|basic", "Notions/Scolaire (A1-B1)");
    }
    private static final Map<String, String> LANG_MAPPING = new HashMap<>();
    static {
        LANG_MAPPING.put("anglais|english|toefl|toeic|ielts", "Anglais");
        LANG_MAPPING.put("français|francais|fr", "Français");
        LANG_MAPPING.put("espagnol|espagnole|spanish", "Espagnol");
        LANG_MAPPING.put("allemand|allemande|german", "Allemand");
        LANG_MAPPING.put("chinois|mandarin", "Chinois (Mandarin)");
        LANG_MAPPING.put("portugais|portugaise", "Portugais");
        LANG_MAPPING.put("italien|italienne", "Italien");
        // ... Ajouter d'autres langues ...
    }
    private static final String NEGATION_PATTERN = "\\b(?:pas\\s+d'|ne\\s+.*\\s+pas|non\\s+|zero|aucune?|non\\s+requis)\\s+";


    // -----------------------------------------------------------------------------------------------------------------
    // --- Méthodes d'Extraction Mutualisées ---
    // -----------------------------------------------------------------------------------------------------------------

    public String extractTypeContrat(String rawtext){
        // --- contrat (spécifique annonce) ---
        Matcher mContrat = CONTRAT_REGEX.matcher(rawtext);
        if (mContrat.find()) return (mContrat.group(1));
        return null;
    }

    /**
     * Extrait les compétences techniques en utilisant le TechDictionary.
     * @param rawText Texte brut (annonce ou CV).
     * @return Liste distincte des compétences standardisées.
     */
    public List<String> extractSkills(String rawText) {
        if (StringUtils.isBlank(rawText)) return Collections.emptyList();

        String lower = rawText.toLowerCase(Locale.ROOT);
        Set<String> found = new HashSet<>();

        // Recherche directe par `lower.contains` pour capturer les bigrams et trigrams
        for (Map.Entry<String, String> entry : TechDictionary.TECHNOLOGIES.entrySet()) {
            String key = entry.getKey().toLowerCase(Locale.ROOT);
            // Utiliser Pattern pour garantir la limite du mot (word boundary) pour les termes courts
            // et une simple vérification contains pour les termes longs (qui sont plus uniques).
            if (key.length() < 5) {
                Pattern wordPat = Pattern.compile("\\b" + Pattern.quote(key) + "\\b", Pattern.UNICODE_CHARACTER_CLASS);
                if (wordPat.matcher(lower).find()) {
                    found.add(entry.getValue());
                }
            } else if (lower.contains(key)) {
                found.add(entry.getValue());
            }
        }

        return found.stream().distinct().sorted().collect(Collectors.toList());
    }

    /**
     * Extrait les langues et leurs niveaux de compétence.
     * @param rawText Texte brut (annonce ou CV).
     * @return Liste des langues avec niveau standardisé (ex: "Anglais (Courant/Professionnel)").
     */
    public List<String> extractLanguages(String rawText) {
        if (StringUtils.isBlank(rawText)) return Collections.emptyList();

        String lower = rawText.toLowerCase(Locale.ROOT);
        List<String> extractedLangs = new ArrayList<>();

        for (Map.Entry<String, String> entry : LANG_MAPPING.entrySet()) {
            String aliases = entry.getKey();
            String standardName = entry.getValue();

            // Pattern: (Négation)?(Langue)
            Pattern langPattern = Pattern.compile("(?<negation>" + NEGATION_PATTERN + ")?(?<lang>" + aliases + ")", Pattern.UNICODE_CHARACTER_CLASS);
            Matcher langMatcher = langPattern.matcher(lower);

            if (langMatcher.find()) {
                // Vérifier si la langue a été trouvée sans négation explicite
                if (langMatcher.group("negation") == null) {

                    String level = "Non Spécifié";

                    // Recherche du niveau dans une fenêtre de 40 caractères autour du mot-clé
                    String searchWindow = getSearchWindow(lower, langMatcher.start(), langMatcher.end(), 40);

                    for (Map.Entry<String, String> levelEntry : LEVEL_MAPPING.entrySet()) {
                        // Utilisation de Pattern pour les niveaux composés (ex: "bon niveau")
                        Pattern levelPattern = Pattern.compile(levelEntry.getKey(), Pattern.UNICODE_CHARACTER_CLASS);
                        if (levelPattern.matcher(searchWindow).find()) {
                            level = levelEntry.getValue();
                            break; // Le niveau le plus élevé trouvé est retenu
                        }
                    }

                    // Évite l'ajout de doublons (ex: si "anglais bilingue" et "anglais courant" sont trouvés)
                    String finalLang = standardName + " (" + level + ")";
                    if (!extractedLangs.contains(finalLang) && !extractedLangs.stream().anyMatch(l -> l.startsWith(standardName))) {
                        extractedLangs.add(finalLang);
                    }
                }
            }
        }

        // S'assurer que le Français est inclus avec un niveau par défaut s'il est mentionné.
        if (lower.contains("français") || lower.contains("francais")) {
            String frenchLang = "Français";
            if (!extractedLangs.stream().anyMatch(l -> l.startsWith(frenchLang))) {
                extractedLangs.add(frenchLang + " (Natif/Bilingue)");
            }
        }

        return extractedLangs.stream().distinct().sorted().collect(Collectors.toList());
    }

    /**
     * Extrait les avantages sociaux.
     * @param rawText Texte brut (annonce ou CV).
     * @return Liste distincte des avantages trouvés.
     */
    public List<String> extractBenefits(String rawText) {
        if (StringUtils.isBlank(rawText)) return Collections.emptyList();

        Set<String> avantages = new HashSet<>();
        Matcher mAv = AVANTAGES_REGEX.matcher(rawText);

        while (mAv.find()) {
            // Normalisation de la capitalisation de l'avantage (ex: "Tickets Restaurant")
            String normalizedBenefit = StringUtils.capitalize(mAv.group(1).toLowerCase(Locale.ROOT));
            // Correction pour les pluriels courants mal capitalisés:
            if (normalizedBenefit.contains("Tickets") || normalizedBenefit.contains("Paniers")) {
                normalizedBenefit = normalizedBenefit.replace("Tickets", "Tickets").replace("Paniers", "Paniers");
            }
            avantages.add(normalizedBenefit);
        }

        return avantages.stream().distinct().toList();
    }

    /**
     * Extrait et normalise les informations de télétravail.
     * @param rawText Texte brut.
     * @return Map contenant mode ("FULL_REMOTE", "PARTIAL_REMOTE", etc.), jours et pourcentage.
     */
    public Map<String, Object> extractTeletravailInfo(String rawText) {
        String mode = null;
        Double percent = null;
        Integer days = null;
        String lower = rawText.toLowerCase(Locale.ROOT);

        if (NO_REMOTE_REGEX.matcher(rawText).find()) {
            mode = "ONSITE";
        } else if (FULL_REMOTE_REGEX.matcher(rawText).find()) {
            mode = "FULL_REMOTE";
            percent = 100.0;
            days = 5;
        } else {
            // 1. Pourcentage
            Matcher mPercent = PERCENT_REMOTE_REGEX.matcher(rawText);
            if (mPercent.find()) {
                try {
                    int perc = Integer.parseInt(mPercent.group(1));
                    mode = "PARTIAL_REMOTE";
                    percent = (double) perc;
                } catch (NumberFormatException ignored) {}
            }

            // 2. Jours par semaine
            Matcher mDays = DAYS_PER_WEEK_REGEX.matcher(rawText);
            if (mDays.find()) {
                try {
                    int d = Integer.parseInt(mDays.group(1));
                    if (d >= 0 && d <= 7) {
                        mode = "PARTIAL_REMOTE";
                        days = d;
                    }
                } catch (NumberFormatException ignored) {}
            }

            // 3. Mots-clés hybrides/optionnels
            if (mode == null && HYBRID_REGEX.matcher(rawText).find()) mode = "HYBRID";
            if (mode == null && OPTIONAL_REMOTE_REGEX.matcher(rawText).find()) mode = "OPTIONAL_REMOTE";

            // 4. Fallback générique
            if (mode == null && (lower.contains("télétravail") || lower.contains("teletravail") || lower.contains("remote")))
                mode = "OPTIONAL_REMOTE";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("modeTravail", mode);
        result.put("teletravailJourParSemaine", days);
        result.put("teletravailPourcentage", percent);

        // Calculer les jours équivalents pour le résultat final
        Map<String, Object> teleInfo = parseModeTravail(mode, days, percent);
        result.put("modeTravailRaw", teleInfo.get("raw"));
        result.put("teletravailEquivalentDaysPerWeek", teleInfo.get("daysPerWeek"));

        return result;
    }


    // -----------------------------------------------------------------------------------------------------------------
    // --- Méthodes de SALAIRE et Helpers (Déménagées depuis ExtractAnnonceService) ---
    // -----------------------------------------------------------------------------------------------------------------

    public Map<String, Double> extractSalaireRange(String rawText) {
        Map<String, Double> result = new HashMap<>();

        // Cas Range explicite : "48 000 - 60 000 €"
        Matcher mRange = SALARY_RANGE_REGEX.matcher(rawText);
        if (mRange.find()) {
            Double min = parseSalaryNumber(mRange.group(1));
            Double max = parseSalaryNumber(mRange.group(2));

            if (min != null && max != null) {
                // S'assurer que min < max (inversion possible par erreur de saisie)
                if (min > max) {
                    double tmp = min; min = max; max = tmp;
                }
                result.put("min", min);
                result.put("max", max);
                return result;
            }
        }

        // Cas Montants isolés
        Matcher mSalaire = SALAIRE_SIMPLE_REGEX.matcher(rawText);
        List<Double> found = new ArrayList<>();
        while (mSalaire.find()) {
            Double num = parseSalaryNumber(mSalaire.group(1));
            String k = mSalaire.group(2);

            if (num != null && k != null && k.equalsIgnoreCase("k")) {
                num *= 1000.0;
            }

            // Filtre : pour éviter d’ajouter codes postaux, numéros de téléphone ou petits nombres
            if (num != null && num >= 10000) { // On met le seuil à 10k pour être sûr que c'est un salaire.
                found.add(num);
            }
        }

        if (!found.isEmpty()) {
            result.put("min", Collections.min(found));
            if (found.size() > 1) {
                result.put("max", Collections.max(found));
            }
        }
        return result;
    }

    private Double parseSalaryNumber(String raw) {
        if (raw == null) return null;
        String s = raw.replaceAll("[^0-9\\.,kK]", "").replace(',', '.').trim();
        try {
            if (s.endsWith("k") || s.endsWith("K")) {
                return Double.parseDouble(s.substring(0, s.length()-1)) * 1000.0;
            } else {
                // Supprimer tous les séparateurs pour éviter les erreurs de format (123 456 -> 123456)
                s = s.replaceAll("[\\s\\.]", "");
                return Double.parseDouble(s);
            }
        } catch (Exception e) {
            log.debug("parseSalaryNumber failed for '{}': {}", raw, e.getMessage());
            return null;
        }
    }

    /**
     * Calcule le nombre de jours équivalent par semaine et stocke la valeur brute.
     * (Méthode déplacée et rendue publique pour la mutualisation).
     */
    public Map<String, Object> parseModeTravail(String modeTravail, Integer joursParSemaine, Double pourcentage) {
        Map<String, Object> result = new HashMap<>();
        result.put("raw", modeTravail);
        double daysPerWeek = 0.0;

        try {
            if (joursParSemaine != null && joursParSemaine > 0) {
                daysPerWeek = Math.min(joursParSemaine, 5);
            } else if (pourcentage != null && pourcentage > 0) {
                daysPerWeek = 5.0 * (pourcentage / 100.0);
            } else if (modeTravail != null) {
                String lower = modeTravail.toLowerCase(Locale.ROOT);
                // La logique de Regex plus haut dans extractTeletravailInfo est plus précise,
                // mais on garde ce fallback pour une entrée directe si nécessaire.
                if (lower.contains("full_remote") || lower.contains("100%")) {
                    daysPerWeek = 5.0;
                }
            }
        } catch (Exception e) {
            log.warn("Erreur parsing modeTravail='{}' : {}", modeTravail, e.getMessage());
        }

        result.put("daysPerWeek", daysPerWeek);
        return result;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- Helpers Privés ---
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Fonction utilitaire pour obtenir une "fenêtre" de texte autour d'une correspondance.
     */
    private String getSearchWindow(String text, int start, int end, int radius) {
        int windowStart = Math.max(0, start - radius);
        int windowEnd = Math.min(text.length(), end + radius);
        return text.substring(windowStart, windowEnd);
    }

    // Le formatage du salaire est spécifique au résultat de l'annonce, on le laisse dans ExtractAnnonceService
}