import { Injectable } from '@angular/core';
import axios, { AxiosResponse } from 'axios';
import { BASE_URL } from "../../app/baseUrl"

export interface CompanyProfile {
  id: string;
  nom: string;
  logo: string;
  description: string;
  secteur?: string;
  localisation?: string;
  email: string;
  numero: string;
  siteWeb: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfilEntrepriseService {
  private axiosInstance;

  constructor() {
    // Configuration Axios de base
    this.axiosInstance = axios.create({
      baseURL: BASE_URL,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    // Intercepteur pour ajouter le token automatiquement
    this.axiosInstance.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Intercepteur pour gérer les erreurs globalement
    this.axiosInstance.interceptors.response.use(
      (response) => response,
      (error) => {
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
      }
    );
  }

  private getHeaders() {
    const token = localStorage.getItem('token');
    return {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
  }

  async getCompanyProfile(): Promise<CompanyProfile> {
    const userId = localStorage.getItem('userId');
    
    if (!userId) {
      throw new Error('User ID not found');
    }

    try {
      const response: AxiosResponse<any> = await this.axiosInstance.get(`${BASE_URL}api/users/${userId}`);
      return response.data.data;
    } catch (error) {
      console.error('Error fetching company profile:', error);
      throw error;
    }
  }

   async updateCompanyProfile(profileData: Partial<CompanyProfile>): Promise<CompanyProfile> {
    console.log("test");
    
    const userId = localStorage.getItem('userId');
    
    if (!userId) {
      throw new Error('User ID not found');
    }

    try {
      const response: AxiosResponse<CompanyProfile> = await this.axiosInstance.put(
        `${BASE_URL}api/users/${userId}`, 
        profileData
      );
      
      return response.data;
    } catch (error) {
      console.error('Error updating company profile:', error);
      throw error;
    }
  } 

  async createCompanyProfile(profileData: Omit<CompanyProfile, 'id'>): Promise<CompanyProfile> {
    const userId = localStorage.getItem('userId');
    
    if (!userId) {
      throw new Error('User ID not found');
    }

    try {
      const response: AxiosResponse<CompanyProfile> = await this.axiosInstance.post(
        `${BASE_URL}api/users/${userId}`, 
        profileData
      );
      return response.data;
    } catch (error) {
      console.error('Error creating company profile:', error);
      throw error;
    }
  }

  async checkProfileExists(): Promise<boolean> {
    const userId = localStorage.getItem('userId');
    
    if (!userId) {
      throw new Error('User ID not found');
    }

    try {
      const response: AxiosResponse<boolean> = await this.axiosInstance.get(`${BASE_URL}api/users/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error checking profile existence:', error);
      throw error;
    }
  }

  // Méthode utilitaire pour les appels avec gestion d'erreur améliorée
  private async makeRequest<T>(method: 'get' | 'post' | 'put' | 'delete', url: string, data?: any): Promise<T> {
    try {
      const response: AxiosResponse<T> = await this.axiosInstance({
        method,
        url,
        data
      });
      return response.data;
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || 'Une erreur est survenue';
      throw new Error(errorMessage);
    }
  }

  // Version alternative utilisant makeRequest
  async getCompanyProfileAlt(): Promise<CompanyProfile> {
    const userId = localStorage.getItem('userId');
    if (!userId) throw new Error('User ID not found');
    
    return this.makeRequest<CompanyProfile>('get', `${BASE_URL}api/users/${userId}`);
  }
}