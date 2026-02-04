import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SummaryLengthPreset, SummaryParameters } from '../../models/summarization.model';

@Component({
  selector: 'app-input-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './input-panel.component.html',
  styleUrl: './input-panel.component.css'
})
export class InputPanelComponent {
  @Output() summarizeRequested = new EventEmitter<{ text: string, params: SummaryParameters }>();
  @Output() resetRequested = new EventEmitter<void>();

  inputText: string = '';
  summaryLength: SummaryLengthPreset = SummaryLengthPreset.MEDIUM;
  minLength: number = 50;
  maxLength: number = 150;
  
  isDragging: boolean = false;
  selectedFile: File | null = null;
  fileName: string = '';
  
  successMessage: string = '';
  errorMessage: string = '';

  onLengthPresetChange(): void {
    switch(this.summaryLength) {
      case SummaryLengthPreset.SHORT:
        this.minLength = 30;
        this.maxLength = 80;
        break;
      case SummaryLengthPreset.MEDIUM:
        this.minLength = 50;
        this.maxLength = 150;
        break;
      case SummaryLengthPreset.LONG:
        this.minLength = 100;
        this.maxLength = 250;
        break;
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.processFile(file);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
    
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.processFile(files[0]);
    }
  }

  processFile(file: File): void {
    this.selectedFile = file;
    this.fileName = file.name;
    
    const reader = new FileReader();
    
    reader.onload = (e) => {
      const content = e.target?.result as string;
      this.inputText = content;
      this.showSuccess(`File "${file.name}" caricato con successo!`);
    };
    
    reader.onerror = () => {
      this.showError('Errore nella lettura del file');
    };
    
    if (file.type === 'application/json' || file.type === 'text/plain' || 
        file.name.endsWith('.txt') || file.name.endsWith('.json')) {
      reader.readAsText(file);
    } else {
      this.showError('Formato file non supportato. Usa TXT o JSON.');
    }
  }

  clearFile(): void {
    this.selectedFile = null;
    this.fileName = '';
    this.inputText = '';
  }

  onSummarize(): void {
    if (!this.inputText.trim()) {
      this.showError('Inserisci del testo da riassumere');
      return;
    }

    const params: SummaryParameters = {
      preset: this.summaryLength,
      minLength: this.minLength,
      maxLength: this.maxLength
    };

    this.summarizeRequested.emit({ text: this.inputText, params });
  }

  onReset(): void {
    this.inputText = '';
    this.selectedFile = null;
    this.fileName = '';
    this.errorMessage = '';
    this.successMessage = '';
    this.resetRequested.emit();
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => this.successMessage = '', 3000);
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => this.errorMessage = '', 3000);
  }

  get isValid(): boolean {
    return this.inputText.trim().length > 0;
  }
}
