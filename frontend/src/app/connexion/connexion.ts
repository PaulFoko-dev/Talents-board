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
  styleUrl: './connexion.scss'
})
export class Connexion {
  isVisible = true;
  showPassword = false;

  identifiant = {
    email: '',
    motDePasse: ''
  };

  constructor(private router: Router, private authService: AuthService) {}

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

      if (res.status === 200) {
        
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('userId', res.data.userId);

 
        this.router.navigate(['/contact']);
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
