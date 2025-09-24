import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

/**
 * AuthGuard
 * - Protège les routes nécessitant une authentification.
 */
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    // TODO: Vérifier via AuthService si l'utilisateur est connecté
    return true;
  }
}
