import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * AuthInterceptor
 * - Ajoute automatiquement le token Firebase aux requêtes HTTP.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // TODO: Récupérer le token depuis AuthService et l'ajouter au header Authorization
    return next.handle(req);
  }
}
