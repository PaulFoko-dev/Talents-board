import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FeaturesGridComponent } from './components/featuresgrid/featuresgrid';
import { HowItWorksComponent } from './components/how-it-works/how-it-works.component';
import { FooterComponent } from "./components/footer/footer.component";
import { Temoignages } from "./components/temoignages/temoignages";
import { HeaderComponent } from './components/header/header.component';
import { HeroComponent } from './components/hero/hero.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Temoignages, FooterComponent, FeaturesGridComponent, HowItWorksComponent, HeaderComponent, HeroComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})

export class App {
  protected readonly title = signal('frontend-app');
}