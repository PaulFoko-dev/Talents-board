import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../../core/services/api.service';

@Component({
  selector: 'app-enterprises-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <h2 class="fw-bold mb-1">Entreprises</h2>
      <p class="text-muted mb-4">Découvrez les entreprises qui recrutent</p>

      <div class="mb-4">
        <input type="text" class="form-control w-50" [(ngModel)]="search" placeholder="Rechercher une entreprise...">
      </div>

      <div *ngIf="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div class="row g-4" *ngIf="!loading">
        <div class="col-md-6 col-lg-4" *ngFor="let enterprise of filteredEnterprises">
          <div class="card h-100 border-0 shadow-sm">
            <div class="card-body p-4">
              <div class="d-flex align-items-center mb-3">
                <div class="bg-success bg-opacity-10 rounded-circle d-inline-flex align-items-center justify-content-center me-3" style="width:48px;height:48px">
                  <i class="bi bi-building text-success fs-4"></i>
                </div>
                <div>
                  <h5 class="fw-bold mb-0">{{ enterprise.company || enterprise.displayName }}</h5>
                  <p class="text-muted small mb-0">{{ enterprise.email }}</p>
                </div>
              </div>
              <p *ngIf="enterprise.bio" class="text-muted small">{{ enterprise.bio }}</p>
            </div>
          </div>
        </div>
        <div class="col-12" *ngIf="filteredEnterprises.length === 0">
          <div class="text-center py-5">
            <i class="bi bi-building text-muted" style="font-size: 3rem;"></i>
            <p class="text-muted mt-3">Aucune entreprise trouvée</p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class EnterprisesListComponent implements OnInit {
  enterprises: any[] = [];
  loading = true;
  search = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.apiService.getEnterprises().subscribe({
      next: (data) => { this.enterprises = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  get filteredEnterprises() {
    return this.enterprises.filter(e =>
      !this.search || e.company?.toLowerCase().includes(this.search.toLowerCase()) || e.displayName?.toLowerCase().includes(this.search.toLowerCase())
    );
  }
}
