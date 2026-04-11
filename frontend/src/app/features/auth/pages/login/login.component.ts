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
    <div class="tb-auth-wrap">
      <!-- Panneau gauche : brand -->
      <div class="tb-auth-panel">
        <div class="tb-auth-blob tb-auth-blob-1"></div>
        <div class="tb-auth-blob tb-auth-blob-2"></div>
        <div class="anim-1">
          <div class="tb-auth-brand">
            <span style="width:10px;height:10px;background:var(--primary);border-radius:50%;display:inline-block;"></span>
            Talents-Board
          </div>
        </div>
        <div class="tb-auth-tagline anim-2">
          Connectez les <span>talents</span> aux meilleures <span>opportunités</span>.
        </div>
        <div class="tb-auth-features anim-3">
          <div class="tb-auth-feature">
            <div class="tb-auth-feature-icon"><i class="bi bi-ticket-perforated-fill"></i></div>
            <span>Système de tickets bidirectionnel candidats ↔ entreprises</span>
          </div>
          <div class="tb-auth-feature">
            <div class="tb-auth-feature-icon"><i class="bi bi-search-heart"></i></div>
            <span>Matching précis par compétences et localisation</span>
          </div>
          <div class="tb-auth-feature">
            <div class="tb-auth-feature-icon"><i class="bi bi-bell-fill"></i></div>
            <span>Notifications en temps réel à chaque interaction</span>
          </div>
        </div>
      </div>

      <!-- Panneau droit : formulaire -->
      <div class="tb-auth-form-side">
        <div class="tb-auth-form-box">
          <div class="anim-1">
            <h1 class="tb-auth-title">Bon retour 👋</h1>
            <p class="tb-auth-sub">Connectez-vous pour accéder à votre espace</p>
          </div>

          <form (ngSubmit)="onLogin()" class="anim-2">
            <div class="tb-field">
              <label class="tb-label">Adresse email</label>
              <input class="tb-input" type="email" [(ngModel)]="email" name="email"
                     placeholder="jean.dupont@email.com" required autocomplete="email">
            </div>
            <div class="tb-field">
              <label class="tb-label">Mot de passe</label>
              <input class="tb-input" type="password" [(ngModel)]="password" name="password"
                     placeholder="••••••••" required autocomplete="current-password">
            </div>

            <div class="tb-error" *ngIf="error">
              <i class="bi bi-exclamation-circle me-2"></i>{{ error }}
            </div>

            <button type="submit" class="tb-btn tb-btn-full" [disabled]="loading" style="margin-top:0.5rem;">
              <span *ngIf="loading" class="tb-spinner" style="width:16px;height:16px;border-width:2px;"></span>
              <span *ngIf="!loading"><i class="bi bi-arrow-right-circle me-1"></i></span>
              {{ loading ? 'Connexion…' : 'Se connecter' }}
            </button>
          </form>

          <div class="tb-auth-divider anim-3">ou</div>

          <p class="tb-auth-switch anim-3">
            Pas encore de compte ?
            <a routerLink="/auth/register">Créer un compte</a>
          </p>
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
      this.error = e.message || 'Email ou mot de passe incorrect';
    } finally {
      this.loading = false;
    }
  }
}
