import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Users
  getUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/users`);
  }
  getUserById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/users/${id}`);
  }
  getUserByFirebaseUid(uid: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/users/firebase/${uid}`);
  }
  getCandidates(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/users/candidates`);
  }
  getEnterprises(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/users/enterprises`);
  }
  createUser(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/users`, data);
  }
  updateUser(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/users/${id}`, data);
  }

  // Tickets
  getTickets(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/tickets`);
  }
  getTicketById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/tickets/${id}`);
  }
  getTicketsByOwner(ownerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/tickets/owner/${ownerId}`);
  }
  getTicketsByType(type: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/tickets/type/${type}`);
  }
  createTicket(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/tickets`, data);
  }
  updateTicket(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/tickets/${id}`, data);
  }
  closeTicket(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/tickets/${id}/close`, {});
  }
  deleteTicket(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/tickets/${id}`);
  }

  // Applications
  getApplicationsByCandidate(candidateId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/applications/candidate/${candidateId}`);
  }
  getApplicationsByTicket(ticketId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/applications/ticket/${ticketId}`);
  }
  createApplication(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/applications`, data);
  }
  updateApplicationStatus(id: number, status: string): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/applications/${id}/status?status=${status}`, {});
  }

  // Notifications
  getNotifications(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/notifications/user/${userId}`);
  }
  getUnreadNotifications(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/notifications/user/${userId}/unread`);
  }
  getUnreadCount(userId: number): Observable<{count: number}> {
    return this.http.get<{count: number}>(`${this.baseUrl}/notifications/user/${userId}/count`);
  }
  markNotificationRead(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/notifications/${id}/read`, {});
  }
  markAllRead(userId: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/notifications/user/${userId}/read-all`, {});
  }

  // Auth
  registerUser(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/auth/register`, data);
  }
  getProfile(uid: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/auth/profile/${uid}`);
  }
}
