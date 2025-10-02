import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidebarNav } from '../../components/sidebar-nav/sidebar-nav'; // Import du composant

@Component({
  selector: 'app-profil-entreprise',
  standalone: true, 
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatSidenavModule, SidebarNav ],
  templateUrl: './profil-entreprise.html',
  styleUrls: ['./profil-entreprise.scss']
})
export class ProfilEntreprise {
  company = {
    name: 'Tech Innovate Solutions',
    logo: 'assets/tech-innovate-logo.svg',
    description: 'Tech Innovate Solutions est une entreprise leader dans le développement de logiciels innovants et la fourniture de services de conseil technologique. Nous nous engageons à transformer les défis commerciaux en opportunités numériques grâce à des solutions sur mesure. Notre équipe d\'experts est dédiée à la création de technologies de pointe qui propulsent l\'avenir.',
    contact: {
      email: 'contact@techinnovate.com',
      phone: '+33 1 23 45 67 89',
      address: '123, Rue de l\'Innovation, 75001 Paris, France',
      website: 'www.techinnovate.com'
    }
  };

  editProfile() {
    console.log('Éditer le profil cliqué');
    // Logique pour naviguer vers un formulaire d'édition
  }
}