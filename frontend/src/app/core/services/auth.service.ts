import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from './api.service';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<any>(this.loadFromStorage());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private router: Router,
    private apiService: ApiService
  ) {}

  private loadFromStorage(): any {
    try {
      const stored = localStorage.getItem('tb_user');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  }

  private saveToStorage(user: any) {
    if (user) {
      localStorage.setItem('tb_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('tb_user');
    }
  }

  /** Identifiant unique démo basé sur l'email */
  private demoUid(email: string): string {
    return btoa(email).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
  }

  async login(email: string, password: string): Promise<void> {
    // Mode démo : retrouver l'utilisateur par son UID démo dans le backend
    try {
      const user = await this.apiService.getProfile(this.demoUid(email)).toPromise();
      this.currentUserSubject.next(user);
      this.saveToStorage(user);
      this.router.navigate(['/tickets']);
    } catch {
      throw new Error('Compte introuvable. Veuillez créer un compte.');
    }
  }

  async register(email: string, password: string, displayName: string, role: string, company?: string): Promise<void> {
    // Mode démo : créer l'utilisateur directement dans le backend
    const user = await this.apiService.registerUser({
      firebaseUid: this.demoUid(email),
      email,
      displayName,
      role,
      company: company || null
    }).toPromise();

    this.currentUserSubject.next(user);
    this.saveToStorage(user);
    this.router.navigate(['/tickets']);
  }

  async logout(): Promise<void> {
    this.currentUserSubject.next(null);
    this.saveToStorage(null);
    this.router.navigate(['/auth/login']);
  }

  async getToken(): Promise<string | null> {
    // En mode démo, pas de token — le backend accepte toutes les requêtes
    return null;
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }
}
