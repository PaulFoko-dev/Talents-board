package com.talentsboard.backend.service;

import com.google.cloud.Timestamp;
import com.talentsboard.backend.dto.*;
import com.talentsboard.backend.mapper.TicketMapper;
import com.talentsboard.backend.model.Criteres;
import com.talentsboard.backend.model.Ticket;
import com.talentsboard.backend.model.UserType;
import com.talentsboard.backend.repository.TicketRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service orchestration des tickets :
 * - création (candidat / entreprise),
 * - publication (validate),
 * - CRUD complet,
 * - vérifie les droits via ownerUid.
 * * 💡 Utilise ExtractionOrchestratorService pour la logique d'extraction mutualisée.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    // Ancien: private final ExtractAnnonceService extractAnnonceService;
    private final ExtractionCoreService extractionCoreService; // 💡 Remplacement

    // Constante pour le statut
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PUBLISHED = "PUBLISHED";


    /**
     * Crée un ticket candidat en mode DRAFT.
     * Les données de matching sont stockées dans l'objet Criteres.
     *
     * @param req données envoyées par le candidat
     * @param ownerUid UID Firebase de l’utilisateur
     * @param extracted données extraites du CV (text + skills + benefits + languages)
     */
    public Ticket createCandidatTicket(CreateTicketCandidatRequest req,
                                       String ownerUid,
                                       Map<String, Object> extracted) throws Exception  {

        String extractedText = extracted != null ? (String) extracted.getOrDefault("text", "") : null;
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) extracted.getOrDefault("skills", Collections.emptyList());
        String typeContratExtracted = extracted.getOrDefault("typeContrat", "").toString();
        @SuppressWarnings("unchecked")
        List<String> languages = (List<String>) extracted.getOrDefault("languages", Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> benefits = (List<String>) extracted.getOrDefault("benefits", Collections.emptyList()); // Ajouté dans le DTO précédent

        Ticket t = new Ticket();
        t.setOwnerUid(ownerUid);
        t.setOwnerType(UserType.CANDIDAT);
        t.setStatus(STATUS_DRAFT);
        t.setTitle(req.getTitle());
        t.setDomaine(req.getDomaine());
        // On met le texte extrait en RawText si le champ req.getDescriptionRaw() est vide (priorité à la description manuelle)
        t.setRawText(req.getDescriptionRaw() != null && !req.getDescriptionRaw().isEmpty() ? req.getDescriptionRaw() : extractedText);

        // --- Champs spécifiques au Ticket Candidat ---
        t.setAvailability(req.getAvailability());

        // --- Construction des Criteres pour le matching Candidat ---
        Criteres c = new Criteres();

        // Critères extraits du CV (via 'extracted')
        c.setCompetences(skills);
        c.setLanguages(languages);
        c.setAvantages(benefits); // Les avantages extraits sont mappés ici

        // Critères renseignés par le Candidat (via 'req')
        c.setLocalisation(req.getLocalisation());
        c.setDomaine(req.getDomaine());
        c.setTypeContrat(typeContratExtracted);
        c.setNiveauExperience(req.getNiveauExperience());
        c.setSalaireMin(req.getSalaireMinExpectation());
        c.setSalaireMax(req.getSalaireMaxExpectation());
        c.setPreferExactLocation(req.getPreferExactLocation()); // Préférence de matching

        t.setCriteres(c);

        t.setCreatedAt(Timestamp.now());
        t.setUpdatedAt(Timestamp.now());
        t.setScoreDenorm(new HashMap<>()); // Initialisation

        // Pour l'affichage, on construit la chaîne de l'attente salariale si présente
        t.setSalaryRange(buildSalaryRange(req.getSalaireMinExpectation(), req.getSalaireMaxExpectation()));

        return ticketRepository.save(t);
    }

