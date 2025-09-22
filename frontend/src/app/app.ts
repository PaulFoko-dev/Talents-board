import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { HeroComponent } from './components/hero/hero.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, HeroComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App {
  protected readonly title = signal('frontend-app');
}
