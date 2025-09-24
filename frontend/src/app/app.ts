import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Footer } from "./components/footer/footer";
import { Temoignages } from "./components/temoignages/temoignages";
import { FeaturesGridComponent } from '../app-features-grid.component';
import { HowItWorksComponent } from '../how-it-works.component';
import { HeaderComponent } from './components/header/header.component';
import { HeroComponent } from './components/hero/hero.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Temoignages, Footer, FeaturesGridComponent, HowItWorksComponent, HeaderComponent, HeroComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})

export class App {
  protected readonly title = signal('frontend-app');
}