import { Injectable } from '@angular/core';
import { Auth, signInWithEmailAndPassword, createUserWithEmailAndPassword, signOut, User, onAuthStateChanged } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { ApiService } from './api.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<any>(null);
  currentUser$ = this.currentUserSubject.asObservable();
  private firebaseUser: User | null = null;

  constructor(
    private auth: Auth,
    private router: Router,
    private apiService: ApiService
  ) {
    onAuthStateChanged(this.auth, async (user) => {
      this.firebaseUser = user;
      if (user) {
        try {
          const profile = await this.apiService.getProfile(user.uid).toPromise();
          this.currentUserSubject.next(profile);
        } catch {
          this.currentUserSubject.next({ firebaseUid: user.uid, email: user.email });
        }
      } else {
        this.currentUserSubject.next(null);
      }
    });
  }

  async login(email: string, password: string): Promise<void> {
    await signInWithEmailAndPassword(this.auth, email, password);
    this.router.navigate(['/tickets']);
  }

  async register(email: string, password: string, displayName: string, role: string, company?: string): Promise<void> {
    const cred = await createUserWithEmailAndPassword(this.auth, email, password);
    await this.apiService.registerUser({
      firebaseUid: cred.user.uid,
      email,
      displayName,
      role,
      company
    }).toPromise();
    this.router.navigate(['/tickets']);
  }

  async logout(): Promise<void> {
    await signOut(this.auth);
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  async getToken(): Promise<string | null> {
    if (this.firebaseUser) {
      return this.firebaseUser.getIdToken();
    }
    return null;
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }
}
