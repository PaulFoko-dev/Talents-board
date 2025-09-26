import { RouterModule, Routes } from '@angular/router';
import { Candidats } from './candidats/candidats';
import { Entreprises } from './entreprises/entreprises';
import { Connexion } from './connexion/connexion';
import { Inscription } from './inscription/inscription';
import { App } from './app';

export const routes: Routes = [
    { path: ' ', component: App},
    { path: 'candidats', component: Candidats},
    { path: 'entreprises', component: Entreprises},
    { path: 'connexion', component: Connexion},
    { path: 'inscription', component: Inscription}

];

export class app {}
