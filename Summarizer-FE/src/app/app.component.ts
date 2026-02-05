import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SummarizerService } from './services/summarizer.service';
import { SummarizationRequest, SummaryLengthPreset } from './models/summarization.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, 
    CommonModule,
    FormsModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Summarizer';
  
  // Input data
  inputText: string = '';
  selectedFileName: string = '';
  
  // Parametri lunghezza
  lengthPresets = [
    { label: 'Paragrafo', value: SummaryLengthPreset.SHORT },
    { label: 'Punti Elenco', value: SummaryLengthPreset.MEDIUM },
    { label: 'Personalizzato', value: SummaryLengthPreset.CUSTOM }
  ];
  selectedPreset: SummaryLengthPreset = SummaryLengthPreset.SHORT;
  minLength: number = 30;
  maxLength: number = 80;
  
  // Output data
  summary: string = '';
  originalLength: number = 0;
  summaryLength: number = 0;
  
  // Stati
  isLoading: boolean = false;
  errorMessage: string = '';
  showDownloadMenu: boolean = false;

  constructor(private summarizerService: SummarizerService) {}
  
  selectPreset(preset: SummaryLengthPreset): void {
    this.selectedPreset = preset;
    switch (preset) {
      case SummaryLengthPreset.SHORT:
        this.minLength = 30;
        this.maxLength = 80;
        break;
      case SummaryLengthPreset.MEDIUM:
        this.minLength = 50;
        this.maxLength = 150;
        break;
      case SummaryLengthPreset.CUSTOM:
        break;
    }
  }

  onFileSelect(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    this.selectedFileName = file.name;
    const reader = new FileReader();
    
    reader.onload = (e: any) => {
      this.inputText = e.target.result;
    };
    
    reader.readAsText(file);
  }

  summarize(): void {
    if (!this.inputText) return;
    
    // Validazione lato client
    if (this.inputText.trim().length < 100) {
      this.errorMessage = 'Il testo è troppo corto per essere riassunto (minimo 100 caratteri)';
      return;
    }
    
    if (this.minLength < 10) {
      this.errorMessage = 'La lunghezza minima deve essere almeno 10 parole';
      return;
    }
    
    if (this.maxLength > 500) {
      this.errorMessage = 'La lunghezza massima non può superare 500 parole';
      return;
    }
    
    if (this.maxLength <= this.minLength) {
      this.errorMessage = 'La lunghezza massima deve essere maggiore della lunghezza minima';
      return;
    }
    
    this.isLoading = true;
    this.errorMessage = '';
    this.summary = '';
    
    const request: SummarizationRequest = {
      text: this.inputText,
      minLength: this.minLength,
      maxLength: this.maxLength
    };
    
    this.summarizerService.summarize(request).subscribe({
      next: (response) => {
        this.summary = response.summary;
        this.originalLength = response.originalLength;
        this.summaryLength = response.summaryLength;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.error || 'Errore durante la generazione del riassunto';
        console.error('Errore:', error);
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  get reductionPercentage(): number {
    if (this.originalLength === 0) return 0;
    return Math.round(((this.originalLength - this.summaryLength) / this.originalLength) * 100);
  }

  copySummary(): void {
    if (!this.summary) return;
    navigator.clipboard.writeText(this.summary).then(() => {
      console.log('Riassunto copiato negli appunti');
    });
  }

  toggleDownloadMenu(): void {
    this.showDownloadMenu = !this.showDownloadMenu;
  }
  
  downloadTxt(): void {
    if (!this.summary) return;
    this.showDownloadMenu = false;
    const blob = new Blob([this.summary], { type: 'text/plain' });
    this.summarizerService.downloadBlob(blob, 'riassunto.txt');
  }
  
  downloadDocx(): void {
    if (!this.summary) return;
    this.showDownloadMenu = false;
    this.summarizerService.downloadDocx(this.summary).subscribe({
      next: (blob) => {
        this.summarizerService.downloadBlob(blob, 'riassunto.docx');
      },
      error: (error) => {
        this.errorMessage = 'Errore nel download DOCX';
        console.error('Errore:', error);
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }
  
  downloadPdf(): void {
    if (!this.summary) return;
    this.showDownloadMenu = false;
    this.summarizerService.downloadPdf(this.summary).subscribe({
      next: (blob) => {
        this.summarizerService.downloadBlob(blob, 'riassunto.pdf');
      },
      error: (error) => {
        this.errorMessage = 'Errore nel download PDF';
        console.error('Errore:', error);
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }
}
