package com.talentsboard.backend.dto;

import com.talentsboard.backend.model.Criteres;
import lombok.Data;

import java.util.List;

/**
 * Résultat d'extraction enrichi pour une annonce.
 * Le controller renverra ceci encapsulé dans un TicketDTO draft.
 */
@Data
public class AnnonceExtractionResult {
    private Criteres criteres;
    private String title;            // titre estimé (ex : "Développeur Full-Stack Senior")
    private String company;          // organisation détectée (si trouvée)
    private String location;         // localisation détectée / normalisée
    private String salaryRange;      // chaîne lisible "48 000€ - 60 000€"
    private String experienceLevel;  // "Junior", "Senior", "5 ans", ...
    private List<String> languages;  // ex: ["Français", "Anglais technique"]
    private List<String> benefits;   // avantages extraits
    private String modeTravailRaw;              // FULL_REMOTE | PARTIAL_REMOTE | HYBRID | ONSITE | OPTIONAL_REMOTE
    private Double teletravailEquivalentDaysPerWeek;
    private Double teletravailPourcentage;
}