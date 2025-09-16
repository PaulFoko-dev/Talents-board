/**
 * User model
 * Représente un utilisateur (candidat ou entreprise).
 */
export interface User {
  id: string;
  email: string;
  displayName: string;
  role: 'CANDIDATE' | 'ENTERPRISE';
}
