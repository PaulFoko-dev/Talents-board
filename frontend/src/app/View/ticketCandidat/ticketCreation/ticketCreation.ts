import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms'; // <-- Ajoutez ReactiveFormsModule
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CommonModule } from '@angular/common'; // <-- Ajoutez CommonModule
import { TicketService } from '../../../services/ticket.service';

@Component({
  selector: 'app-ticket-creation', // Assurez-vous d'avoir un sélecteur
  standalone: true, // Très important si vous n'utilisez pas de NgModules
  imports: [
    CommonModule,          // <-- Ajoutez-le ici
    ReactiveFormsModule    // <-- Ajoutez-le ici
  ],
  templateUrl: './ticketCreation.html',
  styleUrls: ['./ticketCreation.scss']
})
export class TicketCreation implements OnInit {
  
  ticketForm!: FormGroup; // L'objet principal qui représente notre formulaire
  isLoading = false;      // Pour gérer l'état de chargement lors de la soumission
  submitted = false;      // Pour savoir si l'utilisateur a déjà tenté de soumettre
  selectedFile!: File; // Pour stocker le fichier sélectionné

  // Options pour les listes déroulantes
  domaines: string[] = ['Informatique / IT', 'Finance / Comptabilité', 'Marketing / Communication', 'Ressources Humaines', 'Santé', 'Autre'];
  niveauxExperience: string[] = ['Junior (0-2 ans)', 'Confirmé (2-5 ans)', 'Senior (5 ans et +)', 'Expert / Lead'];
  disponibilites: string[] = ['Immédiate', 'Sous 1 mois', 'Sous 2 mois', 'Sous 3 mois', 'Plus de 3 mois'];

  // Injection des services nécessaires : FormBuilder, Router et notre service de tickets
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private ticketservices: TicketService
    // private ticketService: TicketService 
  ) {}

  ngOnInit(): void {
    // On initialise le formulaire avec sa structure et ses validateurs
    this.ticketForm = this.fb.group({
      // Le premier élément du tableau est la valeur par défaut, le second les validateurs
      title: ['', Validators.required],
      localisation: [''],
      domaine: [''],
      availability: [''],
      salaireMinExpectation: [null],
      salaireMaxExpectation: [null],
      niveauExperience: [''],
      preferExactLocation: [false], // La valeur par défaut d'une checkbox est false
      descriptionRaw: [''],
    });
  }

  // Getter pratique pour accéder facilement aux contrôles du formulaire dans le template HTML
  get f() { return this.ticketForm.controls; }

  onSubmit(): void {
    this.submitted = true;

    // On arrête le processus si le formulaire est invalide
    if (this.ticketForm.invalid) {
      console.log("Formulaire invalide.");
      return;
    }

    this.isLoading = true;
    console.log('Données du formulaire soumises :', this.ticketForm.value);

    // --- SIMULATION DE L'APPEL AU SERVICE ---
    // Remplacer cette partie par votre vrai appel service.
    // this.ticketService.createTicket(this.ticketForm.value)
    of({ success: true }).pipe(
        finalize(() => this.isLoading = false) // On s'assure que isLoading redevient false à la fin
    ).subscribe({
        next: () => {
            console.log('Ticket créé avec succès !');
            // Redirection vers la liste des tickets après succès
            this.router.navigate(['/tickets-candidats']);
        },
        error: (err) => {
            console.error('Erreur lors de la création du ticket', err);
            // Ici, vous pourriez afficher un message d'erreur à l'utilisateur
        }
    });
  }

  onCancel(): void {
    // Redirection simple vers la page précédente (la l
    this.router.navigate(['/tickets-candidats']);
  }

  saveticket(): void {
    console.log(this.ticketForm.value);
    this.ticketservices.saveTicket(this.ticketForm.value, this.selectedFile ).then(data => {
      console.log(data);
      this.router.navigate(['/ticketCandidat']);
    }).catch(error => {
      console.log(error);      
    });
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      console.log('Fichier sélectionné :', file);
    } else {
      // this.selectedFile = null;
      console.log('Aucun fichier sélectionné.');
    }
  }
}