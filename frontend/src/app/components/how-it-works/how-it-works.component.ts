import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-how-it-works',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './how-it-works.component.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  styleUrl: './how-it-works.component.scss'
})
export class HowItWorksComponent {
  steps = [
    { number: 1, icon: 'person_add', title: 'Créez un compte', description: 'Inscrivez-vous en tant que candidat ou entreprise en quelques minutes.' },
    { number: 2, icon: 'upload_file', title: 'Déposez CV/besoin', description: 'Partagez votre CV ou publiez vos besoins en talents facilement.' },
    { number: 3, icon: 'favorite', title: 'Trouvez votre match', description: 'Découvrez des opportunités ou des talents parfaitement adaptés.' }
  ];
}