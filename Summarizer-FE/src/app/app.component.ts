import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { InputPanelComponent } from './components/input-panel/input-panel.component';
import { OutputPanelComponent } from './components/output-panel/output-panel.component';
import { SummarizerService } from './services/summarizer.service';
import { SummarizationRequest, SummaryParameters } from './models/summarization.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, 
    CommonModule,
    InputPanelComponent,
    OutputPanelComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Summarizer - Riassumi i tuoi testi';
  
  // Output data
  summary: string = '';
  originalLength: number = 0;
  summaryLength: number = 0;
  
  // Stati
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(private summarizerService: SummarizerService) {}
  
  onSummarizeRequested(event: { text: string, params: SummaryParameters }): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.summary = '';
    
    const request: SummarizationRequest = {
      text: event.text,
      minLength: event.params.minLength,
      maxLength: event.params.maxLength
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
  
  onResetRequested(): void {
    this.summary = '';
    this.originalLength = 0;
    this.summaryLength = 0;
    this.errorMessage = '';
  }
  
  onDownloadTxt(): void {
    if (!this.summary) return;
    
    const blob = new Blob([this.summary], { type: 'text/plain' });
    this.summarizerService.downloadBlob(blob, 'riassunto.txt');
  }
  
  onDownloadDocx(): void {
    if (!this.summary) return;
    
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
  
  onDownloadPdf(): void {
    if (!this.summary) return;
    
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
