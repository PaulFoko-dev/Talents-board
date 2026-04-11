import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="min-vh-100 d-flex align-items-center bg-light">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-md-5">
            <div class="card shadow-lg border-0">
              <div class="card-body p-5">
                <div class="text-center mb-4">
                  <i class="bi bi-briefcase-fill text-primary" style="font-size: 3rem;"></i>
                  <h2 class="fw-bold mt-2">Talents-Board</h2>
                  <p class="text-muted">Connectez-vous à votre compte</p>
                </div>
                <form (ngSubmit)="onLogin()">
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Email</label>
                    <input type="email" class="form-control" [(ngModel)]="email" name="email" placeholder="votre@email.com" required>
                  </div>
                  <div class="mb-4">
                    <label class="form-label fw-semibold">Mot de passe</label>
                    <input type="password" class="form-control" [(ngModel)]="password" name="password" placeholder="••••••••" required>
                  </div>
                  <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
                  <button type="submit" class="btn btn-primary w-100 py-2" [disabled]="loading">
                    <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
                    Se connecter
                  </button>
                </form>
                <hr class="my-4">
                <p class="text-center mb-0">
                  Pas encore de compte ?
                  <a routerLink="/auth/register" class="text-primary fw-semibold">Créer un compte</a>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  email = '';
  password = '';
  loading = false;
  error = '';

  constructor(private authService: AuthService) {}

  async onLogin() {
    this.loading = true;
    this.error = '';
    try {
      await this.authService.login(this.email, this.password);
    } catch (e: any) {
      this.error = 'Email ou mot de passe incorrect';
    } finally {
      this.loading = false;
    }
  }
}
