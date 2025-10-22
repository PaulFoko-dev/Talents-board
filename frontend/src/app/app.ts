// app.component.ts
import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { SidebarNavComponent } from './components/sidebar-nav/sidebar-nav';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, SidebarNavComponent],
  templateUrl: './app.html',
})
export class App {
  currentRoute = '';
  
  constructor(private router: Router) {
    
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.urlAfterRedirects;
      });
  }
  
  isHomePage(): boolean {
    return this.currentRoute === '/' || this.currentRoute === '/home' || 
           this.currentRoute === '/inscription' || this.currentRoute === '/connexion';
  }
}