import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,              // ✅ indispensable
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {

  menuItems = [
    { icon: 'user', label: 'Profil', route: '/profile' },
    { icon: 'ticket', label: 'Mes tickets', route: '/tickets' },
    { icon: 'briefcase', label: 'Opportunités', route: '/opportunite' },
    { icon: 'file-text', label: 'Candidatures', route: '/candidatures' },
    { icon: 'bell', label: 'Notifications', route: '/notifications', badge: 3 }
  ];

  isCollapsed = false;
}
