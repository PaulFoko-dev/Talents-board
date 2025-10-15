import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TicketService } from '../../services/ticket.service';

export interface Ticket {
  id: string;
  title: string;
  status: 'Actif' | 'Archivé';
  domaine: string;
  localisation: string;
  availability: string;
}

@Component({
  selector: 'app-tickets-candidats',
  templateUrl: './ticketCandidat.html',
  styleUrls: ['./ticketCandidat.scss'],
  standalone: true, // J'ai ajouté standalone pour être explicite
  imports: [CommonModule]
})
export class TicketCandidat implements OnInit {

  tickets: Ticket[] = [];
  isLoading: boolean = false; // Initialisé à false, sera mis à true au début de l'appel
  error: string | null = null; // Pour stocker les messages d'erreur et les afficher à l'utilisateur

  // CORRECTION : Nommage du service en lowerCamelCase
  constructor(
    private router: Router, 
    private ticketService: TicketService 
  ) {}

  // ngOnInit est la méthode parfaite pour lancer le chargement des données initiales.
  ngOnInit(): void {
    this.loadTickets();
  }

  /**
   * CORRECTION MAJEURE : Une seule fonction pour charger les tickets, gérant l'asynchronisme correctement.
   */
  async loadTickets(): Promise<void> {
    this.isLoading = true; // 1. On active le chargement
    this.error = null;     // Réinitialiser les erreurs précédentes

    try {
      // 2. On attend (await) la résolution de la promesse. La variable 'data' contiendra le tableau de tickets.
      const data = await this.ticketService.getTickets();

      // 3. Une fois les données reçues, on met à jour notre variable.
      this.tickets = data.data;
      console.log("Tickets chargés avec succès :", this.tickets);

    } catch (err) {
      // 4. Si une erreur survient pendant l'appel, on la capture ici.
      console.error("Erreur lors du chargement des tickets :", err);
      this.error = "Impossible de charger la liste des tickets. Veuillez réessayer plus tard.";
      this.tickets = []; // S'assurer que la liste est vide en cas d'erreur.

    } finally {
      // 5. Ce bloc s'exécute toujours, que ce soit un succès ou une erreur.
      // C'est l'endroit parfait pour désactiver le chargement.
      this.isLoading = false;
    }
  }

  creerNouveauTicket(): void {
    this.router.navigate(['/ticketCreation']);
  }

  voirDetails(ticketId: string): void {
    this.router.navigate(['/tickets-candidats/details', ticketId]);
  }
}