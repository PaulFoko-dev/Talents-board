import { Injectable } from '@angular/core';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  async login(email: string, motDePasse: string) {
    try {
      const reponse = await axios.post('http://localhost:9090/api/auth/login', {
        email,
        motDePasse
      });
      return reponse.data; 
    } catch (error: any) {
      throw error.response?.data || { message: 'Erreur réseau' };
    }
  }
}
