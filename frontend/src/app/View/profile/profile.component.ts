import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as pdfjsLib from 'pdfjs-dist';
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';

interface User {
  name: string;
  job: string;
  location: string;
  email: string;
  phone: string;
  skills: string[];
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FooterComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {

  user: User = {
    name: 'Jean Dupont',
    job: 'Développeur Web Fullstack',
    location: 'Paris, France',
    email: 'jean.dupont@example.com',
    phone: '+33 6 12 34 56 78',
    skills: ['React', 'Node.js', 'TypeScript', 'SQL', 'Cloud Azure', 'DevOps']
  };

  cvUrl: string = ''; // URL local ou base64 après sélection

  constructor() {
    pdfjsLib.GlobalWorkerOptions.workerSrc = 'assets/pdf.worker.js'; // worker local
  }

  async onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    const fileReader = new FileReader();
    fileReader.onload = async (e: any) => {
      const typedArray = new Uint8Array(e.target.result);
      this.cvUrl = URL.createObjectURL(file); // pour affichage si besoin
      await this.parsePdf(typedArray);
    };
    fileReader.readAsArrayBuffer(file);
  }

  async parsePdf(data: Uint8Array) {
    try {
      const loadingTask = pdfjsLib.getDocument({ data });
      const pdf = await loadingTask.promise;
      let text = '';

      for (let i = 1; i <= pdf.numPages; i++) {
        const page = await pdf.getPage(i);
        const content = await page.getTextContent();
        text += content.items.map(item => (item as any).str).join(' ') + ' ';
      }

      // Extraction basique avec regex
      const emailRegex = /[\w.-]+@[\w.-]+\.\w+/;
      const phoneRegex = /\+?\d[\d\s.-]{6,}/;

      this.user.email = text.match(emailRegex)?.[0] || this.user.email;
      this.user.phone = text.match(phoneRegex)?.[0] || this.user.phone;

      // Exemple simple pour extraire le nom (ici le premier mot suivi du second)
      const nameMatch = text.match(/([A-Z][a-z]+ [A-Z][a-z]+)/);
      this.user.name = nameMatch?.[0] || this.user.name;

      // Exemple rudimentaire pour skills
      const skillsList = ['React', 'Angular', 'Node.js', 'TypeScript', 'SQL', 'DevOps', 'Python', 'Java'];
      this.user.skills = skillsList.filter(skill => text.includes(skill)) || this.user.skills;

    } catch (error) {
      console.error('Erreur lors du parsing PDF:', error);
    }
  }
}
