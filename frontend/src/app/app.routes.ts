import { Routes } from '@angular/router';
import { HomeComponent } from './View/home/home.component';
import { Contact } from './View/contact/contact';

export const routes: Routes = [
  // Route racine vers la page d'accueil
  { path: '', component: HomeComponent, pathMatch: 'full' },
  // Alias explicite vers la page d'accueil
  { path: 'home', component: HomeComponent },
  // Redirection de secours pour toute route inconnue
  // { path: '**', redirectTo: '' },
  {path: 'contact', component: Contact}
];
