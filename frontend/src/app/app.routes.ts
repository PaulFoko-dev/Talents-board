import { Routes } from '@angular/router';
import { Candidats } from './candidats/candidats';
import { App } from './app';

export const routes: Routes = [
    { path: '', component: App},
    { path: 'candidats', component: Candidats}
];
