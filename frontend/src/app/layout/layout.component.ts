import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from './sidebar/sidebar.component';
import { FooterComponent } from '../components/footer/footer.component'; 

@Component({
  selector: 'app-layout',
  standalone: true,              // ✅ standalone ici aussi
  imports: [CommonModule, RouterModule, SidebarComponent, FooterComponent],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {

  // État de la sidebar (ouverte ou fermée sur mobile)
  isSidebarOpen = true;
  isMobileView = false;

  // Largeur minimale pour considérer un affichage "desktop"
  readonly MOBILE_BREAKPOINT = 992;

  constructor() {
    // Initialise selon la taille d'écran actuelle
    this.isSidebarOpen = window.innerWidth >= this.MOBILE_BREAKPOINT;
  }

  /**
   * Écoute le redimensionnement de la fenêtre
   * pour adapter la sidebar automatiquement
   */
  @HostListener('window:resize', ['$event'])
  onResize(event: any): void {
    const width = event.target.innerWidth;
    if (width < this.MOBILE_BREAKPOINT) {
      this.isSidebarOpen = false;
    } else {
      this.isSidebarOpen = true;
    }
  }

  /**
   * Bascule la sidebar (menu burger)
   */
  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  /**
   * Ferme la sidebar en cliquant sur l’overlay
   */
  closeSidebar(): void {
    if (window.innerWidth < this.MOBILE_BREAKPOINT) {
      this.isSidebarOpen = false;
    }
  }
}

