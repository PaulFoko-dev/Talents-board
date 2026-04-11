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
    <div class="tb-page" style="padding-left:1.5rem;padding-right:1.5rem;">
      <div style="max-width:720px;margin:0 auto;">

        <!-- Header -->
        <div style="display:flex;align-items:center;gap:1rem;margin-bottom:2rem;" class="anim-1">
          <a routerLink="/tickets" class="tb-btn-ghost" style="padding:0.5rem 0.75rem;">
            <i class="bi bi-arrow-left"></i>
          </a>
          <div>
            <h1 class="tb-page-title">Nouveau ticket</h1>
            <p class="tb-page-sub">Publiez votre annonce sur la plateforme</p>
          </div>
        </div>

        <div class="tb-card anim-2">
          <div class="tb-card-body" style="padding:2rem;">
            <form (ngSubmit)="onSubmit()">

              <!-- Type -->
              <div class="tb-field">
                <label class="tb-label">Type de ticket</label>
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem;">
                  <div class="tb-type-card" [class.selected-talent]="type === 'SEARCH_TALENT'"
                       (click)="type = 'SEARCH_TALENT'">
                    <div class="tb-type-icon"><i class="bi bi-person-search"></i></div>
                    <div class="tb-type-label">Recherche de Talent</div>
                    <div style="font-size:0.72rem;color:var(--text-faint);margin-top:4px;">Pour les entreprises</div>
                  </div>
                  <div class="tb-type-card" [class.selected-opport]="type === 'SEARCH_OPPORTUNITY'"
                       (click)="type = 'SEARCH_OPPORTUNITY'">
                    <div class="tb-type-icon"><i class="bi bi-briefcase"></i></div>
                    <div class="tb-type-label">Recherche d'Opportunité</div>
                    <div style="font-size:0.72rem;color:var(--text-faint);margin-top:4px;">Pour les candidats</div>
                  </div>
                </div>
              </div>

              <!-- Titre -->
              <div class="tb-field">
                <label class="tb-label">Titre <span style="color:var(--primary);">*</span></label>
                <input class="tb-input" type="text" [(ngModel)]="title" name="title"
                       placeholder="Ex : Développeur Angular Senior, Stage Data Science…" required>
              </div>

              <!-- Description -->
              <div class="tb-field">
                <label class="tb-label">Description</label>
                <textarea class="tb-input" [(ngModel)]="description" name="description"
                          placeholder="Décrivez le poste, les missions, le contexte…"></textarea>
              </div>

              <!-- Compétences -->
              <div class="tb-field">
                <label class="tb-label">Compétences requises</label>
                <input class="tb-input" type="text" [(ngModel)]="skillsInput" name="skills"
                       placeholder="Angular, Java, Spring Boot, Docker… (séparés par des virgules)">
                <div *ngIf="previewSkills.length > 0" style="display:flex;flex-wrap:wrap;gap:4px;margin-top:8px;">
                  <span class="tb-badge tb-badge-skill" *ngFor="let s of previewSkills">{{ s }}</span>
                </div>
              </div>

              <!-- Localisation -->
              <div class="tb-field">
                <label class="tb-label">Localisation</label>
                <div style="position:relative;">
                  <i class="bi bi-geo-alt" style="position:absolute;left:12px;top:50%;transform:translateY(-50%);color:var(--text-faint);"></i>
                  <input class="tb-input" style="padding-left:2.2rem;" type="text"
                         [(ngModel)]="location" name="location"
                         placeholder="Paris, Lyon, Télétravail, Hybride…">
                </div>
              </div>

              <div class="tb-error" *ngIf="error">
                <i class="bi bi-exclamation-circle me-2"></i>{{ error }}
              </div>

              <div style="display:flex;gap:0.75rem;margin-top:1.5rem;padding-top:1.5rem;border-top:1px solid var(--border);">
                <button type="submit" class="tb-btn" [disabled]="loading || !type || !title">
                  <span *ngIf="loading" class="tb-spinner" style="width:16px;height:16px;border-width:2px;"></span>
                  <i class="bi bi-send-fill" *ngIf="!loading"></i>
                  {{ loading ? 'Publication…' : 'Publier le ticket' }}
                </button>
                <a routerLink="/tickets" class="tb-btn-ghost">Annuler</a>
              </div>

            </form>
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

  get previewSkills(): string[] {
    return this.skillsInput.split(',').map(s => s.trim()).filter(s => s.length > 0);
  }

  onSubmit() {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.loading = true;
    this.error = '';
    const skills = JSON.stringify(this.previewSkills);
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
      error: () => { this.error = 'Erreur lors de la création du ticket'; this.loading = false; }
    });
  }
}
