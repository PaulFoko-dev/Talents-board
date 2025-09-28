
import { Routes } from '@angular/router';
import { HomeComponent } from './View/home/home.component';
import { Contact } from './View/contact/contact';

export const routes: Routes = [
  // Route racine vers la page d'accueil
  { path: '', component: HomeComponent, pathMatch: 'full' },
  // Alias explicite vers la page d'accueil
  { path: 'home', component: HomeComponent },
  {path: 'contact', component: Contact}
  { path: 'candidats', component: Candidats},
    { path: 'entreprises', component: Entreprises},
    { path: 'connexion', component: Connexion},
    { path: 'inscription', component: Inscription}
];
