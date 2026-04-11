import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../../core/services/api.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <h2 class="fw-bold mb-4">Mon Profil</h2>
          <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-4">
              <div class="d-flex align-items-center mb-4">
                <div class="bg-primary bg-opacity-10 rounded-circle d-flex align-items-center justify-content-center me-4" style="width:80px;height:80px">
                  <i class="bi bi-person-fill text-primary" style="font-size: 2.5rem;"></i>
                </div>
                <div>
                  <h4 class="fw-bold mb-1">{{ user?.displayName }}</h4>
                  <span class="badge" [class.bg-primary]="user?.role === 'CANDIDATE'" [class.bg-success]="user?.role === 'ENTERPRISE'">
                    {{ user?.role === 'CANDIDATE' ? 'Candidat' : 'Entreprise' }}
                  </span>
                </div>
              </div>

              <form *ngIf="editing" (ngSubmit)="saveProfile()">
                <div class="mb-3">
                  <label class="form-label fw-semibold">Nom complet</label>
                  <input type="text" class="form-control" [(ngModel)]="form.displayName" name="displayName">
                </div>
                <div class="mb-3">
                  <label class="form-label fw-semibold">Bio</label>
                  <textarea class="form-control" [(ngModel)]="form.bio" name="bio" rows="3" placeholder="Parlez de vous..."></textarea>
                </div>
                <div class="mb-3" *ngIf="user?.role === 'ENTERPRISE'">
                  <label class="form-label fw-semibold">Entreprise</label>
                  <input type="text" class="form-control" [(ngModel)]="form.company" name="company">
                </div>
                <div class="mb-3" *ngIf="user?.role === 'CANDIDATE'">
                  <label class="form-label fw-semibold">Compétences</label>
                  <input type="text" class="form-control" [(ngModel)]="form.skills" name="skills" placeholder="Angular, Java, Spring Boot...">
                </div>
                <div class="mb-4">
                  <label class="form-label fw-semibold">URL du CV</label>
                  <input type="url" class="form-control" [(ngModel)]="form.cvUrl" name="cvUrl" placeholder="https://...">
                </div>
                <div class="d-flex gap-2">
                  <button type="submit" class="btn btn-primary" [disabled]="saving">
                    <span *ngIf="saving" class="spinner-border spinner-border-sm me-2"></span>
                    Enregistrer
                  </button>
                  <button type="button" class="btn btn-outline-secondary" (click)="editing = false">Annuler</button>
                </div>
              </form>

              <div *ngIf="!editing">
                <div class="row g-3 mb-4">
                  <div class="col-md-6">
                    <p class="text-muted small mb-1">Email</p>
                    <p class="fw-semibold">{{ user?.email }}</p>
                  </div>
                  <div *ngIf="user?.company" class="col-md-6">
                    <p class="text-muted small mb-1">Entreprise</p>
                    <p class="fw-semibold">{{ user?.company }}</p>
                  </div>
                  <div *ngIf="user?.bio" class="col-12">
                    <p class="text-muted small mb-1">Bio</p>
                    <p>{{ user?.bio }}</p>
                  </div>
                </div>
                <div *ngIf="user?.skills" class="mb-4">
                  <p class="text-muted small mb-2">Compétences</p>
                  <span *ngFor="let skill of parseSkills(user?.skills)" class="badge bg-light text-dark border me-1 mb-1">{{ skill }}</span>
                </div>
                <button class="btn btn-primary" (click)="startEdit()">
                  <i class="bi bi-pencil me-2"></i>Modifier le profil
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ProfileComponent implements OnInit {
  user: any = null;
  editing = false;
  saving = false;
  form: any = {};

  constructor(private authService: AuthService, private apiService: ApiService) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.user = user;
    });
  }

  startEdit() {
    this.form = { ...this.user };
    this.editing = true;
  }

  saveProfile() {
    if (!this.user?.id) return;
    this.saving = true;
    this.apiService.updateUser(this.user.id, this.form).subscribe({
      next: (updated) => {
        this.user = updated;
        this.editing = false;
        this.saving = false;
      },
      error: () => { this.saving = false; }
    });
  }

  parseSkills(skills: string): string[] {
    try { return JSON.parse(skills); } catch { return skills ? skills.split(',') : []; }
  }
}
