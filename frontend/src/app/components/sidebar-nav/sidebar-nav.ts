import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatList, MatListItem, MatNavList } from '@angular/material/list';
import { MatBadge } from '@angular/material/badge';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-sidebar-nav',
  standalone: true, // ✅ Correction: "standalone" au lieu de "standlone"
  imports: [CommonModule, MatList, MatListItem, MatNavList, MatBadge, MatIcon, RouterLink],
  templateUrl: './sidebar-nav.html',
  styleUrls: ['./sidebar-nav.scss']
})
export class SidebarNav {
  @Input() userType: 'entreprise' | 'candidat' = 'entreprise'; // ✅ Input maintenant reconnu
  items = [
    { icon: 'person', label: 'Profil', route: '/profil/entreprise' },
    { icon: 'receipt', label: 'Tickets de besoin' },
    { icon: 'group', label: 'Talents disponibles' },
    { icon: 'send', label: 'Candidatures reçues' },
    { icon: 'notifications', label: 'Notifications', badge: 5 }
  ];
}