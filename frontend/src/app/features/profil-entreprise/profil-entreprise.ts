import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { Subscription } from 'rxjs';

// Import du service - ajustez le chemin selon votre structure
import { ProfilEntrepriseService, CompanyProfile } from '../../services/profilEntreprise.service';

// Import du SidebarNav - ajustez le chemin
import { SidebarNav } from '../../components/sidebar-nav/sidebar-nav';

@Component({
  selector: 'app-profil-entreprise',
  standalone: true,
  imports: [
    CommonModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatProgressSpinnerModule,
    MatSidenavModule,
    SidebarNav // IMPORTANT : Ajoutez SidebarNav ici
  ],
  templateUrl: './profil-entreprise.html',
  styleUrls: ['./profil-entreprise.scss']
})
export class ProfilEntreprise implements OnInit {
  isReadonly: boolean = true;
  company: CompanyProfile = {
    id: '',
    nom: '',
    logo: '/assets/images/TalentsBoard.png',
    description: '',
    secteur: '',
    localisation: '',
    siteWeb: '',
    email: '',
    numero: '',
    // contact: {
    // }
  };

  isLoading = true;
  error: string | null = null;

  constructor(
    private router: Router, // AJOUT: Injection du Router
    private profilEntrepriseService: ProfilEntrepriseService
  ) {}

  async ngOnInit(): Promise<void> {
    await this.loadCompanyProfile();
    
  }

 async loadCompanyProfile(): Promise<void> {
    this.isLoading = true;
    this.error = null;

    try {
     this.profilEntrepriseService.getCompanyProfile().then((data) => {
      this.company = data;
      console.log("company: ",this.company);
      
     });
    } catch (error: any) {
      console.error('Erreur lors du chargement du profil:', error);
      this.error = error.message || 'Erreur lors du chargement du profil';
      this.loadMockData();
    } finally {
      this.isLoading = false;
    }
  }

  // Données mockées pour le développement
  private loadMockData(): void {
    this.company = {
      id: '1',
      nom: 'Nom de l\'entreprise',
      logo: '/assets/images/company-logo-placeholder.png',
      description: 'Description de l\'entreprise et de ses activités principales.',
      secteur: 'Technologie',
      localisation: 'Paris, France',
      siteWeb: 'https://www.example.com',
      // contact: {
      email: 'contact@example.com',
      numero: '+33 1 23 45 67 89',
      // website: 'https://www.example.com'
      // }
    };
    this.isLoading = false;
  }

  // Méthode pour formater les URLs
  getFullUrl(url: string): string {
    if (!url) return '';
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }
    return `http://${url}`;
  }

  onImageError(event: any): void {
    event.target.src = '/assets/images/TalentsBoard.png';
  }

  editProfile(): void {
    // this.router.navigate(['/edit-profil-entreprise']);
    this.isReadonly = false;
  }  
  updateProfile(event: Event) {
    // event.preventDefault();
    console.log('Updating profile with data:', this.company);
    
    this.profilEntrepriseService.updateCompanyProfile(this.company)
      .then(() => {
        console.log('✅ Profil mis à jour avec succès !');
        this.isReadonly = true;
      })
      .catch(error => {
        console.error('❌ Erreur lors de la mise à jour du profil', error);
      });
  }
}