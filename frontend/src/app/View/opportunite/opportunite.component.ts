import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { signal, computed } from '@angular/core';
import { ChunkPipe } from '../../pipes/chunk.pipe'; // adapte le chemin
import { FormsModule } from '@angular/forms'; // pour les selects/recherche


type Job = {
  id: string;
  title: string;
  company: string;
  city: string;
  typeContrat: 'CDI'|'CDD'|'Stage'|'Freelance';
  mode: 'Hybride'|'Télétravail'|'Présentiel'|'Full Remote';
  salaireMinK: number;
  salaireMaxK: number;
  compatibilite: number; // 0..100
  competences: string[];
  badge?: string; // ex: 'Nouveau' | 'Hot'
};

@Component({
  selector: 'app-opportunites',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule,ChunkPipe],
  templateUrl: './opportunite.component.html',
  styleUrls: ['./opportunite.component.scss']
})
export class OpportuniteComponent {
  // Couleurs/branding proches de ton existant
  primary = '#00b4d8';
  primaryDark = '#0077b6';
  accent = '#7c4dff';
  success = '#10b981';
  warning = '#f59e0b';

  // Données statiques (mock)
  jobs = signal<Job[]>([
    {
      id: 'tc-dev-fullstack',
      title: 'Développeur Full Stack Senior',
      company: 'TechCorp',
      city: 'Paris',
      typeContrat: 'CDI',
      mode: 'Hybride',
      salaireMinK: 45, salaireMaxK: 55,
      compatibilite: 92,
      competences: ['React', 'Node.js', 'TypeScript'],
      badge: 'Nouveau'
    },
    {
      id: 'sl-lead-dev',
      title: 'Lead Developer',
      company: 'StartupLab',
      city: 'Bordeaux',
      typeContrat: 'CDI',
      mode: 'Full Remote',
      salaireMinK: 48, salaireMaxK: 58,
      compatibilite: 88,
      competences: ['Vue', 'GraphQL', 'MongoDB']
    },
    {
      id: 'ge-devops',
      title: 'Ingénieur DevOps',
      company: 'GreenEnergy',
      city: 'Toulouse',
      typeContrat: 'CDI',
      mode: 'Hybride',
      salaireMinK: 42, salaireMaxK: 52,
      compatibilite: 85,
      competences: ['Docker', 'Kubernetes', 'AWS']
    },
    {
      id: 'ff-analyste-quant',
      title: 'Analyste Quantitatif',
      company: 'FinanceFlow',
      city: 'Lyon',
      typeContrat: 'CDI',
      mode: 'Présentiel',
      salaireMinK: 50, salaireMaxK: 65,
      compatibilite: 78,
      competences: ['Python', 'SQL', 'Machine Learning']
    },
    {
      id: 'ht-product-manager',
      title: 'Product Manager',
      company: 'HealthTech',
      city: 'Marseille',
      typeContrat: 'CDI',
      mode: 'Hybride',
      salaireMinK: 55, salaireMaxK: 70,
      compatibilite: 71,
      competences: ['Product Strategy', 'Agile', 'Data Analysis']
    }
  ]);

  // Etat des filtres
  q = signal<string>('');
  fContrat = signal<string>('Tous');
  fMode = signal<string>('Tous');
  fSecteur = signal<string>('Tous'); // placeholder
  fSalaireMin = signal<number | null>(null);
  fSalaireMax = signal<number | null>(null);
  onlyHighMatch = signal<boolean>(false);

  // Résultats filtrés
  filtered = computed(() => {
    const q = this.q().toLowerCase().trim();
    const c = this.fContrat();
    const m = this.fMode();
    const sMin = this.fSalaireMin();
    const sMax = this.fSalaireMax();
    const onlyHigh = this.onlyHighMatch();

    return this.jobs().filter(j => {
      const okQ = !q || [j.title, j.company, j.city, ...j.competences].join(' ').toLowerCase().includes(q);
      const okC = c === 'Tous' || j.typeContrat === c;
      const okM = m === 'Tous' || j.mode === m;
      const okSMin = sMin == null || j.salaireMinK >= sMin;
      const okSMax = sMax == null || j.salaireMaxK <= sMax;
      const okMatch = !onlyHigh || j.compatibilite >= 70;
      return okQ && okC && okM && okSMin && okSMax && okMatch;
    });
  });

  // Helpers
  resetSalaire() { this.fSalaireMin.set(null); this.fSalaireMax.set(null); }
  setRange(min: number|null, max: number|null) { this.fSalaireMin.set(min); this.fSalaireMax.set(max); }
}