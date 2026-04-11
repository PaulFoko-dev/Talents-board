import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../../core/services/api.service';

@Component({
  selector: 'app-candidates-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <h2 class="fw-bold mb-1">Candidats</h2>
      <p class="text-muted mb-4">Parcourez les profils de candidats disponibles</p>

      <div class="mb-4">
        <input type="text" class="form-control w-50" [(ngModel)]="search" placeholder="Rechercher un candidat...">
      </div>

      <div *ngIf="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div class="row g-4" *ngIf="!loading">
        <div class="col-md-6 col-lg-4" *ngFor="let candidate of filteredCandidates">
          <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center p-4">
              <div class="bg-primary bg-opacity-10 rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width:64px;height:64px">
                <i class="bi bi-person-fill text-primary fs-3"></i>
              </div>
              <h5 class="fw-bold mb-1">{{ candidate.displayName }}</h5>
              <p class="text-muted small mb-3">{{ candidate.email }}</p>
              <p *ngIf="candidate.bio" class="text-muted small mb-3">{{ candidate.bio }}</p>
              <div *ngIf="candidate.skills" class="mb-3">
                <span *ngFor="let skill of parseSkills(candidate.skills)" class="badge bg-light text-dark border me-1 mb-1">{{ skill }}</span>
              </div>
              <div *ngIf="candidate.cvUrl">
                <a [href]="candidate.cvUrl" target="_blank" class="btn btn-sm btn-outline-primary">
                  <i class="bi bi-file-earmark-pdf me-1"></i>Voir le CV
                </a>
              </div>
            </div>
          </div>
        </div>
        <div class="col-12" *ngIf="filteredCandidates.length === 0">
          <div class="text-center py-5">
            <i class="bi bi-people text-muted" style="font-size: 3rem;"></i>
            <p class="text-muted mt-3">Aucun candidat trouvé</p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class CandidatesListComponent implements OnInit {
  candidates: any[] = [];
  loading = true;
  search = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.apiService.getCandidates().subscribe({
      next: (data) => { this.candidates = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  get filteredCandidates() {
    return this.candidates.filter(c =>
      !this.search || c.displayName?.toLowerCase().includes(this.search.toLowerCase())
    );
  }

  parseSkills(skills: string): string[] {
    try { return JSON.parse(skills); } catch { return skills ? skills.split(',') : []; }
  }
}
