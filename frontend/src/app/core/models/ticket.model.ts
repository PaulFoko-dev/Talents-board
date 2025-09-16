/**
 * Ticket model
 * Représente un ticket de recherche (créé par entreprise ou candidat).
 */
export interface Ticket {
  id: string;
  ownerId: string;
  type: 'SEARCH_TALENT' | 'SEARCH_OPPORTUNITY';
  skills: string[];
  description: string;
}
