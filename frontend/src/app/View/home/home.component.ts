import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../components/header/header.component';
import { HeroComponent } from '../../components/hero/hero.component';
import { HowItWorksComponent } from '../../components/how-it-works/how-it-works.component';
import { Temoignages } from '../../components/temoignages/temoignages';
import { Footer } from '../../components/footer/footer';
import { FeaturesGridComponent } from '../../components/featuresgrid/featuresgrid';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule, HeaderComponent, HeroComponent, FeaturesGridComponent, HowItWorksComponent, Temoignages, Footer],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {}