// NOTE: La méthode buildSalaryRange() doit être présente dans TicketService.

    /**
     * Crée un ticket entreprise (extraction depuis description).
     * 💡 Délègue l'extraction des critères techniques à l'orchestrateur.
     *
     * @param req CreateTicketEntrepriseRequest (au minimum descriptionRaw)
     * @param ownerUid uid du créateur
     * @return Ticket (DRAFT) avec criteres extraits
     */
    public Ticket createEntrepriseDraft(CreateTicketEntrepriseRequest req, String ownerUid) throws Exception {
        // Appeler la méthode d'extraction complète d'ExtractAnnonceService,
        // qui elle-même utilise l'Orchestrateur pour la logique.
        // On conserve l'injection de ExtractAnnonceService pour la logique
        // qui lui est propre (titre, localisation, contrat, etc.).

        // 💡 Note: Si on avait pu extraire titre/localisation/contrat directement ici
        // en utilisant OpenNLP, ce serait plus propre, mais on se base sur
        // l'architecture actuelle qui utilise ExtractAnnonceService pour tout.

        AnnonceExtractionResult ext = new ExtractAnnonceService(extractionCoreService)
                .extractAnnonce(req.getDescriptionRaw());

        Criteres c = (ext.getCriteres() != null) ? ext.getCriteres() : new Criteres();

        Ticket t = new Ticket();
        t.setOwnerUid(ownerUid);
        t.setOwnerType(UserType.ENTREPRISE);
        t.setStatus(STATUS_DRAFT);
        t.setTitle(ext.getTitle() != null ? ext.getTitle() : "Offre entreprise");
        t.setRawText(req.getDescriptionRaw());

        // enrichir avec données brutes et spécifiques
        t.setCompany(ext.getCompany());
        t.setSalaryRange(ext.getSalaryRange()); // Garde la version brute

        // --- Mise à jour des Criteres avec les données extraites ---
        // Les critères dans 'ext.getCriteres()' sont déjà construits par ExtractAnnonceService
        // via les appels à l'Orchestrateur (skills, langues, télétravail etc. sont déjà normalisés)

        t.setCriteres(c);

        // --- hydratation télétravail brut (directement depuis le résultat d'extraction) ---
        t.setModeTravailRaw(ext.getModeTravailRaw());
        t.setTeletravailEquivalentDaysPerWeek(ext.getTeletravailEquivalentDaysPerWeek());

        t.setCreatedAt(Timestamp.now());
        t.setUpdatedAt(Timestamp.now());
        t.setScoreDenorm(new HashMap<>());
        return ticketRepository.save(t);
    }

    /**
     * Valide les champs de l'annonce entreprise (après validation front),
     * applique la logique de normalisation (salaire, télétravail)
     * et publie un Ticket persistant.
     * * 💡 Délègue la normalisation du télétravail et du salaire à l'orchestrateur.
     *
     * @param request annonce brute envoyée par l'entreprise
     * @param ownerUid identifiant de l'entreprise propriétaire
     * @return TicketDTO prêt à être consommé côté front
     */
    // Signature du service mise à jour pour accepter l'ID du ticket à valider
    public TicketDTO validateAndPublishEntreprise(String ticketId, ValidateTicketEntrepriseRequest request, String ownerUid) throws Exception{
        Objects.requireNonNull(ticketId, "L'ID du ticket ne doit pas être nul");
        Objects.requireNonNull(request, "La requête ne doit pas être nulle");
        Objects.requireNonNull(ownerUid, "ownerUid ne doit pas être nul");

        // 1. Récupération du ticket existant (DRAFT)
        Ticket ticket = ticketRepository.findById(ticketId);

        if (ticket == null) {
            throw new NoSuchElementException("Ticket non trouvé avec l'ID: " + ticketId);
        }

        // 2. Vérification des droits (le propriétaire du DRAFT est le seul à pouvoir le publier)
        if (!ticket.getOwnerUid().equals(ownerUid)) {
            throw new SecurityException("Accès refusé. Seul le propriétaire peut valider le ticket.");
        }

        // 3. Mise à jour des champs du Ticket avec les données validées
        ticket.setTitle(request.getTitle());
        ticket.setRawText(request.getDescriptionRaw());
        ticket.setCompany(request.getDomaine()); // L'attribut Domain de la requête sert pour Company
        ticket.setStatus(STATUS_PUBLISHED);
        // ticket.setCreatedAt(Timestamp.now()); // On ne met pas à jour la date de création
        ticket.setUpdatedAt(Timestamp.now());

        // --- Construction et Hydratation des Criteres (mis à jour) ---
        Criteres c = (ticket.getCriteres() != null) ? ticket.getCriteres() : new Criteres();

        c.setLocalisation(request.getLocalisation());
        c.setTypeContrat(request.getTypeContrat());
        c.setNiveauExperience(request.getNiveauExperience());
        c.setAvantages(request.getAvantages() != null ? request.getAvantages() : new ArrayList<>());
        c.setCompetences(request.getCompetences() != null ? request.getCompetences() : new ArrayList<>());
        c.setLanguages(request.getLanguages() != null ? request.getLanguages() : new ArrayList<>());
        c.setDiplomeRequis(request.getDiplomeRequis()); // ✅ Ajout du diplôme si présent

        // --- Gestion Salaire ---
        c.setSalaireMin(request.getSalaireMin());
        c.setSalaireMax(request.getSalaireMax());
        ticket.setSalaryRange(buildSalaryRange(request.getSalaireMin(), request.getSalaireMax()));


        // --- Gestion Télétravail (Normalisation DÉLÉGUÉE) ---
        c.setModeTravail(request.getModeTravail());
        c.setTeletravailJourParSemaine(request.getTeletravailJourParSemaine());
        c.setTeletravailPourcentage(request.getTeletravailPourcentage());

        // 💡 Le service d'orchestration est nommé `extractionOrchestratorService` dans le code précédent
        // Je corrige l'appel `extractionCoreService` vers `extractionOrchestratorService` (assumant la correction de l'injection)
        Map<String, Object> modeTravailInfo = extractionCoreService.parseModeTravail(
                request.getModeTravail(),
                request.getTeletravailJourParSemaine(),
                request.getTeletravailPourcentage());

        ticket.setModeTravailRaw((String) modeTravailInfo.get("raw"));
        ticket.setTeletravailEquivalentDaysPerWeek((Double) modeTravailInfo.get("daysPerWeek"));

        ticket.setCriteres(c);

        // --- Score Dénormalisé ---
        Map<String, Object> scoreDenorm = (ticket.getScoreDenorm() != null) ? ticket.getScoreDenorm() : new HashMap<>();
        scoreDenorm.put("teletravailDays", ticket.getTeletravailEquivalentDaysPerWeek());
        scoreDenorm.put("salaireMin", ticket.getCriteres().getSalaireMin());
        scoreDenorm.put("salaireMax", ticket.getCriteres().getSalaireMax());
        ticket.setScoreDenorm(scoreDenorm);

        // 4. Sauvegarde de la mise à jour (publication)
        ticketRepository.save(ticket);
        log.info("[TICKET] Publication id={} | modeTravailRaw='{}' | equivDays={}",
                ticket.getId(), ticket.getModeTravailRaw(), ticket.getTeletravailEquivalentDaysPerWeek());

        return TicketMapper.toDTO(ticket);
    }

    // --- Reste des méthodes inchangées ---

    /**
     * Construit la chaîne de salaire à afficher
     */
    private String buildSalaryRange(Double min, Double max) {
        if (min == null && max == null) return null;
        if (min != null && max != null) return String.format("%.0f - %.0f €", min, max);
        if (min != null) return String.format("À partir de %.0f €", min);
        return String.format("Jusqu'à %.0f €", max);
    }


    public Ticket getTicket(String id) throws Exception {
        return ticketRepository.findById(id);
    }

    public List<Ticket> listPublished(int limit, String pageToken) throws Exception {
        return ticketRepository.findAllPublished(limit, pageToken);
    }

    public List<Ticket> listByOwner(String ownerUid, int limit, String pageToken) throws Exception {
        return ticketRepository.findByOwner(ownerUid, limit, pageToken);
    }

    public void deleteTicket(String id, String ownerUid, boolean isAdmin) throws Exception {
        Ticket t = ticketRepository.findById(id);
        if (t == null) return;
        if (!isAdmin && !t.getOwnerUid().equals(ownerUid)) throw new RuntimeException("Accès refusé");

        ticketRepository.deleteById(id);
    }
}