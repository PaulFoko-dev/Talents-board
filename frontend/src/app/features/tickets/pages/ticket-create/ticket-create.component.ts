import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-ticket-create',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container py-4">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <div class="d-flex align-items-center mb-4">
            <a routerLink="/tickets" class="btn btn-outline-secondary me-3">
              <i class="bi bi-arrow-left"></i>
            </a>
            <div>
              <h2 class="fw-bold mb-0">Créer un ticket</h2>
              <p class="text-muted mb-0">Publiez votre annonce sur la plateforme</p>
            </div>
          </div>
          <div class="card border-0 shadow-sm">
            <div class="card-body p-4">
              <form (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label class="form-label fw-semibold">Type de ticket</label>
                  <div class="row g-2">
                    <div class="col-6">
                      <div class="card border-2" [class.border-primary]="type === 'SEARCH_TALENT'" (click)="type = 'SEARCH_TALENT'" style="cursor:pointer">
                        <div class="card-body text-center py-3">
                          <i class="bi bi-search fs-3" [class.text-primary]="type === 'SEARCH_TALENT'"></i>
                          <p class="mb-0 fw-semibold mt-1 small">Recherche de Talent</p>
                        </div>
                      </div>
                    </div>
                    <div class="col-6">
                      <div class="card border-2" [class.border-primary]="type === 'SEARCH_OPPORTUNITY'" (click)="type = 'SEARCH_OPPORTUNITY'" style="cursor:pointer">
                        <div class="card-body text-center py-3">
                          <i class="bi bi-briefcase fs-3" [class.text-primary]="type === 'SEARCH_OPPORTUNITY'"></i>
                          <p class="mb-0 fw-semibold mt-1 small">Recherche d'Opportunité</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-semibold">Titre *</label>
                  <input type="text" class="form-control" [(ngModel)]="title" name="title" placeholder="Ex: Développeur Angular Senior" required>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-semibold">Description</label>
                  <textarea class="form-control" [(ngModel)]="description" name="description" rows="4" placeholder="Décrivez votre besoin..."></textarea>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-semibold">Compétences requises</label>
                  <input type="text" class="form-control" [(ngModel)]="skillsInput" name="skills" placeholder="Angular, Java, Spring Boot (séparés par des virgules)">
                </div>
                <div class="mb-4">
                  <label class="form-label fw-semibold">Localisation</label>
                  <input type="text" class="form-control" [(ngModel)]="location" name="location" placeholder="Paris, Télétravail...">
                </div>
                <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
                <div class="d-flex gap-2">
                  <button type="submit" class="btn btn-primary" [disabled]="loading || !type || !title">
                    <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
                    <i class="bi bi-check2 me-2" *ngIf="!loading"></i>
                    Publier le ticket
                  </button>
                  <a routerLink="/tickets" class="btn btn-outline-secondary">Annuler</a>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class TicketCreateComponent {
  type = '';
  title = '';
  description = '';
  skillsInput = '';
  location = '';
  loading = false;
  error = '';

  constructor(private apiService: ApiService, private authService: AuthService, private router: Router) {}

  onSubmit() {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.loading = true;
    this.error = '';
    const skills = JSON.stringify(this.skillsInput.split(',').map((s: string) => s.trim()).filter((s: string) => s));
    this.apiService.createTicket({
      ownerId: user.id,
      ownerName: user.displayName,
      type: this.type,
      title: this.title,
      description: this.description,
      skills,
      location: this.location
    }).subscribe({
      next: () => this.router.navigate(['/tickets']),
      error: () => { this.error = 'Erreur lors de la création'; this.loading = false; }
    });
  }
}
