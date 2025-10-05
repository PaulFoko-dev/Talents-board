
import { Routes } from '@angular/router';
import { HomeComponent } from './View/home/home.component';
import { ProfileComponent } from './View/profile/profile.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';

export const routes: Routes = [
  // Route racine vers la page d'accueil
  { path: '', component: ProfileComponent, pathMatch: 'full' },
  // Alias explicite vers la page d'accueil
  { path: 'home', component: HomeComponent },
    { path: 'profile', component: ProfileComponent }
];
