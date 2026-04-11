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
    <div class="tb-page" style="padding-left:1.5rem;padding-right:1.5rem;">
      <div style="max-width:1200px;margin:0 auto;">

        <!-- Header -->
        <div class="tb-page-header anim-1">
          <div>
            <h1 class="tb-page-title">Tickets</h1>
            <p class="tb-page-sub">{{ filteredTickets.length }} opportunité{{ filteredTickets.length !== 1 ? 's' : '' }} disponible{{ filteredTickets.length !== 1 ? 's' : '' }}</p>
          </div>
          <a routerLink="/tickets/create" class="tb-btn">
            <i class="bi bi-plus-lg"></i> Créer un ticket
          </a>
        </div>

        <!-- Filtres -->
        <div class="tb-filters anim-2">
          <div style="position:relative;flex:1;min-width:200px;">
            <i class="bi bi-search" style="position:absolute;left:12px;top:50%;transform:translateY(-50%);color:var(--text-faint);font-size:0.85rem;"></i>
            <input class="tb-input" style="padding-left:2.2rem;" type="text"
                   [(ngModel)]="searchTerm" placeholder="Rechercher un ticket…">
          </div>
          <select class="tb-input" style="min-width:180px;flex-shrink:0;" [(ngModel)]="filterType">
            <option value="">Tous les types</option>
            <option value="SEARCH_TALENT">Recherche de talent</option>
            <option value="SEARCH_OPPORTUNITY">Recherche d'opportunité</option>
          </select>
          <select class="tb-input" style="min-width:140px;flex-shrink:0;" [(ngModel)]="filterStatus">
            <option value="">Tous statuts</option>
            <option value="OPEN">Ouvert</option>
            <option value="CLOSED">Fermé</option>
            <option value="MATCHED">Matché</option>
          </select>
          <button class="tb-btn-ghost" (click)="resetFilters()" style="flex-shrink:0;">
            <i class="bi bi-arrow-counterclockwise"></i> Reset
          </button>
        </div>

        <!-- Loading -->
        <div class="tb-loading" *ngIf="loading">
          <div class="tb-spinner"></div>
          <span>Chargement des tickets…</span>
        </div>

        <!-- Grille -->
        <div *ngIf="!loading" style="display:grid;grid-template-columns:repeat(auto-fill,minmax(320px,1fr));gap:1.25rem;">

          <div class="tb-card anim-3" *ngFor="let ticket of filteredTickets; let i = index"
               [style.animation-delay]="(i * 0.05) + 's'">
            <!-- Stripe de couleur en haut -->
            <div class="tb-ticket-stripe" [class.tb-ticket-stripe-teal]="ticket.type === 'SEARCH_OPPORTUNITY'"></div>

            <div class="tb-card-body">
              <!-- Badges -->
              <div style="display:flex;align-items:center;gap:6px;margin-bottom:0.85rem;flex-wrap:wrap;">
                <span class="tb-badge tb-badge-talent" *ngIf="ticket.type === 'SEARCH_TALENT'">
                  <i class="bi bi-search"></i> Talent
                </span>
                <span class="tb-badge tb-badge-opport" *ngIf="ticket.type === 'SEARCH_OPPORTUNITY'">
                  <i class="bi bi-briefcase"></i> Opportunité
                </span>
                <span class="tb-badge tb-badge-open"    *ngIf="ticket.status === 'OPEN'">Ouvert</span>
                <span class="tb-badge tb-badge-closed"  *ngIf="ticket.status === 'CLOSED'">Fermé</span>
                <span class="tb-badge tb-badge-matched" *ngIf="ticket.status === 'MATCHED'">Matché</span>
              </div>

              <!-- Titre -->
              <h5 style="font-family:'Syne',sans-serif;font-size:1rem;font-weight:700;margin-bottom:0.5rem;color:var(--text);">
                {{ ticket.title }}
              </h5>

              <!-- Description -->
              <p style="color:var(--text-muted);font-size:0.84rem;line-height:1.55;margin-bottom:0.85rem;min-height:40px;">
                {{ (ticket.description || '') | slice:0:110 }}{{ (ticket.description?.length || 0) > 110 ? '…' : '' }}
              </p>

              <!-- Localisation -->
              <div *ngIf="ticket.location" style="display:flex;align-items:center;gap:5px;color:var(--text-faint);font-size:0.78rem;margin-bottom:0.75rem;">
                <i class="bi bi-geo-alt"></i> {{ ticket.location }}
              </div>

              <!-- Skills -->
              <div *ngIf="ticket.skills" style="display:flex;flex-wrap:wrap;gap:4px;margin-bottom:0.5rem;">
                <span class="tb-badge tb-badge-skill" *ngFor="let skill of parseSkills(ticket.skills)">{{ skill }}</span>
              </div>
            </div>

            <div class="tb-card-footer">
              <span style="font-size:0.78rem;color:var(--text-faint);">
                <i class="bi bi-person me-1"></i>{{ ticket.ownerName }}
              </span>
              <button class="tb-btn tb-btn-sm" (click)="applyToTicket(ticket)"
                      *ngIf="currentUser?.role === 'CANDIDATE' && ticket.status === 'OPEN'">
                Postuler <i class="bi bi-arrow-right ms-1"></i>
              </button>
            </div>
          </div>

          <!-- État vide -->
          <div class="tb-empty" *ngIf="filteredTickets.length === 0">
            <i class="bi bi-inbox tb-empty-icon"></i>
            <span>Aucun ticket ne correspond à vos critères</span>
            <button class="tb-btn-ghost" (click)="resetFilters()">Réinitialiser les filtres</button>
          </div>

        </div>
      </div>
    </div>
  `
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
      const matchSearch = !this.searchTerm ||
        t.title?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        t.description?.toLowerCase().includes(this.searchTerm.toLowerCase());
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
      error: (e: any) => alert('Erreur : ' + (e.error?.message || 'Candidature déjà envoyée'))
    });
  }
}
