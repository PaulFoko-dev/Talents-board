import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-connexion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './connexion.html',
  styleUrls: ['./connexion.scss']
})
export class Connexion {
  userType: 'candidat' | 'entreprise' = 'candidat';
  isVisible = true;
  showPassword = false;

  identifiant = {
    email: '',
    motDePasse: ''
  };


  constructor(private router: Router, private authService: AuthService) { }

  setUserType(type: 'candidat' | 'entreprise'): void {
    this.userType = type;
  }

  close() {
    this.isVisible = false;
    this.router.navigate(['/']);
  }

  async onSubmit() {
    try {
      const res = await this.authService.login(
        this.identifiant.email,
        this.identifiant.motDePasse
      );

      console.log('Réponse du backend:', res);

      console.log('Type utilisateur:', res.data.nom + ' ' + res.data.prenom, res.data.typeUser);
      if (res.status === 200) {
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('userId', res.data.userId);
        localStorage.setItem('userType', res.data.typeUser);

        try {
          const userProfile = await this.authService.getUserProfile(res.data.userId);
          const Username = await this.authService.getUserFullName(res.data.userId);
          localStorage.setItem('username', Username);
          console.log('Profil utilisateur', userProfile);
        } catch (profileError) {
          console.warn('Impossible de récupérer le profil');
        }

        if (res.data.typeUser === 'CANDIDAT') {
          this.router.navigate(['/ticketCandidat']);
        } else {
          if (res.data.typeUser === 'ENTREPRISE') {
            this.router.navigate(['/dashboardEntreprise']);
          }
        }

      } else {
        alert(res.message || 'Erreur de connexion');
      }
    } catch (error: any) {
      alert(error.message || 'Identifiants invalides');
    }
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }
}
