// sidebar-nav.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { MatList, MatListItem, MatNavList } from '@angular/material/list';
import { MatBadge } from '@angular/material/badge';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

export interface SidebarItem {
  icon: string;
  label: string;
  route: string;
  badge?: number;
  isActive?: boolean;
}

interface UserProfile {
  name: string;
  role: string;
  avatar: string;
}

@Component({
  selector: 'app-sidebar-nav',
  standalone: true,
  imports: [
    CommonModule,
    MatList,
    MatListItem,
    MatNavList,
    MatBadge,
    MatIcon,
    RouterLink,
    MatButtonModule
  ],
  templateUrl: './sidebar-nav.html',
  styleUrls: ['./sidebar-nav.scss']
})
export class SidebarNavComponent implements OnInit {
  
  userProfile: UserProfile = {
    name: '',
    role: '',
    avatar: 'assets/avatar-placeholder.png'
  };

  userType: string = '';
  fullName: string = 'Utilisateur';

  // Navigation pour les candidats
  candidatItems: SidebarItem[] = [
    {
      icon: 'person',
      label: 'Mon Profil',
      route: '/profil',
      isActive: false
    },
    {
      icon: 'work',
      label: 'Mes Tickets',
      route: '/ticketCandidat',
      isActive: false
    },
    {
      icon: 'search',
      label: 'Opportunités',
      route: '/opportunite',
      isActive: false
    },
    {
      icon: 'notifications',
      label: 'Notifications',
      route: '/notifications',
      badge: 3,
      isActive: false
    },
    {
      icon: 'message',
      label: 'Messages',
      route: '/messages',
      badge: 5,
      isActive: false
    }
  ];

  // Navigation pour les entreprises
  entrepriseItems: SidebarItem[] = [
    {
      icon: 'business',
      label: 'Profil Entreprise',
      route: '/profil-entreprise',
      isActive: false
    },
    {
      icon: 'dashboard',
      label: 'Dashboard Talents',
      route: '/dashboardEntreprise',
      isActive: false
    },
    // {
    //   icon: 'group',
    //   label: 'Gestion Talents',
    //   route: '',
    //   isActive: false
    // },
    // {
    //   icon: 'notifications',
    //   label: 'Notifications',
    //   route: '/',
    //   badge: 7,
    //   isActive: false
    // },
  ];

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.initializeUserProfile();
    this.setActiveRoute();
  }

  private initializeUserProfile(): void {
    // Récupérer le type d'utilisateur depuis le localStorage
    const storedUserType = localStorage.getItem('userType');
    
    if (!storedUserType) {
      console.warn('Aucun type d\'utilisateur trouvé, redirection vers la connexion');
      this.router.navigate(['/connexion']);
      return;
    }

    this.userType = storedUserType;
    this.fullName = localStorage.getItem('username') || 'Utilisateur';

    console.log('User type dans le sidebar:', this.userType);
    console.log('Nom d\'utilisateur:', this.fullName);

    if (this.userType === 'CANDIDAT') {
      this.userProfile = {
        name: this.fullName,
        role: 'Candidat',
        avatar: 'assets/avatar-candidat.png'
      };
    } else if (this.userType === 'ENTREPRISE') {
      this.userProfile = {
        name: this.fullName,
        role: 'Recruteur',
        avatar: 'assets/avatar-entreprise.png'
      };
    } else {
      // Type inconnu, valeur par défaut
      this.userProfile = {
        name: this.fullName,
        role: 'Utilisateur',
        avatar: 'assets/avatar-placeholder.png'
      };
    }
  }

  private setActiveRoute(): void {
    const currentRoute = this.router.url;
    console.log('Route actuelle:', currentRoute);

    if (this.userType === 'CANDIDAT') {
      this.candidatItems.forEach(item => {
        item.isActive = currentRoute === item.route;
        console.log(`Item ${item.label} actif: ${item.isActive}`);
      });
    } else if (this.userType === 'ENTREPRISE') {
      this.entrepriseItems.forEach(item => {
        item.isActive = currentRoute === item.route;
        console.log(`Item ${item.label} actif: ${item.isActive}`);
      });
    }
  }

  onItemClick(clickedItem: SidebarItem): void {
    if (this.userType === 'CANDIDAT') {
      this.candidatItems.forEach(item => item.isActive = false);
    } else if (this.userType === 'ENTREPRISE') {
      this.entrepriseItems.forEach(item => item.isActive = false);
    }
    
    clickedItem.isActive = true;

    if (clickedItem.route) {
      this.router.navigate([clickedItem.route]);
    }
  }

  logout(): void {
    // Nettoyer le localStorage
    localStorage.removeItem('userType');
    localStorage.removeItem('username');
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    
    // Rediriger vers la page de connexion
    this.router.navigate(['/connexion']);
  }
}