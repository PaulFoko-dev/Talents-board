import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { signal, computed } from '@angular/core';
import { ChunkPipe } from '../../pipes/chunk.pipe';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../../components/header/header.component";
import axios from 'axios';
import { BASE_URL } from '../../baseUrl';
import { SidebarNav } from "../../components/sidebar-nav/sidebar-nav";
import { MatSidenavContainer, MatSidenav, MatSidenavContent } from "@angular/material/sidenav";

type Job = {
  id: string;
  title: string;
  company: string;
  city: string;
  typeContrat: 'CDI'|'CDD'|'Stage'|'Freelance'|'Tous' | string;
  mode: 'Hybride'|'Télétravail'|'Présentiel'|'Full Remote' | string;
  salaireMinK?: number | null;
  salaireMaxK?: number | null;
  salaireRangeRaw?: string | null;
  compatibilite: number;
  descriptionRaw?: string;
  domaine?: string;
  localisation?: string;
};

type CandidateDetails = {
  id: string;
  ownerUid: string;
  ownerType: string;
  status: string;
  title: string;
  descriptionRaw: string;
  company: string;
  domaine: string;
  salaryRange: string;
  availability: string;
  localisation: string;
  typeContrat: string;
  niveauExperience: string;
  competences: string[];
  languages: string[];
  avantages: string[];
  modeTravail: string;
  teletravailJourParSemaine: number;
  scoreDenorm: any;
};

@Component({
  selector: 'app-opportunites',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    RouterModule, 
    ChunkPipe, 
    HeaderComponent, 
    SidebarNav, 
    MatSidenavContainer, 
    MatSidenav,
    MatSidenavContent
  ],
  templateUrl: './opportunite.component.html',
  styleUrls: ['./opportunite.component.scss']
})
export class OpportuniteComponent implements OnInit {
  primary = '#00b4d8';
  primaryDark = '#0077b6';
  accent = '#7c4dff';
  success = '#10b981';
  warning = '#f59e0b';

  jobs = signal<Job[]>([]);

  // Etat des filtres
  q = signal<string>('');
  fContrat = signal<string>('Tous');
  fMode = signal<string>('Tous');
  fSecteur = signal<string>('Tous');
  fSalaireMin = signal<number | null>(null);
  fSalaireMax = signal<number | null>(null);
  onlyHighMatch = signal<boolean>(false);

  // Etat du modal
  showModal = false;
  isLoading = false;
  candidateDetails: CandidateDetails | null = null;

  filtered = computed(() => {
    const q = this.q().toLowerCase().trim();
    const c = this.fContrat();
    const m = this.fMode();
    const sMin = this.fSalaireMin();
    const sMax = this.fSalaireMax();
    const onlyHigh = this.onlyHighMatch();

    return this.jobs().filter(j => {
      const hay = [j.title, j.company, j.city, j.domaine, j.descriptionRaw, j.localisation, j.salaireRangeRaw].join(' ').toLowerCase();
      const okQ = !q || hay.includes(q);
      const okC = c === 'Tous' || !c || j.typeContrat === c;
      const okM = m === 'Tous' || !m || j.mode === m;
      const jobMin = j.salaireMinK ?? null;
      const jobMax = j.salaireMaxK ?? null;
      const okSMin = sMin == null || (jobMax != null ? jobMax >= sMin : true);
      const okSMax = sMax == null || (jobMin != null ? jobMin <= sMax : true);
      const okMatch = !onlyHigh || j.compatibilite >= 70;
      return okQ && okC && okM && okSMin && okSMax && okMatch;
    });
  });

  ngOnInit(): void {
    this.fetchPublished();
  }

  async fetchPublished() {
    try {
      const token = localStorage.getItem('token');
      const headers: any = {};
      if (token) headers.Authorization = `Bearer ${token}`;

      const url = `${BASE_URL}api/tickets/published`;
      const resp = await axios.get(url, { headers, timeout: 80000 });
      const payload = resp.data;

      if (!payload || !Array.isArray(payload.data)) {
        console.error('Réponse inattendue de api/tickets/published', payload);
        return;
      }

      const mapped: Job[] = payload.data.map((d: any) => {
        let min: number | null = null;
        let max: number | null = null;
        const sr: string | undefined = d.salaryRange;
        if (sr && typeof sr === 'string') {
          const nums = sr.match(/(\d{1,3}(?:[.,]\d+)?)/g);
          if (nums && nums.length >= 1) {
            const parsed = nums.map(n => parseFloat(n.replace(',', '.')));
            min = parsed[0] || null;
            if (parsed.length >= 2) max = parsed[1] || null;
            if (/[kK]/.test(sr)) {
              if (min != null) min = Math.round(min);
              if (max != null) max = Math.round(max);
            }
          }
        }

        let compat = 0;
        try {
          const sd = d.scoreDenorm;
          if (sd && typeof sd === 'object') {
            const text = JSON.stringify(sd);
            const m = text.match(/-?\d+(\.\d+)?/);
            if (m) {
              const v = parseFloat(m[0]);
              if (!Number.isNaN(v)) {
                compat = Math.max(0, Math.min(100, Math.round(v)));
              }
            }
          }
        } catch (e) { /* ignore */ }

        return {
          id: d.id,
          title: d.title || d.descriptionRaw?.split('\n')?.[0] || 'Offre',
          company: d.company || 'Société',
          city: d.localisation || d.city || d.localisation || '',
          typeContrat: d.typeContrat || 'Tous',
          mode: d.modeTravail || 'Tous',
          salaireMinK: min,
          salaireMaxK: max,
          salaireRangeRaw: d.salaryRange || null,
          compatibilite: compat,
          descriptionRaw: d.descriptionRaw || '',
          domaine: d.domaine || '',
          localisation: d.localisation || ''
        } as Job;
      });

      this.jobs.set(mapped);
      console.log('✅ offres chargées', mapped.length);
    } catch (error: any) {
      console.error('Échec fetchPublished:', error?.response?.data ?? error?.message ?? error);
    }
  }

  // Méthodes pour le modal
  async openCandidateModal(ticketId: string) {
    console.log('Ouverture du modal pour le ticket:', ticketId);
    this.showModal = true;
    this.isLoading = true;
    this.candidateDetails = null;

    try {
      const token = localStorage.getItem('token');
      const headers: any = {};
      if (token) headers.Authorization = `Bearer ${token}`;

      const url = `${BASE_URL}api/tickets/${ticketId}`;
      const response = await axios.get(url, { headers, timeout: 80000 });
      
      if (response.data && response.data.data) {
        this.candidateDetails = response.data.data;
        console.log('Détails du candidat chargés:', this.candidateDetails);
      } else {
        console.error('Réponse inattendue de api/tickets/{id}', response.data);
      }
    } catch (error: any) {
      console.error('Erreur lors du chargement des détails du candidat:', error?.response?.data ?? error?.message ?? error);
    } finally {
      this.isLoading = false;
    }
  }

  closeModal() {
    console.log('Fermeture du modal');
    this.showModal = false;
    this.candidateDetails = null;
    this.isLoading = false;
  }

  contactCandidate() {
    if (this.candidateDetails) {
      console.log('Contacter le candidat:', this.candidateDetails);
      // Implémentez votre logique de contact ici
      alert(`Fonction de contact pour ${this.candidateDetails.title} à implémenter`);
    }
  }

  resetSalaire() { 
    this.fSalaireMin.set(null); 
    this.fSalaireMax.set(null); 
  }
  
  setRange(min: number|null, max: number|null) { 
    this.fSalaireMin.set(min); 
    this.fSalaireMax.set(max); 
  }
}