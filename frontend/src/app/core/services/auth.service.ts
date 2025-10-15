import { Injectable } from '@angular/core';
import axios from 'axios';
import { BASE_URL } from "../../baseUrl"

@Injectable({
  providedIn: 'root'
})
export class AuthService {

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

  async register_candidat(data: any ) {
    try {
      const res = await axios.post(BASE_URL +'api/auth/register/candidat', data);

      return res.data;
    } catch (error: any) {
      throw error.response?.data || { message: 'Erreur lors de l’inscription' };
    }
  }
  async register_entreprise(data: any) {
    try {
      const res = await axios.post(BASE_URL +'api/auth/register/entreprise', data);

      return res.data;
    } catch (error: any) {
      throw error.response?.data || { message: 'Erreur lors de l’inscription' };
    }
  }
}
