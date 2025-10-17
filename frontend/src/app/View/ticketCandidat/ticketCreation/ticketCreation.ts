import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormArray, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../../services/ticket.service';

export function fileTypeValidator(type: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const file = control.value as File;
    if (file && file.type !== type) {
      // Si le type de fichier ne correspond pas, retourne une erreur
      return { fileType: true };
    }
    // Si le fichier est correct ou si le champ est vide, retourne null (pas d'erreur)
    return null;
  };
}

@Component({
  selector: 'app-ticket-creation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './ticketCreation.html',
  styleUrls: ['./ticketCreation.scss']
})
export class TicketCreation implements OnInit {

  // --- PROPERTIES ---
  ticketForm!: FormGroup;
  validationForm!: FormGroup; // Déclaration du second formulaire

  isLoading = false;
  isValidating = false; // Variable pour le spinner du second formulaire
  submitted = false;
  selectedFile!: File;
  createdTicket: any = null; // Stocke la réponse de l'API pour la validation

  // --- STATIC DATA ---
  domaines: string[] = [
    'Informatique / IT', 'Finance / Comptabilité', 'Marketing / Communication',
    'Ressources Humaines', 'Santé', 'Autre'
  ];
  niveauxExperience: string[] = [
    'Junior (0-2 ans)', 'Confirmé (2-5 ans)', 'Senior (5 ans et +)', 'Expert / Lead'
  ];
  disponibilites: string[] = [
    'Immédiate', 'Sous 1 mois', 'Sous 2 mois', 'Sous 3 mois', 'Plus de 3 mois'
  ];

  // --- LIFECYCLE & CONSTRUCTOR ---
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private ticketService: TicketService
  ) { }

  ngOnInit(): void {
    this.initTicketForm();
  }

  // --- FORM INITIALIZATION ---
  private initTicketForm(): void {
    this.ticketForm = this.fb.group({
      title: ['', Validators.required],
      descriptionRaw: [''],
      localisation: [''],
      domaine: [''],
      availability: [''],
      salaireMinExpectation: [null],
      salaireMaxExpectation: [null],
      cv: [null, [Validators.required, fileTypeValidator('application/pdf')]],
      niveauExperience: [''],
      preferExactLocation: [false]
    });
  }

  private initValidationForm(): void {
    if (!this.createdTicket) return;

    this.validationForm = this.fb.group({
      title: [this.createdTicket.title || ''],
      localisation: [this.createdTicket.localisation || ''],
      domaine: [this.createdTicket.domaine || ''],
      typeContrat: [this.createdTicket.typeContrat || ''],
      niveauExperience: [this.createdTicket.niveauExperience || ''],
      diplomeRequis: [this.createdTicket.diplomeRequis || ''],
      modeTravail: [this.createdTicket.modeTravail || 'Présentiel'],
      teletravailJourParSemaine: [this.createdTicket.teletravailJourParSemaine || null],
      teletravailPourcentage: [this.createdTicket.teletravailPourcentage || null],
      salaireMin: [this.createdTicket.salaireMin || null],
      salaireMax: [this.createdTicket.salaireMax || null],
      descriptionRaw: [this.createdTicket.descriptionRaw || ''],
      competences: this.fb.array(this.createdTicket.competences || []),
      languages: this.fb.array(this.createdTicket.languages || []),
      avantages: this.fb.array(this.createdTicket.avantages || [])
    });
  }
  get cv() {
    return this.ticketForm.get('cv');
  }
  // --- GETTERS for Form Controls & FormArrays ---
  get f() { return this.ticketForm.controls; }
  get vf() { return this.validationForm.controls; }

  getCompetences(): FormArray { return this.validationForm.get('competences') as FormArray; }
  getLanguages(): FormArray { return this.validationForm.get('languages') as FormArray; }
  getAvantages(): FormArray { return this.validationForm.get('avantages') as FormArray; }

  // --- EVENT HANDLERS ---
  onFileChange(event: any): void {
    const file = event.target.files?.[0];
    if (file) {
      this.selectedFile = file;
    }
  }
  onCvChange(event: any): void {
    const file = event.target.files?.[0];

    if (file) {
      // Met à jour la valeur du formulaire avec l'objet Fichier.
      // Cela déclenche automatiquement les validateurs (required, fileType).
      this.ticketForm.patchValue({ cv: file });

      // Marque le champ comme "touché" pour que les messages d'erreur s'affichent si besoin.
      this.cv?.markAsTouched();
    }
  }
  onCancel(): void {
    this.router.navigate(['/tickets-candidats']);
  }

  // --- FORM SUBMISSION LOGIC ---

  onSubmit(): void {
    this.submitted = true;
    if (this.ticketForm.invalid) {
      console.warn('⚠️ Formulaire initial invalide. Erreurs :', this.ticketForm.errors);
      // Loggez spécifiquement l'erreur du CV pour aider au débogage
      if (this.cv?.errors) {
        console.warn('Erreurs du champ CV :', this.cv.errors);
      }
      return;
    }

    this.isLoading = true;

    // ✅ CORRECTION ICI
    // 1. On récupère le fichier CV depuis le formulaire
    const cvFile = this.ticketForm.value.cv as File;

    // 2. On crée une copie des données du formulaire SANS le champ 'cv'
    const dataToSend = { ...this.ticketForm.value };
    delete dataToSend.cv;

    // 3. On appelle le service avec les bonnes variables
    this.ticketService.createCandidateTicket(dataToSend, cvFile)
      .then(response => {
        console.log('✅ Ticket pré-créé avec succès :', response);
        this.createdTicket = response.data;
        this.initValidationForm();
      })
      .catch(error => {
        console.error('❌ Erreur lors de la création du ticket', error);
      })
      .finally(() => {
        this.isLoading = false;
      });
  }
  /**
   * Étape 2 : Soumission du formulaire de validation après correction
   * par l'utilisateur.
   */
  onValidate(): void {
    if (this.validationForm.invalid) {
      console.warn('⚠️ Formulaire de validation invalide.');
      return;
    }

    this.isValidating = true;
    const finalTicketData = {
      ...this.createdTicket, // Conserve les métadonnées comme l'ID du ticket
      ...this.validationForm.value // Écrase avec les valeurs validées/corrigées
    };

    this.ticketService.validateCandidateTicket(finalTicketData)
      .then(() => {
        console.log('✅ Ticket validé avec succès !');
        this.router.navigate(['ticketCandidat']); // Redirection après succès
      })
      .catch(error => {
        console.error('❌ Erreur lors de la validation du ticket', error);
      })
      .finally(() => {
        this.isValidating = false;
      });
  }


  // --- DYNAMIC LIST (FormArray) MANAGEMENT ---

  /**
   * Ajoute un élément (tag) à un FormArray.
   * @param event L'événement clavier (pour récupérer la valeur et prévenir la soumission).
   * @param formArrayName Le nom du FormArray ('competences', 'languages', etc.).
   */
  addItem(event: Event, formArrayName: string): void {
    const input = event.target as HTMLInputElement;
    const value = input.value.trim();

    if (value) {
      const formArray = this.validationForm.get(formArrayName) as FormArray;
      formArray.push(this.fb.control(value));
      input.value = '';
    }
    event.preventDefault(); // Empêche la soumission du formulaire sur appui de la touche "Entrée"
  }

  /**
   * Supprime un élément d'un FormArray à un index donné.
   * @param formArrayName Le nom du FormArray.
   * @param index L'index de l'élément à supprimer.
   */
  removeItem(formArrayName: string, index: number): void {
    const formArray = this.validationForm.get(formArrayName) as FormArray;
    formArray.removeAt(index);
  }
}