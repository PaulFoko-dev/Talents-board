import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FeaturesGridComponent } from '../app-features-grid.component';
import { HowItWorksComponent } from '../how-it-works.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FeaturesGridComponent, HowItWorksComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend-app');
}
