package com.talentsboard.backend.service;

import com.talentsboard.backend.dto.MatchResultDTO;
import com.talentsboard.backend.model.Criteres;
import com.talentsboard.backend.model.Ticket;
import com.talentsboard.backend.model.UserType;
import com.talentsboard.backend.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de matching avancé et bi-directionnel :
 * - Cherche des tickets publiés compatibles (Candidat vs Entreprise).
 * - Calcule un score multicritères (Compétences, Langues, Salaire, Télétravail, etc.).
 */
@Slf4j
@Service
public class AdvancedMatchService { // Renommé de MatchService

    private final TicketRepository ticketRepository;

    // --- Poids des Critères pour le Scoring ---
    private static final double WEIGHT_SKILL = 50.0;
    private static final double WEIGHT_LANGUAGE = 20.0;
    private static final double WEIGHT_SALARY = 15.0;
    private static final double WEIGHT_EXPERIENCE = 10.0;
    private static final double WEIGHT_LOCATION_BONUS = 5.0; // Bonus si localisation exacte
    private static final double WEIGHT_TOTAL = WEIGHT_SKILL + WEIGHT_LANGUAGE + WEIGHT_SALARY + WEIGHT_EXPERIENCE + WEIGHT_LOCATION_BONUS;


    public AdvancedMatchService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Retourne les matches (limit 10) pour le ticket donné, en s'assurant que le match est Candidat <-> Entreprise.
     */
    public List<MatchResultDTO> findMatchesForTicket(Ticket sourceTicket, int limit) throws Exception {
        if (sourceTicket.getCriteres() == null) return Collections.emptyList();

        // Détermine le type de ticket cible à rechercher
        UserType targetType = sourceTicket.getOwnerType() == UserType.CANDIDAT ? UserType.ENTREPRISE : UserType.CANDIDAT;

        // 1. Phase de Pré-filtrage/Candidats (Optimisation)
        // On récupère tous les tickets cibles PUBLISHED (ou une liste filtrée plus tard pour l'efficacité)
        // Le filtre par compétence est conservé comme index inversé primaire.
        Set<String> targetIds = new HashSet<>();
        for (String skill : sourceTicket.getCriteres().getCompetences()) {
            // Dans un système réel, on interrogerait un index Lucene/Elasticsearch filtré sur (status=PUBLISHED, ownerType=targetType)
            // Pour l'exemple, nous supposons findBySkill retourne une liste pré-filtrée ou nous la filtrons ensuite.
            List<Ticket> potentialTargets = ticketRepository.findBySkill(skill, 1000);
            for (Ticket t : potentialTargets) {
                if (t.getOwnerType() == targetType && t.getStatus().equals("PUBLISHED") && !t.getId().equals(sourceTicket.getId())) {
                    targetIds.add(t.getId());
                }
            }
        }

        // 2. Phase de Scoring Détaillé
        List<MatchResultDTO> results = new ArrayList<>();

        for (String targetId : targetIds) {
            Ticket targetTicket = ticketRepository.findById(targetId);
            if (targetTicket == null) continue;

            // Logique de scoring différenciée
            double score;
            if (sourceTicket.getOwnerType() == UserType.CANDIDAT) {
                // Candidat cherche Annonce : Match sur COMPATIBILITÉ (le candidat matche-t-il les critères de l'offre?)
                score = computeCandidateScore(sourceTicket, targetTicket);
            } else {
                // Entreprise cherche Candidat : Match sur PERTINENCE (le candidat est-il bon pour l'offre?)
                score = computeCompanyScore(sourceTicket, targetTicket);
            }

            if (score > 0) {
                MatchResultDTO dto = new MatchResultDTO();
                dto.setTicketId(targetId);
                dto.setOwnerUid(targetTicket.getOwnerUid());
                dto.setScore(score);

                // MatchedSkills (Intersection pour l'affichage)
                dto.setMatchedSkills(intersect(sourceTicket.getCriteres().getCompetences(), targetTicket.getCriteres().getCompetences()));
                results.add(dto);
            }
        }

        // 3. Phase de Classement
        return results.stream()
                .sorted(Comparator.comparingDouble(MatchResultDTO::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- LOGIQUE DE SCORING SPÉCIFIQUE (Candidat <-> Entreprise) ---
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Score : Candidat (A) vs Entreprise (B).
     * Calcule à quel point le Candidat A correspond aux CRITÈRES requis par l'Entreprise B.
     */
    private double computeCandidateScore(Ticket candidate, Ticket companyOffer) {
        Criteres cA = candidate.getCriteres();
        Criteres cB = companyOffer.getCriteres();
        double finalScore = 0;

        // 1. Score de Compétences (50%)
        double skillMatch = computeSkillMatch(cA.getCompetences(), cB.getCompetences());
        finalScore += skillMatch * WEIGHT_SKILL;

        // 2. Score de Langues (20%)
        finalScore += computeLanguageScore(cA.getLanguages(), cB.getLanguages()) * WEIGHT_LANGUAGE;

        // 3. Score Salaire (15%)
        finalScore += computeSalaryScore(candidate, companyOffer) * WEIGHT_SALARY;

        // 4. Score Expérience (10%)
        // Logique simplifiée: si l'offre demande un junior mais le candidat est senior, le score baisse (et vice-versa)
        finalScore += computeExperienceScore(cA.getNiveauExperience(), cB.getNiveauExperience()) * WEIGHT_EXPERIENCE;

        // 5. Localisation (Bonus de 5%)
        if (isExactLocationMatch(cA.getLocalisation(), cB.getLocalisation())) {
            finalScore += WEIGHT_LOCATION_BONUS;
        }
        // Note: La localisation est déjà gérée par le classement naturel. Les matches sans lieu exact
        // auront simplement un score plus bas et seront classés après.

        // Normalisation (le score est déjà entre 0 et 100 si les poids sont bien calibrés)
        return Math.min(100.0, finalScore);
    }

    /**
     * Score : Entreprise (A) vs Candidat (B).
     * Calcule à quel point le Candidat B est PERTINENT pour les BESOINS actuels de l'Entreprise A.
     * Logique plus basée sur la Proximité sémantique (Domaine, Ville, Récence, etc.)
     */
    private double computeCompanyScore(Ticket companySource, Ticket candidateTarget) {
        Criteres cA = companySource.getCriteres(); // Criteres de l'offre A
        Criteres cB = candidateTarget.getCriteres(); // Criteres du candidat B
        double finalScore = 0;

        // 1. Score de Compétences (50%) - Le candidat a-t-il les compétences de l'offre A?
        finalScore += computeSkillMatch(cB.getCompetences(), cA.getCompetences()) * WEIGHT_SKILL;

        // 2. Score de Langues (20%) - Le candidat a-t-il les langues de l'offre A?
        finalScore += computeLanguageScore(cB.getLanguages(), cA.getLanguages()) * WEIGHT_LANGUAGE;

        // 3. Proximité de Domaine (10%)
        if (cA.getDomaine() != null && cA.getDomaine().equals(cB.getDomaine())) {
            finalScore += 10.0;
        } else if (cA.getDomaine() != null && cB.getDomaine() != null && cA.getDomaine().contains(cB.getDomaine())) {
            finalScore += 5.0; // Proximité
        }

        // 4. Proximité de Ville (10%)
        if (isExactLocationMatch(cA.getLocalisation(), cB.getLocalisation())) {
            finalScore += 10.0;
        } else if (isRegionMatch(cA.getLocalisation(), cB.getLocalisation())) {
            finalScore += 5.0; // Bonus pour la même région/zone
        }

        // 5. Récence du Ticket Candidat (10%) - Une entreprise préfère les profils actifs.
        double daysAgo = (System.currentTimeMillis() - candidateTarget.getUpdatedAt().toDate().getTime()) / (1000 * 60 * 60 * 24);
        double recencyScore = 1.0 - Math.min(1.0, daysAgo / 60.0); // Full score si < 60 jours, dégressif après
        finalScore += recencyScore * 10.0;

        return Math.min(100.0, finalScore);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- FONCTIONS DE SCORING DÉTAILLÉ ---
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Calcule le score de Compétences. (Intersect(A, B) / Taille(A))
     */
    private double computeSkillMatch(List<String> sourceSkills, List<String> targetSkills) {
        if (sourceSkills == null || targetSkills == null || sourceSkills.isEmpty()) return 0;
        long matched = intersect(sourceSkills, targetSkills).size();
        return (double) matched / sourceSkills.size(); // Fraction de compétences source trouvées chez la cible.
    }

    /**
     * Calcule le score de Langues. (Intersect(A, B) / Taille(A))
     * Pourrait être amélioré pour vérifier les Niveaux.
     */
    private double computeLanguageScore(List<String> sourceLangs, List<String> targetLangs) {
        if (sourceLangs == null || targetLangs == null || sourceLangs.isEmpty()) return 0;

        // Comparaison simple : on ignore le niveau pour l'instant (ex: "Anglais (Courant)" vs "Anglais (Bilingue)" = Match)
        Set<String> sourceNames = sourceLangs.stream().map(s -> s.split(" ")[0]).collect(Collectors.toSet());
        Set<String> targetNames = targetLangs.stream().map(s -> s.split(" ")[0]).collect(Collectors.toSet());

        long matched = sourceNames.stream().filter(targetNames::contains).count();
        return (double) matched / sourceNames.size();
    }

    /**
     * Calcule le score de Salaire. (Candidat Match Offre)
     */
    private double computeSalaryScore(Ticket candidate, Ticket companyOffer) {
        Double minC = candidate.getCriteres().getSalaireMin();
        Double maxC = candidate.getCriteres().getSalaireMax();
        Double minO = companyOffer.getCriteres().getSalaireMin();
        Double maxO = companyOffer.getCriteres().getSalaireMax();

        // 1. Si l'offre n'a pas de salaire ou le candidat n'en a pas, le match est neutre.
        if (minO == null && maxO == null) return 0.5;
        if (minC == null && maxC == null) return 0.5;

        // 2. Si le candidat a un salaire minimum, l'offre doit au moins proposer cela.
        if (minC != null && minO != null && minO < minC) return 0.0; // Score 0 si l'offre est sous la barre du candidat

        // 3. S'il y a un recouvrement, le score est élevé.
        if (minC != null && maxC != null && minO != null && maxO != null) {
            // Recouvrement (intersection / union)
            double intersection = Math.max(0, Math.min(maxC, maxO) - Math.max(minC, minO));
            double union = Math.max(maxC, maxO) - Math.min(minC, minO);
            return union > 0 ? intersection / union : 0;
        }

        // Fallback: si l'offre est dans la fourchette du candidat, c'est un bon match.
        if (minC != null && maxC != null && minO != null && minO >= minC && minO <= maxC) return 0.75;

        return 0.5;
    }

    /**
     * Calcule le score d'Expérience.
     */
    private double computeExperienceScore(String candidateExp, String offerExp) {
        // Logique très simplifiée: 1.0 si les niveaux se chevauchent, 0.5 si écart mineur, 0.0 si majeur.
        // Cela nécessiterait une fonction de normalisation (Junior=1, Middle=2, Senior=3, etc.)
        if (offerExp == null || candidateExp == null) return 0.5;

        String c = candidateExp.toLowerCase();
        String o = offerExp.toLowerCase();

        if (o.contains("junior")) {
            return c.contains("junior") || c.contains("débutant") ? 1.0 : 0.5;
        } else if (o.contains("senior") || o.contains("expert") || o.contains("lead")) {
            return c.contains("senior") || c.contains("expert") || c.contains("lead") || c.contains("confirmé") ? 1.0 : 0.5;
        }
        return 0.75; // Neutre si les niveaux ne sont pas clairs ou correspondent
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- FONCTIONS UTILITAIRES ---
    // -----------------------------------------------------------------------------------------------------------------

    private List<String> intersect(List<String> a, List<String> b) {
        if (a == null || b == null) return Collections.emptyList();
        return a.stream().filter(b::contains).distinct().collect(Collectors.toList());
    }

    private boolean isExactLocationMatch(String locA, String locB) {
        if (locA == null || locB == null) return false;
        // Correspondance par ville ou code postal
        return locA.trim().equalsIgnoreCase(locB.trim());
    }

    private boolean isRegionMatch(String locA, String locB) {
        if (locA == null || locB == null) return false;
        // Logique à implémenter (ex: vérifie si les codes postaux ont le même département)
        // Simplification: vérifie les régions globales (ex: "IDF" vs "Paris")
        String lowerA = locA.toLowerCase();
        String lowerB = locB.toLowerCase();

        if (lowerA.contains("paris") || lowerA.contains("idf")) {
            return lowerB.contains("paris") || lowerB.contains("idf");
        }
        // Pour les villes standard, ce sera False (moins pertinent dans ce contexte)
        return false;
    }
}