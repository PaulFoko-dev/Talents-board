import { Injectable } from '@angular/core';
import axios, { AxiosError } from 'axios';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  // private readonly baseUrl = "http://10.180.171.237:8080";
  private readonly baseUrl = "http://127.0.0.1:8080";

  constructor() { }

  /**
   * 🔹 Génère les en-têtes d'authentification à partir du localStorage
   */
  private getAuthHeaders() {
    // const token = localStorage.getItem("token");
    const token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjlkMjEzMGZlZjAyNTg3ZmQ4ODYxODg2OTgyMjczNGVmNzZhMTExNjUiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiZGF5Iiwicm9sZSI6IkNBTkRJREFUIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL3RhbGVudHMtYm9hcmQtYjZkMzYiLCJhdWQiOiJ0YWxlbnRzLWJvYXJkLWI2ZDM2IiwiYXV0aF90aW1lIjoxNzYwNjE1MjQyLCJ1c2VyX2lkIjoiMmdHeFA3VkJUZVB2MzF5S2ZwMnRGR09rV1E4MyIsInN1YiI6IjJnR3hQN1ZCVGVQdjMxeUtmcDJ0RkdPa1dRODMiLCJpYXQiOjE3NjA2MTUyNDIsImV4cCI6MTc2MDYxODg0MiwiZW1haWwiOiJtaWtlQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJtaWtlQGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6InBhc3N3b3JkIn19.X4aNd_YmOiZ29bbVkHBpBhpgJ6LvpPMGTDqpcJlh2zZYFgjf4vjBIx_kyKjuk4m5CM3sydYfOLRotMJs9yzyaNxIxVJqQhoBEY0_SazvsnTUluKBjwQv8nD8SWJCN_-deiMIcp1nT3F2CZctwofROmwuA3hZy6JPYRZ6vUDx8w0r0RwnVnGta_1_Zx6CnqOib4x69lUei5UkV2sz2Z2wx-qkcf5BfNjBFE8zljbI5dKLPE7Zs8EIRUe3XFe4KzjHbBxIGjL-lJytuW8GfpnruYJIx01GOzfKJQDX2eOhRQPMfrGnFiD9VFEtZTiRWNPjVM3DTmy9DUG0Cfr5oTRIgg";
    if (!token) {
      throw new Error("❌ Token d'authentification introuvable dans le localStorage.");
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
      const response = await axios.get(`${this.baseUrl}/api/tickets/list`, { headers });
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
  async createCandidateTicket(data: any, file?: File): Promise<any> {
    const formData = new FormData();
    formData.append('data', JSON.stringify(data)); // <-- data doit contenir les champs du DTO
    if (file) {
      formData.append('file', file, file.name);
    }

    const headers = {
      ...this.getAuthHeaders(),
      'Content-Type': 'multipart/form-data'
    };

    try {
      const response = await axios.post(`${this.baseUrl}/api/tickets/candidat`, formData, { headers });
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
    const userId = localStorage.getItem("userId");
    if (!userId) {
      throw new Error("❌ L'ID utilisateur est introuvable dans le localStorage.");
    }

    const headers = {
      ...this.getAuthHeaders(),
      'Content-Type': 'application/json'
    };

    try {
      const response = await axios.put(
        `${this.baseUrl}/api/tickets/${userId}/validate`,
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
