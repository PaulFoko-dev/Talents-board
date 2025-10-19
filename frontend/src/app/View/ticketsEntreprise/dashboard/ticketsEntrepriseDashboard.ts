import { Component, OnInit, inject, Pipe, PipeTransform } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TicketEntrepriseService, TalentMatch, TalentMatchResponse } from '../../../services/ticketEntreprise.service';
import { TicketService } from '../../../services/ticket.service';

export interface Ticket {
  id: string;
  ownerUid: string;
  ownerType: 'CANDIDAT' | 'ENTREPRISE';
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
  scoreDenorm: {
    [key: string]: any;
  };
}

@Pipe({
  name: 'truncate',
  standalone: true
})
export class TruncatePipe implements PipeTransform {
  transform(value: string, limit: number = 100, completeWords: boolean = false, ellipsis: string = '...'): string {
    if (!value) return '';

    if (value.length <= limit) {
      return value;
    }

    if (completeWords) {
      limit = value.substr(0, limit).lastIndexOf(' ');
    }

    return value.substr(0, limit) + ellipsis;
  }
}

@Component({
  selector: 'ticketsEntrepriseDashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, TruncatePipe],
  templateUrl: './ticketsEntrepriseDashboard.html',
  styleUrls: ['./ticketsEntrepriseDashboard.scss']
})
export class EntrepriseDashboardComponent implements OnInit {
  private ticketEntrepriseService = inject(TicketEntrepriseService);
  tickets: Ticket[] = [];
  isLoading: boolean = true;
  errorMessage: string | null = null;
  
  // Propriétés pour les matches - AJOUT DES PROPRIÉTÉS MANQUANTES
  showMatchesModal: boolean = false;
  selectedTicketId: string | null = null;
  selectedTicketTitle: string = '';
  talentMatches: TalentMatch[] = [];
  matchesLoading: boolean = false;
  matchesError: string | null = null;

  // Navigation
  activeSection: string = 'tickets'; // 'tickets' par défaut

  constructor(
    private ticketService: TicketService,
    private router: Router
  ) { }

  // Méthode pour changer la section active
  setActiveSection(section: string): void {
    this.activeSection = section;

    // Si on clique sur "Tickets de besoin", recharger les tickets
    if (section === 'tickets') {
      this.loadTickets();
    }
  }

  async openMatchesModal(ticketId: string, ticketTitle: string = ''): Promise<void> {
    console.log('Opening matches modal for ticket:', ticketId);
    this.selectedTicketId = ticketId;
    this.selectedTicketTitle = ticketTitle;
    this.showMatchesModal = true;
    this.matchesError = null;
    
    await this.loadTicketMatches(ticketId);
  }

  closeMatchesModal(): void {
    console.log('Closing matches modal');
    this.showMatchesModal = false;
    this.selectedTicketId = null;
    this.selectedTicketTitle = '';
    this.talentMatches = [];
    this.matchesError = null;
  }

  async loadTicketMatches(ticketId: string, limit: number = 10): Promise<void> {
    this.matchesLoading = true;
    this.matchesError = null;

    try {
      // APPEL CORRECT DU SERVICE
      const response: TalentMatchResponse = await this.ticketEntrepriseService.getTicketMatches(ticketId, limit);
      
      console.log('✅ Réponse API matches:', response);
      
      if (response && response.data && Array.isArray(response.data)) {
        this.talentMatches = response.data;
        console.log('✅ Matches chargés avec succès:', this.talentMatches.length + ' matches');
      } else {
        console.warn('⚠️ Format de réponse inattendu:', response);
        this.talentMatches = [];
      }
    } catch (error: any) {
      console.error('❌ Erreur lors du chargement des matches:', error);
      this.matchesError = this.getMatchesErrorMessage(error);
      this.talentMatches = [];
    } finally {
      this.matchesLoading = false;
    }
  }

  private getMatchesErrorMessage(error: any): string {
    if (error.response?.status === 401) {
      return 'Erreur d\'authentification. Veuillez vous reconnecter.';
    } else if (error.response?.status === 404) {
      return 'Aucun match trouvé pour ce ticket.';
    } else if (error.response?.status >= 500) {
      return 'Erreur serveur. Veuillez réessayer plus tard.';
    } else {
      return 'Erreur lors du chargement des matches.';
    }
  }

  // Méthodes utilitaires pour les matches
  formatMatchScore(score: number): string {
    return `${Math.round(score * 100)}%`;
  }

  getTalentInitials(ownerUid: string): string {
    const parts = ownerUid.split(/[-_]/);
    if (parts.length >= 2) {
      return (parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
    }
    return ownerUid.substring(0, 2).toUpperCase();
  }

  contactTalent(talentId: string): void {
    console.log('Contacter le talent:', talentId);
    // Implémentez la logique de contact
  }

  viewTalentProfile(talentId: string): void {
    console.log('Voir le profil du talent:', talentId);
    // Implémentez la navigation vers le profil
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'ACTIF': 'status-active',
      'NOUVEAU': 'status-new',
      'EN_COURS': 'status-in-progress',
      'TERMINE': 'status-completed',
      'ANNULE': 'status-cancelled'
    };
    return statusMap[status?.toUpperCase()] || 'status-default';
  }

