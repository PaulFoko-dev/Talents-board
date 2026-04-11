import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../../core/services/api.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-tickets-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="fw-bold mb-1">Tickets</h2>
          <p class="text-muted mb-0">Découvrez les opportunités disponibles</p>
        </div>
        <a routerLink="/tickets/create" class="btn btn-primary">
          <i class="bi bi-plus-circle me-2"></i>Créer un ticket
        </a>
      </div>

      <!-- Filtres -->
      <div class="card mb-4 border-0 shadow-sm">
        <div class="card-body">
          <div class="row g-3 align-items-center">
            <div class="col-md-4">
              <input type="text" class="form-control" [(ngModel)]="searchTerm" placeholder="Rechercher...">
            </div>
            <div class="col-md-3">
              <select class="form-select" [(ngModel)]="filterType">
                <option value="">Tous les types</option>
                <option value="SEARCH_TALENT">Recherche de talent</option>
                <option value="SEARCH_OPPORTUNITY">Recherche d'opportunité</option>
              </select>
            </div>
            <div class="col-md-3">
              <select class="form-select" [(ngModel)]="filterStatus">
                <option value="">Tous les statuts</option>
                <option value="OPEN">Ouvert</option>
                <option value="CLOSED">Fermé</option>
                <option value="MATCHED">Matché</option>
              </select>
            </div>
            <div class="col-md-2">
              <button class="btn btn-outline-secondary w-100" (click)="resetFilters()">
                <i class="bi bi-arrow-counterclockwise me-1"></i>Reset
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <!-- Grille de tickets -->
      <div *ngIf="!loading" class="row g-4">
        <div class="col-md-6 col-lg-4" *ngFor="let ticket of filteredTickets">
          <div class="card h-100 border-0 shadow-sm hover-card">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-3">
                <span class="badge" [class.bg-primary]="ticket.type === 'SEARCH_TALENT'" [class.bg-success]="ticket.type === 'SEARCH_OPPORTUNITY'">
                  {{ ticket.type === 'SEARCH_TALENT' ? 'Recherche Talent' : 'Opportunité' }}
                </span>
                <span class="badge" [class.bg-success]="ticket.status === 'OPEN'" [class.bg-secondary]="ticket.status === 'CLOSED'" [class.bg-warning]="ticket.status === 'MATCHED'">
                  {{ ticket.status }}
                </span>
              </div>
              <h5 class="card-title fw-bold">{{ ticket.title }}</h5>
              <p class="card-text text-muted small">{{ ticket.description | slice:0:100 }}{{ ticket.description?.length > 100 ? '...' : '' }}</p>
              <div *ngIf="ticket.location" class="text-muted small mb-2">
                <i class="bi bi-geo-alt me-1"></i>{{ ticket.location }}
              </div>
              <div *ngIf="ticket.skills" class="mb-3">
                <span *ngFor="let skill of parseSkills(ticket.skills)" class="badge bg-light text-dark border me-1 mb-1">{{ skill }}</span>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">{{ ticket.ownerName }}</small>
                <button class="btn btn-sm btn-outline-primary" (click)="applyToTicket(ticket)" *ngIf="currentUser?.role === 'CANDIDATE' && ticket.status === 'OPEN'">
                  Postuler
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="col-12" *ngIf="filteredTickets.length === 0">
          <div class="text-center py-5">
            <i class="bi bi-inbox text-muted" style="font-size: 3rem;"></i>
            <p class="text-muted mt-3">Aucun ticket trouvé</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .hover-card { transition: transform 0.2s, box-shadow 0.2s; }
    .hover-card:hover { transform: translateY(-4px); box-shadow: 0 8px 25px rgba(0,0,0,0.15) !important; }
  `]
})
export class TicketsListComponent implements OnInit {
  tickets: any[] = [];
  loading = true;
  searchTerm = '';
  filterType = '';
  filterStatus = '';
  currentUser: any = null;

  constructor(private apiService: ApiService, private authService: AuthService) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.loadTickets();
  }

  loadTickets() {
    this.loading = true;
    this.apiService.getTickets().subscribe({
      next: (data) => { this.tickets = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  get filteredTickets() {
    return this.tickets.filter(t => {
      const matchSearch = !this.searchTerm || t.title?.toLowerCase().includes(this.searchTerm.toLowerCase()) || t.description?.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchType = !this.filterType || t.type === this.filterType;
      const matchStatus = !this.filterStatus || t.status === this.filterStatus;
      return matchSearch && matchType && matchStatus;
    });
  }

  parseSkills(skills: string): string[] {
    try { return JSON.parse(skills); } catch { return skills ? skills.split(',') : []; }
  }

  resetFilters() {
    this.searchTerm = '';
    this.filterType = '';
    this.filterStatus = '';
  }

  applyToTicket(ticket: any) {
    if (!this.currentUser?.id) return;
    this.apiService.createApplication({
      candidateId: this.currentUser.id,
      ticketId: ticket.id,
      message: 'Candidature spontanée'
    }).subscribe({
      next: () => alert('Candidature envoyée avec succès !'),
      error: (e: any) => alert('Erreur: ' + (e.error?.message || 'Candidature déjà envoyée'))
    });
  }
}
