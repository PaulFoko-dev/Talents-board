/* import { Injectable } from '@angular/core';
import axios from 'axios';
import { AxiosResponse } from 'axios';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private baseUrl = "http://10.180.171.237:8080";
  private readonly authToken = 'eyJhbGciOiJSUzI1NiIsImtpZCI6IjlkMjEzMGZlZjAyNTg3ZmQ4ODYxODg2OTgyMjczNGVmNzZhMTExNjUiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiZGF5Iiwicm9sZSI6IkNBTkRJREFUIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL3RhbGVudHMtYm9hcmQtYjZkMzYiLCJhdWQiOiJ0YWxlbnRzLWJvYXJkLWI2ZDM2IiwiYXV0aF90aW1lIjoxNzYwNTI3NzgwLCJ1c2VyX2lkIjoiMmdHeFA3VkJUZVB2MzF5S2ZwMnRGR09rV1E4MyIsInN1YiI6IjJnR3hQN1ZCVGVQdjMxeUtmcDJ0RkdPa1dRODMiLCJpYXQiOjE3NjA1Mjc3ODAsImV4cCI6MTc2MDUzMTM4MCwiZW1haWwiOiJtaWtlQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJtaWtlQGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6InBhc3N3b3JkIn19.R0lBiV88d94Mfdf_tkW_AnF4fA6ckU6ygB4pYevP0_kGJ3OkIUFpyeoiSZ3rUQlN3X-yaZYMiWQjwS-ryR6kYFORoU6aP-oL1Qi-765qcEo4ql-WrEN88cXuqOESdehIkrvt_iZEFevV8UHc4wpSeQahjoorRQHoZs7ZC1mAxp1N3d5c4mKypn3wuQR8fnG1QmO6k2VXjAoZzsibJrBnZ5L34n0R656EdHNezZ4m5B3UvN9KAtl5XJ5_tp02GRBwQ2gG2An2UXUoxAHA4otWsR248R6YWXN5KC1-dozNVBIRDiuiJMvmKLRGOF7t0PE2tBr7nCFkV5KwUN88865CJg'
  async getTicket(): Promise<any> {
    const response = await axios.get('${this.baseUrl}/api/tickets/list');
    return response.data;
  }

  async saveTicket(data: string, file: File): Promise<any> {
    const response: AxiosResponse = await axios.post('${this.baseUrl}/api/tickets/candidat', {data, file});
    return response.data;
  }

  // GET avec token Bearer dans l'en-tête
  async getTicketWithToken(token: string): Promise<any> {
    const response: AxiosResponse = await axios.get('${this.baseUrl}/api/tickets/list', {
      headers: {
        Authorization: 'Bearer ${this.authToken}'
      }
    });
    return response.data;
  }

  // POST (upload) avec token Bearer — on utilise FormData pour envoyer le fichier
/*   async saveTicketWithToken(token: string, ticket: string, file: File): Promise<any> {
    const formData = new FormData();
    formData.append('ticket', ticket);
    if (file) {
      formData.append('file', file, file.name);
    }

    const response: AxiosResponse = await axios.post(`${this.baseUrl}/api/tickets/candidat`, formData, {
      headers: {
       Authorization: 'Bearer ${this.authToken}'
      ,
        'Content-Type': 'multipart/form-data'
      }
    });

    return response.data;
  } 

} */
import { Injectable } from '@angular/core';
import axios, { AxiosError } from 'axios';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  // OPTIMISATION : L'URL de base est correcte.
  private readonly baseUrl = "http://10.180.171.237:8080";

  // CORRECTION MAJEURE : Vous devez remplacer ce token par un NOUVEAU et VALIDE !
  // Celui-ci a expiré le 15/10/2025 à 11:49.
  private readonly authToken = 'eyJhbGciOiJSUzI1NiIsImtpZCI6IjlkMjEzMGZlZjAyNTg3ZmQ4ODYxODg2OTgyMjczNGVmNzZhMTExNjUiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoicGF1bDIiLCJyb2xlIjoiQ0FORElEQVQiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vdGFsZW50cy1ib2FyZC1iNmQzNiIsImF1ZCI6InRhbGVudHMtYm9hcmQtYjZkMzYiLCJhdXRoX3RpbWUiOjE3NjA1Mzc4NDAsInVzZXJfaWQiOiIxQWk5UjBwVUdRWXJPQnRwYUkxSWYyVnlYeFQyIiwic3ViIjoiMUFpOVIwcFVHUVlyT0J0cGFJMUlmMlZ5WHhUMiIsImlhdCI6MTc2MDUzNzg0MCwiZXhwIjoxNzYwNTQxNDQwLCJlbWFpbCI6InBhdWwyQG1haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbInBhdWwyQG1haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.YGYNX-LG_gk0EOvDlDecfDo_XtMGOfHeNa3ItJrHjWz1skduQl8VfUZy5gvjlR708qnzojNE5KYtq7JCaS4xv3mz3_8LrCs9B_0mymuImeEH0hyPW2Cc9gCKEbraTzGFUX6SqdEVUI_bhYt_CCSBdNadQUg-k6Eky33FcDJQk32Npul0oGv6U9p_QJjG8Bquvdiq44M47A7ijcDBPnjnR4t5pRCdiKO3aom4eN9Xk3Co2UnZB2ZHvZpMLFb9uy3x9-gw1Oa4mt1MqDaDKa_AyyfA5Ge558vMfXR7JVzO3wGQVczF1zDoS9sbzhNa-yzZCrl9Kh-fWWM7qEorXeP4iQ'

  constructor() {}

  /**
   * OPTIMISATION : Méthode privée centralisée pour créer les en-têtes d'authentification.
   * Cela évite de répéter le même code dans chaque fonction.
   */
  private getAuthHeaders() {
    if (!this.authToken) {
      throw new Error("Le token d'authentification n'est pas défini dans le service.");
    }
    // CORRECTION : Utilisation des backticks `` pour que la variable soit interprétée.
    return {
      Authorization: `Bearer ${this.authToken}`
    };
  }

  /**
   * Récupère la liste des tickets de l'utilisateur authentifié.
   */
  async getTickets(): Promise<any> {
    const headers = this.getAuthHeaders();
    try {
      // CORRECTION : Utilisation des backticks `` pour construire l'URL.
      const response = await axios.get(`${this.baseUrl}/api/tickets/list`, { headers });
      console.log("✅ SUCCÈS [getTickets]:", response.data);
      return response.data;
    } catch (error) {
      // OPTIMISATION : Gestion d'erreur détaillée pour un meilleur débogage.
      this.handleApiError('getTickets', error);
    }
  }

  /**
   * Enregistre un nouveau ticket avec des données et un fichier.
   * @param data Les données du ticket au format string (JSON).
   * @param file Le fichier PDF à uploader.
   */
  async saveTicket(data: string, file: File): Promise<any> {
    // CORRECTION : Utilisation de FormData, indispensable pour l'envoi de fichiers.
    const formData = new FormData();
    // Les clés 'ticket' et 'file' doivent correspondre à ce que votre API attend.
    formData.append('ticket', data);
    formData.append('file', file, file.name);

    // OPTIMISATION : On fusionne les en-têtes d'authentification avec le Content-Type.
    const headers = {
      ...this.getAuthHeaders(),
      'Content-Type': 'multipart/form-data'
    };

    try {
      // CORRECTION : Utilisation des backticks `` pour construire l'URL.
      const response = await axios.post(`${this.baseUrl}/api/tickets/candidat`, formData, { headers });
      console.log("✅ SUCCÈS [saveTicket]:", response.data);
      return response.data;
    } catch (error) {
      this.handleApiError('saveTicket', error);
    }
  }

  /**
   * OPTIMISATION : Une fonction unique pour logger les erreurs de manière cohérente.
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
    // On relance l'erreur pour que le composant qui appelle puisse aussi la gérer.
    throw error;
  }
}