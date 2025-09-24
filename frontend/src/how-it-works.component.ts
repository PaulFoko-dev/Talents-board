import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface StepItem {
  num: number;
  icon: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-how-it-works',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './how-it-works.component.html',
})
export class HowItWorksComponent {
  steps: StepItem[] = [
    {
      num: 1,
      icon: '👤',
      title: 'Créez un compte',
      description:
        "Inscrivez-vous en tant que candidat ou entreprise en quelques minutes.",
    },
    {
      num: 2,
      icon: '📄',
      title: 'Déposez CV/besoin',
      description:
        'Partagez votre CV ou publiez vos besoins en talents facilement.',
    },
    {
      num: 3,
      icon: '💙',
      title: 'Trouvez votre match',
      description:
        'Découvrez des opportunités ou des talents parfaitement adaptés.',
    },
  ];
}
