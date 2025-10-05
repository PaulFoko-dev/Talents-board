import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../components/header/header.component';
import { HeroComponent } from '../../components/hero/hero.component';
import { HowItWorksComponent } from '../../components/how-it-works/how-it-works.component';
import { Temoignages } from '../../components/temoignages/temoignages';
import { FooterComponent } from '../../components/footer/footer.component';
import { FeaturesGridComponent } from '../../components/featuresgrid/featuresgrid';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule, HeaderComponent, HeroComponent, FeaturesGridComponent, HowItWorksComponent, Temoignages, FooterComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {}
