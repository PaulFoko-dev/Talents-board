import { Injectable } from '@angular/core';
import axios from 'axios';
import { BASE_URL } from "../../baseUrl"

@Injectable({
  providedIn: 'root'
})
export class AuthService {

    private getHeaders() {
    const token = localStorage.getItem('token');
    return {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
  }

  async login(email: string, motDePasse: string) {
    try {
      const reponse = await axios.post(BASE_URL + 'api/auth/login', {
        email,
        motDePasse
      });
      return reponse.data;
    } catch (error: any) {
      throw error.response?.data;
    }
  }

  async register_candidat(data: any) {
    try {
      const res = await axios.post(BASE_URL + 'api/auth/register/candidat', data);

      return res.data;
    } catch (error: any) {
      throw error.response?.data || { message: 'Erreur lors de l’inscription' };
    }
  }
  async register_entreprise(data: any) {
    try {
      const res = await axios.post(BASE_URL + 'api/auth/register/entreprise', data);

      return res.data;
    } catch (error: any) {
      throw error.response?.data || { message: 'Erreur lors de l’inscription' };
    }
  }

  async getUserProfile(userId: string) {
    try {
      const response = await axios.get(BASE_URL + `api/users/${userId}`, {
        headers: this.getHeaders() // AJOUT: Headers avec token
      });
      return response.data;
    } catch (error: any) {
      throw error.response?.data || { message: 'Erreur lors de la récupération du profil' };
    }
  }

  async getUserFullName(userId: string): Promise<string> {
    try {
      const userData = await this.getUserProfile(userId);

      if (userData.status === 200 && userData.data) {
        const user = userData.data;

        // Si c'est une entreprise, retourner seulement le nom
        if (user.type === 'ENTREPRISE') {
          return user.nom || 'Entreprise';
        }

        // Si c'est un candidat, retourner nom + prénom
        if (user.prenom) {
          return `${user.nom} ${user.prenom}`.trim();
        }

        return user.nom || 'Utilisateur';
      }

      throw new Error('Données utilisateur non disponibles');

    } catch (error: any) {
      console.error('Erreur lors de la récupération du nom:', error);
      throw error;
    }
  }
}
