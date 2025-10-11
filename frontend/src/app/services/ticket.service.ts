import { Injectable } from '@angular/core';
import axios from 'axios';
import { AxiosResponse } from 'axios';
@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private baseUrl = "http://localhost:8080";
  async getTicket(): Promise<any> {
    const response = await axios.get(`${this.baseUrl}/api/tickets/list`);
    return response.data;
  }

  async saveTicket(ticket: string, file: File): Promise<any> {
    const response: AxiosResponse = await axios.post(`${this.baseUrl}/api/tickets/candidat`, {ticket, file});
    return response.data;
  }

}