  // Méthode pour voir les détails d'un ticket
  viewTicketDetails(ticketId: string): void {
    console.log('Voir les détails du ticket:', ticketId);
    // Implémentez la navigation vers la page de détails
    // this.router.navigate(['/ticket', ticketId]);
  }

  // Méthode pour voir les matches d'un ticket
  viewTicketMatches(ticketId: string): void {
    console.log('Voir les matches du ticket:', ticketId);
    // Implémentez la navigation vers la page des matches
    // this.router.navigate(['/ticket', ticketId, 'matches']);
  }

  // Méthode pour formater la date (à adapter selon vos besoins)
  getFormattedDate(): string {
    return new Date().toLocaleDateString('fr-FR');
  }

  ngOnInit(): void {
    this.loadMatches();
    this.loadTickets();
  }

  loadMatches(): void {
    this.isLoading = true;
    this.errorMessage = null;
    setTimeout(() => {
      this.isLoading = false;
    }, 1000);
  }

  async loadTickets(): Promise<void> {
    this.isLoading = true;
    this.errorMessage = null;

    try {
      const response = await this.ticketService.getTickets();
      this.tickets = response.data || []; // On s'assure que 'tickets' est un tableau
    } catch (error) {
      this.errorMessage = "Impossible de charger les tickets.";
      console.error(error);
    } finally {
      this.isLoading = false;
    }
  }

  // Modal properties
  showTicketModal: boolean = false;
  newTicketDescription: string = '';
  creatingTicket: boolean = false;
  ticketError: string = '';
  ticketSuccess: boolean = false;

  filters = {
    poste: '',
    experience: 'Tous niveaux',
    contrat: 'Tous types',
    localisation: 'Toutes localisations'
  };

  // Méthodes du modal
  openTicketModal(): void {
    console.log('Opening modal...');
    this.showTicketModal = true;
    this.newTicketDescription = '';
    this.ticketError = '';
    this.ticketSuccess = false;
  }

  closeTicketModal(): void {
    console.log('Closing modal...');
    this.showTicketModal = false;
    this.newTicketDescription = '';
    this.ticketError = '';
    this.creatingTicket = false;
    this.ticketSuccess = false;
  }

  // Méthode modifiée pour appeler l'API
  async createTicket(): Promise<void> {
    console.log('Creating ticket...', this.newTicketDescription);

    if (!this.newTicketDescription.trim()) {
      this.ticketError = 'Veuillez saisir une description pour le ticket';
      return;
    }

    this.creatingTicket = true;
    this.ticketError = '';
    this.ticketSuccess = false;

    try {
      // Appel à votre service API
      const response = await this.ticketEntrepriseService.createSimpleEntrepriseTicket(this.newTicketDescription.trim());

      console.log('✅ Ticket créé avec succès:', response);
      this.ticketSuccess = true;

      // Fermer le modal après 2 secondes et recharger les données
      setTimeout(() => {
        this.closeTicketModal();
        this.loadMatches(); // Recharger les matches avec le nouveau ticket
      }, 2000);

    } catch (error: any) {
      console.error('❌ Erreur lors de la création du ticket:', error);
      this.ticketError = this.getTicketErrorMessage(error);
    } finally {
      this.creatingTicket = false;
    }
  }

  private getTicketErrorMessage(error: any): string {
    if (error.response?.status === 401) {
      return 'Erreur d\'authentification. Veuillez vous reconnecter.';
    } else if (error.response?.status === 400) {
      return 'Description invalide. Veuillez vérifier votre saisie.';
    } else if (error.response?.status === 403) {
      return 'Vous n\'avez pas les permissions pour créer un ticket.';
    } else if (error.response?.status >= 500) {
      return 'Erreur serveur. Veuillez réessayer plus tard.';
    } else {
      return 'Erreur lors de la création du ticket. Veuillez réessayer.';
    }
  }

  resetFilters(): void {
    this.filters = {
      poste: '',
      experience: 'Tous niveaux',
      contrat: 'Tous types',
      localisation: 'Toutes localisations'
    };
  }

  voirProfilsSuggères(): void {
    console.log('Voir les profils suggérés');
    this.loadMatches();
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  }

  formatMatchPercentage(percentage: number): string {
    return `${percentage}%`;
  }

  reloadData(): void {
    this.loadMatches();
  }

  onDescriptionInput(event: any): void {
    const value = event.target.value;
    if (value.length > 500) {
      event.target.value = value.substring(0, 500);
      this.newTicketDescription = event.target.value;
    }
  }
}