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
    <div class="tb-auth-wrap">
      <!-- Panneau gauche -->
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
          Rejoignez une communauté de <span>talents</span> et d'<span>entreprises</span>.
        </div>
        <div class="tb-auth-features anim-3">
          <div class="tb-auth-feature">
            <div class="tb-auth-feature-icon"><i class="bi bi-person-badge-fill"></i></div>
            <span>Profil candidat enrichi avec CV et compétences</span>
          </div>
          <div class="tb-auth-feature">
            <div class="tb-auth-feature-icon"><i class="bi bi-building-check"></i></div>
            <span>Espace entreprise pour gérer vos recrutements</span>
          </div>
          <div class="tb-auth-feature">
            <div class="tb-auth-feature-icon"><i class="bi bi-lightning-charge-fill"></i></div>
            <span>Accès immédiat dès la création du compte</span>
          </div>
        </div>
      </div>

      <!-- Panneau droit : formulaire -->
      <div class="tb-auth-form-side">
        <div class="tb-auth-form-box">
          <div class="anim-1">
            <h1 class="tb-auth-title">Créer un compte</h1>
            <p class="tb-auth-sub">Rejoignez la plateforme en quelques secondes</p>
          </div>

          <form (ngSubmit)="onRegister()" class="anim-2">
            <div class="tb-field">
              <label class="tb-label">Nom complet</label>
              <input class="tb-input" type="text" [(ngModel)]="displayName" name="displayName"
                     placeholder="Jean Dupont" required autocomplete="name">
            </div>
            <div class="tb-field">
              <label class="tb-label">Adresse email</label>
              <input class="tb-input" type="email" [(ngModel)]="email" name="email"
                     placeholder="jean.dupont@email.com" required autocomplete="email">
            </div>
            <div class="tb-field">
              <label class="tb-label">Mot de passe</label>
              <input class="tb-input" type="password" [(ngModel)]="password" name="password"
                     placeholder="Min. 6 caractères" required autocomplete="new-password">
            </div>

            <div class="tb-field">
              <label class="tb-label">Je suis…</label>
              <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem;">
                <div class="tb-role-card" [class.selected]="role === 'CANDIDATE'" (click)="role = 'CANDIDATE'">
                  <div class="tb-role-icon"><i class="bi bi-person-fill"></i></div>
                  <div class="tb-role-label">Candidat</div>
                </div>
                <div class="tb-role-card" [class.selected]="role === 'ENTERPRISE'" (click)="role = 'ENTERPRISE'">
                  <div class="tb-role-icon"><i class="bi bi-buildings-fill"></i></div>
                  <div class="tb-role-label">Entreprise</div>
                </div>
              </div>
            </div>

            <div class="tb-field" *ngIf="role === 'ENTERPRISE'">
              <label class="tb-label">Nom de l'entreprise</label>
              <input class="tb-input" type="text" [(ngModel)]="company" name="company"
                     placeholder="Ma Société SAS">
            </div>

            <div class="tb-error" *ngIf="error">
              <i class="bi bi-exclamation-circle me-2"></i>{{ error }}
            </div>

            <button type="submit" class="tb-btn tb-btn-full" [disabled]="loading || !role" style="margin-top:0.5rem;">
              <span *ngIf="loading" class="tb-spinner" style="width:16px;height:16px;border-width:2px;"></span>
              <span *ngIf="!loading"><i class="bi bi-rocket-takeoff me-1"></i></span>
              {{ loading ? 'Création…' : 'Créer mon compte' }}
            </button>
          </form>

          <div class="tb-auth-divider anim-3">ou</div>
          <p class="tb-auth-switch anim-3">
            Déjà un compte ? <a routerLink="/auth/login">Se connecter</a>
          </p>
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
      this.error = 'Erreur : ' + e.message;
    } finally {
      this.loading = false;
    }
  }
}
