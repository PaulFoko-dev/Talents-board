import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FooterComponent } from '../components/footer/footer.component';
import { HeaderComponent } from '../components/header/header.component';
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-inscription',
  standalone: true,
  imports: [FooterComponent, HeaderComponent, FormsModule],
  templateUrl: './inscription.html',
  styleUrls: ['./inscription.scss']
})
export class Inscription {
  userType: 'candidat' | 'entreprise' = 'candidat';
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;
  
  // Modèle pour les candidats
  candidat = {
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    confirmationMotDePasse: '',
    competences: '',
    cv: null as File | null
  };
  
  // Modèle pour les entreprises
  entreprise = {
    nom: '',
    adresse: '',
    email: '',
    motDePasse: '',
    confirmationMotDePasse: ''
  };

  constructor(private router: Router) {}

  setUserType(type: 'candidat' | 'entreprise'): void {
    this.userType = type;
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Vérifications du fichier
      if (file.size > 5 * 1024 * 1024) {
        alert('Le fichier est trop volumineux. Taille maximale: 5MB');
        return;
      }
      if (file.type !== 'application/pdf') {
        alert('Seuls les fichiers PDF sont acceptés');
        return;
      }
      this.candidat.cv = file;
    }
  }

  onSubmit(): void {
    if (this.userType === 'candidat') {
      
      if (this.candidat.motDePasse !== this.candidat.confirmationMotDePasse) {
        alert('Les mots de passe ne correspondent pas');
        return;
      }
      
      if (!this.candidat.cv) {
        alert('Veuillez télécharger votre CV');
        return;
      }
      
      console.log('Inscription candidat:', this.candidat);
      
    } else {
      if (this.entreprise.motDePasse !== this.entreprise.confirmationMotDePasse) {
        alert('Les mots de passe ne correspondent pas');
        return;
      }
      
      console.log('Inscription entreprise:', this.entreprise);
    }
    
    // Redirection après inscription réussie
    this.router.navigate(['/connexion']);
  }

  goToLogin(): void {
    this.router.navigate(['/connexion']);
  }
}