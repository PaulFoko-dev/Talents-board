import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/tickets', pathMatch: 'full' },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/pages/login/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () => import('./features/auth/pages/register/register.component').then(m => m.RegisterComponent)
      }
    ]
  },
  {
    path: 'tickets',
    loadComponent: () => import('./features/tickets/pages/tickets-list/tickets-list.component').then(m => m.TicketsListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'tickets/create',
    loadComponent: () => import('./features/tickets/pages/ticket-create/ticket-create.component').then(m => m.TicketCreateComponent),
    canActivate: [authGuard]
  },
  {
    path: 'candidates',
    loadComponent: () => import('./features/candidates/pages/candidates-list/candidates-list.component').then(m => m.CandidatesListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'enterprises',
    loadComponent: () => import('./features/enterprises/pages/enterprises-list/enterprises-list.component').then(m => m.EnterprisesListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'notifications',
    loadComponent: () => import('./features/notifications/pages/notifications-list/notifications-list.component').then(m => m.NotificationsListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/profile/pages/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '/tickets' }
];
