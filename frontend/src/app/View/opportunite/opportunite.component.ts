import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { signal, computed } from '@angular/core';
import { ChunkPipe } from '../../pipes/chunk.pipe';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../../components/header/header.component";
import axios from 'axios';
import { BASE_URL } from '../../baseUrl';

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
  compatibilite: number; // 0..100
  // compétences/languages/avantages intentionally omitted from display
  descriptionRaw?: string;
  domaine?: string;
  localisation?: string;
};

@Component({
  selector: 'app-opportunites',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ChunkPipe, HeaderComponent],
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

  // Exposé pour debug / action manuelle si besoin
  async fetchPublished() {
    try {
      const token = localStorage.getItem('token');
      const headers: any = {};
      if (token) headers.Authorization = `Bearer ${token}`;

      const url = `${BASE_URL}api/tickets/published`;

      const resp = await axios.get(url, { headers, timeout: 80000 });
      const payload = resp.data;

      // sécurité: valider la forme attendue
      if (!payload || !Array.isArray(payload.data)) {
        console.error('Réponse inattendue de api/tickets/published', payload);
        return;
      }

      const mapped: Job[] = payload.data.map((d: any) => {
        // tenter d'extraire un min/max depuis salaryRange (ex: "40-50k" ou "40k - 50k")
        let min: number | null = null;
        let max: number | null = null;
        const sr: string | undefined = d.salaryRange;
        if (sr && typeof sr === 'string') {
          const nums = sr.match(/(\d{1,3}(?:[.,]\d+)?)/g);
          if (nums && nums.length >= 1) {
            const parsed = nums.map(n => parseFloat(n.replace(',', '.')));
            min = parsed[0] || null;
            if (parsed.length >= 2) max = parsed[1] || null;
            // normaliser en milliers si la chaîne contient 'k' ou 'K'
            if (/[kK]/.test(sr)) {
              if (min != null) min = Math.round(min);
              if (max != null) max = Math.round(max);
            }
          }
        }

        // compatibilite non fournie par l'API: fallback 0
        let compat = 0;
        // si scoreDenorm contient une valeur utile, essayer d'en dériver un pourcentage
        try {
          const sd = d.scoreDenorm;
          // tentative prudente: parcourir et prendre le premier nombre trouvé
          if (sd && typeof sd === 'object') {
            const text = JSON.stringify(sd);
            const m = text.match(/-?\d+(\.\d+)?/);
            if (m) {
              const v = parseFloat(m[0]);
              if (!Number.isNaN(v)) {
                // normaliser grossièrement
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

  resetSalaire() { this.fSalaireMin.set(null); this.fSalaireMax.set(null); }
  setRange(min: number|null, max: number|null) { this.fSalaireMin.set(min); this.fSalaireMax.set(max); }
}
