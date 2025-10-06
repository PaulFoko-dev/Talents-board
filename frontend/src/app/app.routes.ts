
import { Routes } from '@angular/router';
import { HomeComponent } from './View/home/home.component';
import { Contact } from './View/contact/contact';
import { Connexion } from './connexion/connexion';

export const routes: Routes = [
  // Route racine vers la page d'accueil
  { path: '', component: HomeComponent, pathMatch: 'full' },
  // Alias explicite vers la page d'accueil
  { path: 'home', component: HomeComponent },
  {path: 'contact', component: Contact},
  { path: 'connexion', component: Connexion}
];
