import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-temoignages',
  imports: [],
  templateUrl: './temoignages.html',
  styleUrl: './temoignages.scss'
})


export class Temoignages {

  @Input() avis : string = "Grâce à Talents-board, j'ai trouvé mon emploi de rêve en un temps record ! L'interface est intuitive et le matching est d'une efficacité redoutable.";
  @Input() auteur : string = "Sophie Dupont";
  @Input() position : string = "Développeuse Full-stack";
  @Input() photo : string = "assets/image/Sophie.jpg"

}