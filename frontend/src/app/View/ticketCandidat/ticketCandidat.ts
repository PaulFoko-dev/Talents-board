import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { TicketService } from '../../services/ticket.service';

// --- BONNE PRATIQUE ---
// Normalement, cette interface serait dans son propre fichier (ex: ticket.model.ts)
export interface Ticket {
  id: string;
  title: string;
  status: 'Actif' | 'Archivé';
  domaine: string;
  localisation: string;
  availability: string;
}

// --- BONNE PRATIQUE ---
// La récupération des données devrait se faire via un service (ex: ticket.service.ts)
// Ici, nous simulons le service pour l'exemple.

@Component({
  selector: 'app-tickets-candidats',
  templateUrl: './ticketCandidat.html',
  styleUrls: ['./ticketCandidat.scss'],
  imports: [CommonModule] // Important pour les composants autonomes
})
export class TicketCandidat implements OnInit {

  // Propriétés pour stocker l'état du composant
  tickets: Ticket[] = [];
  isLoading: boolean = true;

  // Injection du service Router pour la navigation
  constructor(private router: Router, private ticketservices: TicketService) { }

  // Méthode du cycle de vie d'Angular, appelée une fois que le composant est initialisé
  ngOnInit(): void {
    this.chargerTickets();
    this.getTicket();
  }

  chargerTickets(): void {
    this.isLoading = true;

    // --- SIMULATION D'UN APPEL API ---
    // On simule une attente de 1.5 secondes pour imiter un appel réseau
    const mockTickets: Ticket[] = [
      { id: '1', title: 'Développeur Full-Stack', status: 'Actif', domaine: 'Informatique', localisation: 'Paris, France', availability: 'Sous 1 mois' },
      { id: '2', title: 'Data Scientist Senior', status: 'Actif', domaine: 'Data', localisation: 'Lyon, France', availability: 'Immédiate' },
      { id: '3', title: 'Chef de Projet Marketing Digital', status: 'Archivé', domaine: 'Marketing', localisation: 'Télétravail', availability: 'Sous 3 mois' },
    ];
    
    // Pour tester l'état vide, commentez la ligne mockTickets ci-dessus et décommentez la suivante :
    // const mockTickets: Ticket[] = [];

    // On utilise `of()` et `delay()` de RxJS pour simuler une réponse asynchrone
    of(mockTickets).pipe(
      delay(1500) 
    ).subscribe(data => {
      this.tickets = data;
      this.isLoading = false;
    });
  }

  /**
   * Navigue vers la page de création de ticket.
   */
  creerNouveauTicket(): void {
  this.router.navigate(['/ticketCreation']);
  }

  /**
   * Navigue vers la page de détails d'un ticket spécifique.
   * @param ticketId L'identifiant du ticket à afficher.
   */
  voirDetails(ticketId: string): void {
    // Cette navigation inclut un paramètre dans l'URL
    this.router.navigate(['/tickets-candidats/details', ticketId]);
  }

  getTicket(): void {
    this.ticketservices.getTicket().then(data => {
      console.log(data);
      this.tickets = data;
    }).catch(error => {
      this.tickets[0].title = "moi";
    });
  }
}