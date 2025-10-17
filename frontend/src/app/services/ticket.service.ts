import { Injectable } from '@angular/core';
import axios, { AxiosError } from 'axios';
import { BASE_URL } from "../../app/baseUrl"

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  // private readonly baseUrl = "http://10.180.171.237:8080";
  // private readonly baseUrl = "http://localhost:8080";

  constructor() { }

  /**
   * 🔹 Génère les en-têtes d'authentification à partir du localStorage
   */
  private getAuthHeaders() {
    const token = localStorage.getItem("token");
    if (!token) {
      console.log("❌ Token d'authentification introuvable dans le localStorage.");
    }

    return {
      Authorization: `Bearer ${token}`
    };
  }

  /**
   * 🔹 Récupère la liste des tickets du candidat connecté
   */
  async getTickets(): Promise<any> {
    const headers = this.getAuthHeaders();

    try {
      const response = await axios.get(`${BASE_URL}api/tickets/list`, { headers });
      console.log("✅ SUCCÈS [getTickets]:", response.data);
      return response.data;
    } catch (error) {
      this.handleApiError('getTickets', error);
    }
  }

  /**
   * 🔹 Crée un ticket candidat avec les données du formulaire et un fichier optionnel
   * @param data Objet contenant les infos du formulaire
   * @param file (optionnel) Fichier joint (CV, PDF, etc.)
   */
  // ticket.service.ts

  async createCandidateTicket(data: any, file?: File): Promise<any> {
    const formData = new FormData();
    formData.append('data', JSON.stringify(data));
    if (file) {
      formData.append('file', file, file.name);
    }

    // ✅ CORRECTION ICI : L'en-tête Content-Type est supprimé
    const headers = {
      ...this.getAuthHeaders(),
      // PAS DE 'Content-Type' ICI ! Axios le gère.
    };

    try {
      const response = await axios.post(`${BASE_URL}api/tickets/candidat`, formData, { headers, 
        timeout: 80000 });
      return response.data;
    } catch (error) {
      this.handleApiError('createCandidateTicket', error);
    }
  }


  /**
   * 🔹 Valide un ticket après confirmation du candidat (PUT)
   * L'ID du candidat est pris dans le localStorage
   */
  async validateCandidateTicket(ticketData: any): Promise<any> {
    const headers = {
      ...this.getAuthHeaders(),
      'Content-Type': 'application/json'
    };

    try {
      const response = await axios.put(
        `${BASE_URL}api/tickets/${ticketData.id}/validate`,
        ticketData,
        { headers }
      );
      console.log("✅ SUCCÈS [validateCandidateTicket]:", response.data);
      return response.data;
    } catch (error) {
      this.handleApiError('validateCandidateTicket', error);
    }
  }

  /**
   * 🔹 Gestion d’erreurs centralisée
   */
  private handleApiError(methodName: string, error: any) {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError;
      console.error(`❌ ERREUR [${methodName}]:`, {
        message: axiosError.message,
        status: axiosError.response?.status,
        data: axiosError.response?.data
      });
    } else {
      console.error(`❌ ERREUR INCONNUE [${methodName}]:`, error);
    }
    throw error;
  }
}
