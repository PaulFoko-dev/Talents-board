import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

interface FeatureItem {
  icon: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-features-grid',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule], // 🔹 Ajouté ici
  templateUrl: './featuresgrid.html',
  styleUrl: './featuresgrid.scss'
})
export class FeaturesGridComponent {
  features: FeatureItem[] = [
    { icon: '📄', title: 'Import CV', description: 'Simplifiez votre profil en important votre CV en quelques clics.' },
    { icon: '💡', title: 'Matching intelligent', description: 'Notre algorithme trouve les meilleures opportunités pour vous.' },
    { icon: '🔔', title: 'Notifications', description: 'Recevez des alertes en temps réel sur les nouvelles offres et candidatures.' },
    { icon: '🗂️', title: 'Suivi candidatures', description: 'Gérez et suivez l’état de toutes vos candidatures en un seul endroit.' },
  ];
}
