import { Directive, EventEmitter, HostListener, Output } from '@angular/core';

/**
 * DragDropDirective
 * - Ajoute un comportement de glisser-déposer pour uploader des fichiers.
 * Exemple: upload de CV en PDF.
 */
@Directive({ selector: '[appDragDrop]' })
export class DragDropDirective {
  @Output() fileDropped = new EventEmitter<File>();

  @HostListener('dragover', ['$event'])
  onDragOver(event: DragEvent) {
    event.preventDefault();
  }

  @HostListener('drop', ['$event'])
  onDrop(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer?.files.length) {
      this.fileDropped.emit(event.dataTransfer.files[0]);
    }
  }
}

/**
 * Exemple d'utilisation
 * <div appDragDrop (fileDropped)="onFileSelected($event)">
 *  Glissez votre CV ici
 * </div>
 */