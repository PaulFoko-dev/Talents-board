import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../../core/services/api.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-notifications-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="fw-bold mb-1">Notifications</h2>
          <p class="text-muted mb-0">{{ unreadCount }} non lue(s)</p>
        </div>
        <button class="btn btn-outline-secondary" (click)="markAllRead()" *ngIf="unreadCount > 0">
          <i class="bi bi-check2-all me-2"></i>Tout marquer comme lu
        </button>
      </div>

      <div *ngIf="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div *ngIf="!loading">
        <div *ngFor="let notif of notifications" class="card border-0 shadow-sm mb-3" [class.bg-light]="notif.isRead">
          <div class="card-body d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="badge rounded-pill" [class.bg-primary]="!notif.isRead" [class.bg-secondary]="notif.isRead">
                  <i class="bi bi-bell-fill"></i>
                </span>
              </div>
              <div>
                <p class="mb-0" [class.fw-semibold]="!notif.isRead">{{ notif.message }}</p>
                <small class="text-muted">{{ notif.createdAt }}</small>
              </div>
            </div>
            <button class="btn btn-sm btn-outline-primary" *ngIf="!notif.isRead" (click)="markRead(notif)">
              Marquer lu
            </button>
          </div>
        </div>
        <div *ngIf="notifications.length === 0" class="text-center py-5">
          <i class="bi bi-bell-slash text-muted" style="font-size: 3rem;"></i>
          <p class="text-muted mt-3">Aucune notification</p>
        </div>
      </div>
    </div>
  `
})
export class NotificationsListComponent implements OnInit {
  notifications: any[] = [];
  loading = true;
  currentUser: any = null;

  constructor(private apiService: ApiService, private authService: AuthService) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser?.id) {
      this.apiService.getNotifications(this.currentUser.id).subscribe({
        next: (data) => { this.notifications = data; this.loading = false; },
        error: () => { this.loading = false; }
      });
    } else {
      this.loading = false;
    }
  }

  get unreadCount() {
    return this.notifications.filter(n => !n.isRead).length;
  }

  markRead(notif: any) {
    this.apiService.markNotificationRead(notif.id).subscribe({
      next: (updated) => {
        const idx = this.notifications.findIndex(n => n.id === notif.id);
        if (idx !== -1) this.notifications[idx] = updated;
      }
    });
  }

  markAllRead() {
    this.apiService.markAllRead(this.currentUser.id).subscribe({
      next: () => this.notifications.forEach(n => n.isRead = true)
    });
  }
}
