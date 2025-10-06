package com.talentsboard.backend.service;

import com.talentsboard.backend.dto.AnnonceExtractionResult;
import com.talentsboard.backend.model.Criteres;
import com.talentsboard.backend.util.TechDictionary;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors; // Ajouté

/**
 * Service d'extraction d'informations depuis une annonce brute.
 * Maintenant, c'est un ORCHESTRATEUR qui délègue la logique d'extraction à ExtractionOrchestratorService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ExtractAnnonceService {

    private static final Logger logger = LoggerFactory.getLogger(ExtractAnnonceService.class);

    private final ExtractionCoreService extractionCoreService; // 💡 Injection

    // Seules les regex spécifiques à l'annonce restent ici
    private static final Pattern EXPERIENCE_REGEX = Pattern.compile(
            "\\b(?:(junior|senior|confirm[eé]|d[eé]butant|expert|lead|middle)|([0-9]+)\\s?-\\s?([0-9]+)\\s?ans?|\\+([0-9])\\s?ans?|[0-9]+\\s?ans?)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);

    private static final Pattern LOCATION_CITY_POSTAL_REGEX = Pattern.compile(
            "\\b([A-Z][\\p{L}\\s\\-']{2,})\\s*(?:\\((\\d{5})\\)|(\\d{5}))\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);

    private static final Pattern LOCATION_GLOBAL_REGEX = Pattern.compile(
            "\\b(France\\s+enti[eè]re|International|Monde|Europe|IDF|R[ée]gion\\s+parisienne|National|Remote)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);

    private static final Pattern LOCATION_SIMPLE_REGEX = Pattern.compile(
            "(?:(?:Lieu|Localisation|Ville)[:\\s]+([A-Z][\\p{L}\\-\\s]+)(?:\\(\\d{5}\\))?)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);

    private static final Pattern COMPANY_ABOUT_REGEX = Pattern.compile(
            "(?:Contexte\\s*[:\\-]|À\\s*Propos\\s*de|A\\s*propos\\s*d[e']|About)\\s+([^\\n\\.,\\-]{2,80})",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS
    );

    /**
     * Extrait infos structurées (dont télétravail homogénéisé).
     */
    public AnnonceExtractionResult extractAnnonce(String descriptionRaw) {
        AnnonceExtractionResult res = new AnnonceExtractionResult();
        Criteres c = new Criteres();

        if (StringUtils.isBlank(descriptionRaw)) {
            res.setCriteres(c);
            return res;
        }

        String raw = descriptionRaw.replace('\u00A0', ' ').replaceAll("\\s+", " ").trim();
        String lower = raw.toLowerCase(Locale.ROOT);

        // --- titre ---
        String title = heuristiqueTitle(raw);
        title = cleanTitle(title);
        res.setTitle(title);

        // --- company extraction (spécifique annonce)
        Matcher mCompany = COMPANY_ABOUT_REGEX.matcher(raw);
        if (mCompany.find()) {
            String candidate = StringUtils.trim(mCompany.group(1));
            candidate = candidate.replaceAll("\\b(est|sont|qui|une|un|établie|spécialisée)\\b.*$", "").trim();
            candidate = collapseRepeatedWords(candidate);
            if (candidate.length() > 0) res.setCompany(candidate);
        }

        // --- localisation (spécifique annonce) ---
        extractLocalisation(raw, c, res);

        // --- contrat (spécifique annonce) ---
        String typeContrat = extractionCoreService.extractTypeContrat(raw);
        c.setTypeContrat(typeContrat);

        // --- expérience (spécifique annonce) ---
        Matcher mExp = EXPERIENCE_REGEX.matcher(raw);
        if (mExp.find()) {
            // ... (logique expérience inchangée car spécifique à l'annonce)
            String niveau = null;
            if (mExp.group(1) != null) {
                niveau = StringUtils.capitalize(mExp.group(1).toLowerCase(Locale.ROOT));
            } else if (mExp.group(2) != null) {
                niveau = mExp.group(2) + "-" + (mExp.group(3) != null ? mExp.group(3) : "") + " ans";
            } else {
                for (int i = 0; i <= mExp.groupCount(); i++) {
                    if (mExp.group(i) != null && mExp.group(i).matches("\\d+")) {
                        niveau = mExp.group(i) + " ans";
                        break;
                    }
                }
            }
            if (niveau != null) {
                c.setNiveauExperience(niveau);
                res.setExperienceLevel(niveau);
            }
        }


        // ------------------ 💡 DÉLÉGATION À L'ORCHESTRATEUR ------------------

        // --- salaire (DÉLÉGUÉ) ---
        Map<String, Double> salaryRange = extractionCoreService.extractSalaireRange(raw);
        Double min = salaryRange.get("min");
        Double max = salaryRange.get("max");
        c.setSalaireMin(min);
        c.setSalaireMax(max);
        res.setSalaryRange(formatSalaryRange(min, max));

        // --- avantages (DÉLÉGUÉ) ---
        List<String> avantages = extractionCoreService.extractBenefits(raw);
        c.setAvantages(avantages.stream().distinct().toList());
        res.setBenefits(c.getAvantages());

        // --- compétences (DÉLÉGUÉ) ---
        List<String> skills = extractionCoreService.extractSkills(raw);
        c.setCompetences(skills);

        // --- langues (DÉLÉGUÉ et AMÉLIORÉ) ---
        List<String> langs = extractionCoreService.extractLanguages(raw);
        res.setLanguages(langs);

        // --- télétravail (DÉLÉGUÉ et AMÉLIORÉ) ---
        Map<String, Object> teleInfo = extractionCoreService.extractTeletravailInfo(raw);
        c.setModeTravail((String) teleInfo.get("modeTravail"));
        c.setTeletravailJourParSemaine((Integer) teleInfo.get("teletravailJourParSemaine"));
        c.setTeletravailPourcentage((Double) teleInfo.get("teletravailPourcentage"));
        res.setModeTravailRaw((String) teleInfo.get("modeTravailRaw"));
        res.setTeletravailEquivalentDaysPerWeek((Double) teleInfo.get("teletravailEquivalentDaysPerWeek"));

        // ---------------------------------------------------------------------

        res.setCriteres(c);
        return res;
    }

    // ---------------- helpers (spécifiques à l'annonce/résultat) ----------------

    private String formatSalaryRange(Double min, Double max) {
        if (min == null && max == null) return null;
        NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
        nf.setMaximumFractionDigits(0);
        if (min != null && max != null) {
            return String.format("%s € - %s €", nf.format(min), nf.format(max));
        }
        if (min != null) return String.format("À partir de %s €", nf.format(min));
        return String.format("Jusqu'à %s €", nf.format(max));
    }

    // ... (autres helpers inchangés) ...

    private void extractLocalisation(String raw, Criteres c, AnnonceExtractionResult res) {
        // Logique localisation inchangée (spécifique à la structure d'une annonce)
        Matcher matcherPostal = LOCATION_CITY_POSTAL_REGEX.matcher(raw);
        if (matcherPostal.find()) {
            String city = matcherPostal.group(1);
            String postal = matcherPostal.group(2) != null ? matcherPostal.group(2) : matcherPostal.group(3);
            String loc = (city != null ? city.trim() : "") + (postal != null ? " (" + postal + ")" : "");
            res.setLocation(StringUtils.trim(loc));
            c.setLocalisation(res.getLocation());
            return;
        }

        Matcher matcherSimple = LOCATION_SIMPLE_REGEX.matcher(raw);
        if (matcherSimple.find()) {
            String city = matcherSimple.group(1);
            res.setLocation(StringUtils.trim(city));
            c.setLocalisation(res.getLocation());
            return;
        }

        Matcher matcherGlobal = LOCATION_GLOBAL_REGEX.matcher(raw);
        if (matcherGlobal.find()) {
            res.setLocation(StringUtils.trim(matcherGlobal.group(1)));
            c.setLocalisation(res.getLocation());
        }
    }

    private String heuristiqueTitle(String raw) {
        String[] splitByKeywords = raw.split("(?i)(Lieu|Localisation|Contrat|Rémunération|À Propos|À propos|Contexte)");
        String maybe = splitByKeywords.length > 0 ? splitByKeywords[0].trim() : raw;
        if (maybe.length() > 160) {
            int nl = maybe.indexOf('.');
            if (nl > 30) maybe = maybe.substring(0, nl);
            if (maybe.length() > 160) maybe = maybe.substring(0, 160).trim();
        }
        return StringUtils.normalizeSpace(maybe);
    }

    private String cleanTitle(String rawTitle) {
        if (rawTitle == null) return null;
        return rawTitle.replaceAll("(?i)^(intitul[ée] du poste|titre|offre|poste proposé)\\s*:?\\s*", "").trim();
    }

    private String collapseRepeatedWords(String s) {
        if (s == null) return null;
        s = s.replaceAll("\\b(\\p{L}+\\s+\\p{L}+)(\\s*\\1)+\\b", "$1");
        s = s.replaceAll("\\b(\\p{L}{3,})\\1\\b", "$1");
        String[] parts = s.split("\\s+");
        StringBuilder out = new StringBuilder();
        String prev = null;
        for (String p : parts) {
            String norm = p.toLowerCase(Locale.ROOT);
            if (!norm.equals(prev)) {
                if (out.length() > 0) out.append(' ');
                out.append(p);
            }
            prev = norm;
        }
        return out.toString().replaceAll("[,\\.\\-\\/]+$", "").trim();
    }
}