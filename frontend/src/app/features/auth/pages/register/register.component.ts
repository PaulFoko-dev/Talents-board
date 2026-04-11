import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="min-vh-100 d-flex align-items-center bg-light">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-md-6">
            <div class="card shadow-lg border-0">
              <div class="card-body p-5">
                <div class="text-center mb-4">
                  <i class="bi bi-briefcase-fill text-primary" style="font-size: 3rem;"></i>
                  <h2 class="fw-bold mt-2">Créer un compte</h2>
                  <p class="text-muted">Rejoignez la communauté Talents-Board</p>
                </div>
                <form (ngSubmit)="onRegister()">
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Nom complet</label>
                    <input type="text" class="form-control" [(ngModel)]="displayName" name="displayName" placeholder="Jean Dupont" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Email</label>
                    <input type="email" class="form-control" [(ngModel)]="email" name="email" placeholder="votre@email.com" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Mot de passe</label>
                    <input type="password" class="form-control" [(ngModel)]="password" name="password" placeholder="Min. 6 caractères" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Je suis...</label>
                    <div class="row g-2">
                      <div class="col-6">
                        <div class="card border-2" [class.border-primary]="role === 'CANDIDATE'" (click)="role = 'CANDIDATE'" style="cursor:pointer">
                          <div class="card-body text-center py-3">
                            <i class="bi bi-person-fill fs-2" [class.text-primary]="role === 'CANDIDATE'"></i>
                            <p class="mb-0 fw-semibold mt-1">Candidat</p>
                          </div>
                        </div>
                      </div>
                      <div class="col-6">
                        <div class="card border-2" [class.border-primary]="role === 'ENTERPRISE'" (click)="role = 'ENTERPRISE'" style="cursor:pointer">
                          <div class="card-body text-center py-3">
                            <i class="bi bi-building fs-2" [class.text-primary]="role === 'ENTERPRISE'"></i>
                            <p class="mb-0 fw-semibold mt-1">Entreprise</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="mb-4" *ngIf="role === 'ENTERPRISE'">
                    <label class="form-label fw-semibold">Nom de l'entreprise</label>
                    <input type="text" class="form-control" [(ngModel)]="company" name="company" placeholder="Ma Société SAS">
                  </div>
                  <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
                  <button type="submit" class="btn btn-primary w-100 py-2" [disabled]="loading || !role">
                    <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
                    Créer mon compte
                  </button>
                </form>
                <hr class="my-4">
                <p class="text-center mb-0">
                  Déjà un compte ?
                  <a routerLink="/auth/login" class="text-primary fw-semibold">Se connecter</a>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  displayName = '';
  email = '';
  password = '';
  role = '';
  company = '';
  loading = false;
  error = '';

  constructor(private authService: AuthService) {}

  async onRegister() {
    this.loading = true;
    this.error = '';
    try {
      await this.authService.register(this.email, this.password, this.displayName, this.role, this.company);
    } catch (e: any) {
      this.error = 'Erreur lors de la création du compte: ' + e.message;
    } finally {
      this.loading = false;
    }
  }
}
