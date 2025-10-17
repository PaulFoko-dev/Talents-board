import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { TicketService } from '../../services/ticket.service';

// On définit une interface pour typer nos données, c'est une bonne pratique
export interface Ticket {
  id: string;
  title: string;
  domaine: string;
  localisation: string;
  availability: string;
  status: string;
  // Ajoutez d'autres champs si nécessaire
}

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ticketCandidat.html',
  styleUrls: ['./ticketCandidat.scss']
})
export class TicketCandidat  implements OnInit {

  // ✅ La variable est initialisée ici !
  tickets: Ticket[] = [];
  isLoading: boolean = true;
  errorMessage: string | null = null;

  constructor(
    private ticketService: TicketService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // On lance le chargement des données
    this.loadTickets();
  }

  async loadTickets(): Promise<void> {
    this.isLoading = true;
    this.errorMessage = null;

    try {
      const response = await this.ticketService.getTickets();
      this.tickets = response.data || []; // On s'assure que 'tickets' est un tableau
    } catch (error) {
      this.errorMessage = "Impossible de charger les tickets.";
      console.error(error);
    } finally {
      this.isLoading = false;
    }
  }

  // Fonctions pour les boutons
  creerNouveauTicket(): void {
    this.router.navigate(['/ticketCreation']);
  }

  voirDetails(ticketId: string): void {
    this.router.navigate(['/tickets', ticketId]);
  }
}