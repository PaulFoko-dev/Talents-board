import { Injectable } from '@angular/core';
import axios, { AxiosError } from 'axios';
import { BASE_URL } from '../../app/baseUrl';

export interface TalentMatch {
  ticketId: string;
  ownerUid: string;
  score: number;
  matchedSkills: string[];
}

export interface ApiResponse {
  status: number;
  message: string;
  data: TalentMatch[];
}

export interface TalentMatchResponse {
  status: number;
  message: string;
  data: TalentMatch[];
}

@Injectable({
  providedIn: 'root'
})
export class TicketEntrepriseService {

  constructor() { }

  /**
   * 🔐 Génère les en-têtes d'authentification à partir du localStorage
   */
  private getAuthHeaders() {
    const token = localStorage.getItem('token');
    if (!token) {
      console.log("❌ Token d'authentification introuvable dans le localStorage.");
    }

    return {
      Authorization: `Bearer ${token}`
    };
  }


  /**
   * 🟩 Crée un ticket entreprise (POST /api/tickets/entreprise)
   * Body attendu : { "descriptionRaw": "string" }
   */
  async createSimpleEntrepriseTicket(descriptionRaw: string): Promise<any> {
    const headers = {
      ...this.getAuthHeaders(),
      'Content-Type': 'application/json'
    };

    const body = { descriptionRaw };

    try {
      const response = await axios.post(`${BASE_URL}api/tickets/entreprise`, body, { headers });
      console.log('✅ [createSimpleEntrepriseTicket] Ticket créé avec succès :', response.data);
      return response.data;
    } catch (error) {
      return this.handleApiError('createSimpleEntrepriseTicket', error);
    }
  }
  // Ajoutez cette méthode dans votre service TicketEntrepriseService
  /**
   * 🎯 Récupère les matches d'un ticket entreprise (GET /api/tickets/{id}/matches)
   * @param id - ID du ticket
   * @param limit - Nombre de résultats (défaut: 10)
   */
  async getTicketMatches(id: string, limit: number = 10): Promise<TalentMatchResponse> {
    const headers = this.getAuthHeaders();

    const params = {
      limit: limit
    };

    try {
      const response = await axios.get(`${BASE_URL}api/tickets/${id}/matches`, {
        headers,
        params
      });
      console.log('✅ [getTicketMatches] Matches récupérés avec succès :', response.data);
      return response.data;
    } catch (error) {
      return this.handleApiError('getTicketMatches', error);
    }
  }
  /**
   * Gestion centralisée des erreurs API : log et rethrow pour propager une erreur à l'appelant.
   */
  private handleApiError(operation: string, error: unknown): never {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError;
      const status = axiosError.response?.status;
      const data = axiosError.response?.data;
      console.error(`❌ [${operation}] API error (status: ${status}):`, { message: axiosError.message, data });
      // Rethrow the original AxiosError so callers can inspect it if needed
      throw axiosError;
    } else {
      console.error(`❌ [${operation}] Unexpected error:`, error);
      // Normalize and throw a generic Error
      const message = (error instanceof Error) ? error.message : String(error);
      throw new Error(`[${operation}] ${message}`);
    }
  }
}