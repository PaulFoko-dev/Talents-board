import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FooterComponent } from '../components/footer/footer.component';
import { HeaderComponent } from '../components/header/header.component';
import { FormsModule } from "@angular/forms";
import { AuthService } from '../core/services/auth.service';

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
    confirmationMotDePasse: ''
  };
  
  // Modèle pour les entreprises
  entreprise = {
    nom: '',
    email: '',
    motDePasse: '',
    confirmationMotDePasse: '',
    secteur: '',
    localisation: '',
    siteWeb: ''
  };


  constructor(private router: Router,  private authService: AuthService) {}

  setUserType(type: 'candidat' | 'entreprise'): void {
    this.userType = type;
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

    async register(): Promise<void> {
      if (this.userType === 'candidat') {
        console.log(this);
        if (this.candidat.motDePasse !== this.candidat.confirmationMotDePasse) {
          alert('Les mots de passe ne correspondent pas');
          return;
        }
        
        try {
          const {confirmationMotDePasse, ...candidat} = this.candidat;
          const res = await this.authService.register_candidat(
            candidat
          );

          console.log('Réponse du backend:', res);

          if (res.status === 200) {
              this.router.navigate(['/connexion'])
            
          } else {
            alert(res.message);
          }
        } catch (error: any) {
          alert(error.message );
        }
        
    } else {
      if (this.entreprise.motDePasse !== this.entreprise.confirmationMotDePasse) {
        alert('Les mots de passe ne correspondent pas');
        return;
      }
      
      try {
          const {confirmationMotDePasse, ...entreprise} = this.candidat;
          const res = await this.authService.register_entreprise(
            entreprise
          );

          console.log('Réponse du backend:', res);

          if (res.status === 200) {
              this.router.navigate(['/connexion'])
            
          } else {
            alert(res.message);
          }
        } catch (error: any) {
          alert(error.message );
        }
    }

  }
}