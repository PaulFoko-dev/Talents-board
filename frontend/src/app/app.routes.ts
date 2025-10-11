
import { Routes } from '@angular/router';
import { HomeComponent } from './View/home/home.component';
import { ProfileComponent } from './View/profile/profile.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { Connexion } from './connexion/connexion';
import { OpportuniteComponent } from './View/opportunite/opportunite.component';
import { LayoutComponent } from './layout/layout.component';
import { Inscription } from './inscription/inscription';
import { TicketCreation } from './View/ticketCandidat/ticketCreation/ticketCreation';
import { TicketCandidat } from './View/ticketCandidat/ticketCandidat';import { TicketCandidat } from './View/ticketCandidat/ticketCandidat';
import { TicketCreation } from './View/ticketCandidat/ticketCreation/ticketCreation';

export const routes: Routes = [
  // Route racine vers la page d'accueil
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path : 'profil', component: ProfileComponent},
  // Alias explicite vers la page d'accueil
  { path: 'home', component: HomeComponent },
  { path: 'inscription', component: Inscription },
  { path: 'connexion', component: Connexion },
  { path: 'header', component: HeaderComponent },
  { path: 'footer', component: FooterComponent },
  { path: 'ticketCreation', component: TicketCreation},
  { path: 'ticketCandidat', component: TicketCandidat }
];
