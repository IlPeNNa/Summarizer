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
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Summarizer';
  
  // Input data
  inputText: string = '';
  selectedFileName: string = '';
  
  // Parametri lunghezza
  lengthPresets = [
    { label: 'Standard', value: SummaryLengthPreset.SHORT, description: '30-80 parole' },
    { label: 'Punti Elenco', value: SummaryLengthPreset.MEDIUM, description: '50-150 parole' },
    { label: 'Personalizzato', value: SummaryLengthPreset.CUSTOM, description: 'Personalizzabile' }
  ];
  selectedPreset: SummaryLengthPreset = SummaryLengthPreset.SHORT;
  minLength: number = 30;
  maxLength: number = 80;
  format: string = 'paragraph';
  
  // Output data
  summary: string = '';
  originalLength: number = 0;
  summaryLength: number = 0;
  
  // Stati
  isLoading: boolean = false;
  errorMessage: string = '';
  showDownloadMenu: boolean = false;
  isDragOver: boolean = false;
  
  // Auth modal
  showAuthModal: boolean = false;
  showRegisterForm: boolean = false;
  showPassword: boolean = false;
  loginEmail: string = '';
  loginPassword: string = '';
  registerEmail: string = '';
  registerPassword: string = '';
  registerConfirmPassword: string = '';

  constructor(private summarizerService: SummarizerService) {}
  
  selectPreset(preset: SummaryLengthPreset): void {
    this.selectedPreset = preset;
    switch (preset) {
      case SummaryLengthPreset.SHORT:
        this.minLength = 30;
        this.maxLength = 80;
        this.format = 'paragraph';
        break;
      case SummaryLengthPreset.MEDIUM:
        this.minLength = 50;
        this.maxLength = 150;
        this.format = 'bullet';
        break;
      case SummaryLengthPreset.CUSTOM:
        this.format = 'paragraph';
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

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      const file = files[0];
      this.selectedFileName = file.name;
      const reader = new FileReader();
      
      reader.onload = (e: any) => {
        this.inputText = e.target.result;
      };
      
      reader.readAsText(file);
    }
  }

  clearText(): void {
    this.inputText = '';
    this.selectedFileName = '';
    this.errorMessage = '';
    this.summary = '';
  }

  summarize(): void {
    if (!this.inputText) return;
    
    // Validazione lato client
    if (this.inputWordCount < 100) {
      this.errorMessage = 'Il testo è troppo corto per essere riassunto (minimo 100 parole)';
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
    
    // Converti parole in token approssimativi (1 parola ≈ 1.6 token in media)
    const tokenMultiplier = 1.6;
    const minTokens = Math.round(this.minLength * tokenMultiplier);
    const maxTokens = Math.round(this.maxLength * tokenMultiplier);
    
    const request: SummarizationRequest = {
      input: this.inputText,
      minLength: minTokens,
      maxLength: maxTokens,
      format: this.format
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
  
  // Auth modal methods
  toggleAuthModal(): void {
    this.showAuthModal = !this.showAuthModal;
    if (!this.showAuthModal) {
      this.resetAuthForms();
    }
  }
  
  closeAuthModal(): void {
    this.showAuthModal = false;
    this.resetAuthForms();
  }
  
  resetAuthForms(): void {
    this.showRegisterForm = false;
    this.showPassword = false;
    this.loginEmail = '';
    this.loginPassword = '';
    this.registerEmail = '';
    this.registerPassword = '';
    this.registerConfirmPassword = '';
  }
  
  switchToRegister(event: Event): void {
    event.preventDefault();
    this.showRegisterForm = true;
  }
  
  switchToLogin(event: Event): void {
    event.preventDefault();
    this.showRegisterForm = false;
  }
  
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
  
  // Helper per contare le parole
  getWordCount(text: string): number {
    if (!text || text.trim().length === 0) return 0;
    return text.trim().split(/\s+/).length;
  }
  
  get inputWordCount(): number {
    return this.getWordCount(this.inputText);
  }
  
  get summaryWordCount(): number {
    return this.getWordCount(this.summary);
  }
  
  handleLogin(event: Event): void {
    event.preventDefault();
    console.log('Login attempt:', this.loginEmail);
    // TODO: Implementare la logica di login
    alert('Login feature coming soon!');
  }
  
  handleRegister(event: Event): void {
    event.preventDefault();
    if (this.registerPassword !== this.registerConfirmPassword) {
      alert('Passwords do not match!');
      return;
    }
    console.log('Register attempt:', this.registerEmail);
    // TODO: Implementare la logica di registrazione
    alert('Registration feature coming soon!');
  }
  
  handleForgotPassword(event: Event): void {
    event.preventDefault();
    // TODO: Implementare il recupero password
    alert('Password recovery feature coming soon!');
  }
}
