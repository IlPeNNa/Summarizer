import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SummarizerService } from './services/summarizer.service';
import { AuthService } from './services/auth.service';
import { SummarizationRequest, SummaryLengthPreset, SummaryItem } from './models/summarization.model';

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
export class AppComponent implements OnInit {
  title = 'Summarizer';
  
  // Input data
  inputText: string = '';
  selectedFileName: string = '';
  selectedFile: File | null = null;
  
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
  lastSummaryId: number | null = null;
  
  // Stati
  isLoading: boolean = false;
  errorMessage: string = '';
  showDownloadMenu: boolean = false;
  isDragOver: boolean = false;
  isCopied: boolean = false;
  
  // Auth
  isAuthenticated: boolean = false;
  currentUserEmail: string | null = null;
  showAuthModal: boolean = false;
  showRegisterForm: boolean = false;
  showPassword: boolean = false;
  loginEmail: string = '';
  loginPassword: string = '';
  registerEmail: string = '';
  registerPassword: string = '';
  registerConfirmPassword: string = '';
  authError: string = '';
  
  // Riassunti utente
  showSummariesSidebar: boolean = false;
  userSummaries: SummaryItem[] = [];
  totalSummariesCount: number = 0;
  
  // Feedback
  showFeedbackModal: boolean = false;
  feedbackRating: number = 0;
  feedbackComment: string = '';
  feedbackError: string = '';
  
  // Change Password
  showChangePasswordModal: boolean = false;
  changePasswordEmail: string = '';
  changePasswordNew: string = '';
  changePasswordConfirm: string = '';
  changePasswordError: string = '';

  constructor(
    private summarizerService: SummarizerService,
    private authService: AuthService
  ) {}
  
  ngOnInit(): void {
    // Controlla se l'utente è autenticato
    this.authService.isAuthenticated$.subscribe(isAuth => {
      this.isAuthenticated = isAuth;
      this.currentUserEmail = isAuth ? this.authService.getUserEmail() : null;
      
      // Se l'utente si è appena autenticato, carica i suoi riassunti
      if (isAuth) {
        this.loadUserSummaries();
      }
    });
  }
  
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

  onMinLengthChange(): void {
    // Assicura che minLength non superi maxLength
    if (this.minLength > this.maxLength) {
      this.maxLength = this.minLength;
    }
  }

  onMaxLengthChange(): void {
    // Assicura che maxLength non sia inferiore a minLength
    if (this.maxLength < this.minLength) {
      this.minLength = this.maxLength;
    }
  }

  onFileSelect(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    this.selectedFile = file;
    this.selectedFileName = file.name;
    
    // Valida il tipo di file
    const validExtensions = ['.txt', '.pdf', '.docx'];
    const fileExtension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'));
    
    if (!validExtensions.includes(fileExtension)) {
      this.errorMessage = 'Formato file non supportato. Usa .txt, .pdf o .docx';
      this.selectedFile = null;
      this.selectedFileName = '';
      setTimeout(() => this.errorMessage = '', 5000);
      return;
    }
    
    // Per i file TXT, mostra il contenuto nell'editor
    if (fileExtension === '.txt') {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.inputText = e.target.result;
        this.selectedFile = null; // Non serve più il file, usiamo il testo
      };
      reader.readAsText(file);
    } else {
      // Per PDF e DOCX, estrai il testo e mostralo
      this.isLoading = true;
      this.inputText = 'Estrazione testo in corso...';
      
      this.summarizerService.extractTextFromFile(file).subscribe({
        next: (response) => {
          this.inputText = response.text;
          this.selectedFile = null; // Non serve più il file, usiamo il testo estratto
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Errore durante l\'estrazione del testo';
          this.inputText = '';
          this.selectedFile = null;
          this.selectedFileName = '';
          this.isLoading = false;
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    }
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
      this.selectedFile = file;
      this.selectedFileName = file.name;
      
      // Valida il tipo di file
      const validExtensions = ['.txt', '.pdf', '.docx'];
      const fileExtension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'));
      
      if (!validExtensions.includes(fileExtension)) {
        this.errorMessage = 'Formato file non supportato. Usa .txt, .pdf o .docx';
        this.selectedFile = null;
        this.selectedFileName = '';
        setTimeout(() => this.errorMessage = '', 5000);
        return;
      }
      
      // Per i file TXT, mostra il contenuto nell'editor
      if (fileExtension === '.txt') {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.inputText = e.target.result;
          this.selectedFile = null; // Non serve più il file, usiamo il testo
        };
        reader.readAsText(file);
      } else {
        // Per PDF e DOCX, estrai il testo e mostralo
        this.isLoading = true;
        this.inputText = 'Estrazione testo in corso...';
        
        this.summarizerService.extractTextFromFile(file).subscribe({
          next: (response) => {
            this.inputText = response.text;
            this.selectedFile = null; // Non serve più il file, usiamo il testo estratto
            this.isLoading = false;
          },
          error: (error) => {
            this.errorMessage = error.error?.error || 'Errore durante l\'estrazione del testo';
            this.inputText = '';
            this.selectedFile = null;
            this.selectedFileName = '';
            this.isLoading = false;
            setTimeout(() => this.errorMessage = '', 5000);
          }
        });
      }
    }
  }

  clearText(): void {
    this.inputText = '';
    this.selectedFileName = '';
    this.selectedFile = null;
    this.errorMessage = '';
    this.summary = '';
  }

  summarize(): void {
    // Validazione testo
    if (!this.inputText) {
      this.errorMessage = 'Inserisci del testo o carica un file';
      return;
    }
    
    if (this.inputWordCount < 50) {
      this.errorMessage = 'Il testo è troppo corto per essere riassunto (minimo 50 parole)';
      return;
    }
    
    // Validazione parametri lunghezza
    if (this.minLength < 10) {
      this.errorMessage = 'La lunghezza minima deve essere almeno 10 parole';
      return;
    }
    
    if (this.maxLength > 600) {
      this.errorMessage = 'La lunghezza massima non può superare 600 parole';
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
    
    // Usa sempre il testo nel textarea (che sia stato digitato o estratto da file)
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
        this.lastSummaryId = response.summaryId || null;
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
      this.isCopied = true;
      setTimeout(() => {
        this.isCopied = false;
      }, 2000);
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
    this.authError = '';
    
    if (!this.loginEmail || !this.loginPassword) {
      this.authError = 'Inserisci email e password';
      return;
    }
    
    this.isLoading = true;
    this.authService.login(this.loginEmail, this.loginPassword).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.showAuthModal = false;
        this.resetAuthForms();
        this.loadUserSummaries();
      },
      error: (error) => {
        this.isLoading = false;
        this.authError = 'Email o password non corretti';
      }
    });
  }
  
  handleRegister(event: Event): void {
    event.preventDefault();
    this.authError = '';
    
    if (!this.registerEmail || !this.registerPassword || !this.registerConfirmPassword) {
      this.authError = 'Compila tutti i campi';
      return;
    }
    
    if (this.registerPassword !== this.registerConfirmPassword) {
      this.authError = 'Le password non corrispondono';
      return;
    }
    
    if (this.registerPassword.length < 6) {
      this.authError = 'La password deve essere almeno 6 caratteri';
      return;
    }
    
    this.isLoading = true;
    this.authService.register(this.registerEmail, this.registerPassword).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.showAuthModal = false;
        this.resetAuthForms();
      },
      error: (error) => {
        this.isLoading = false;
        this.authError = error.error?.error || 'Errore durante la registrazione. Email già esistente?';
      }
    });
  }
  
  handleForgotPassword(event: Event): void {
    event.preventDefault();
    this.closeAuthModal();
    this.openChangePasswordModal();
  }
  
  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.userSummaries = [];
        this.showSummariesSidebar = false;
      },
      error: () => {
        // Anche se il server fallisce, rimuovi il token localmente
        this.authService.logoutLocal();
        this.userSummaries = [];
        this.showSummariesSidebar = false;
      }
    });
  }
  
  openAuthModal(): void {
    this.showAuthModal = true;
    this.authError = '';
  }
  
  toggleSummariesSidebar(): void {
    if (!this.isAuthenticated) {
      this.openAuthModal();
      return;
    }
    
    this.showSummariesSidebar = !this.showSummariesSidebar;
    
    if (this.showSummariesSidebar) {
      this.loadUserSummaries();
    }
  }
  
  loadUserSummaries(): void {
    this.summarizerService.getMySummaries(10).subscribe({
      next: (response) => {
        this.userSummaries = response.summaries;
        this.totalSummariesCount = response.totalCount;
      },
      error: (error) => {
        console.error('Errore caricamento riassunti:', error);
      }
    });
  }
  
  loadSummary(summary: SummaryItem): void {
    this.summary = summary.summaryText;
    this.summaryLength = summary.summaryLength;
    this.showSummariesSidebar = false;
    this.lastSummaryId = summary.id;
  }
  
  deleteSummaryItem(id: number, event: Event): void {
    event.stopPropagation();
    
    if (!confirm('Vuoi eliminare questo riassunto?')) {
      return;
    }
    
    this.summarizerService.deleteSummary(id).subscribe({
      next: () => {
        this.loadUserSummaries();
      },
      error: (error) => {
        console.error('Errore eliminazione riassunto:', error);
      }
    });
  }
  
  openFeedbackModal(): void {
    if (!this.isAuthenticated) {
      this.openAuthModal();
      return;
    }
    
    if (!this.summary) {
      alert('Genera prima un riassunto per lasciare un feedback');
      return;
    }
    
    this.showFeedbackModal = true;
    this.feedbackRating = 0;
    this.feedbackComment = '';
    this.feedbackError = '';
  }
  
  setRating(rating: number): void {
    this.feedbackRating = rating;
  }
  
  closeFeedbackModal(): void {
    this.showFeedbackModal = false;
    this.feedbackRating = 0;
    this.feedbackComment = '';
    this.feedbackError = '';
  }
  
  submitFeedback(event?: Event): void {
    if (event) {
      event.preventDefault();
    }
    
    if (this.feedbackRating === 0) {
      this.feedbackError = 'Seleziona una valutazione (stelle)';
      return;
    }
    
    // Se non abbiamo lastSummaryId, significa che il riassunto non è stato salvato
    // (utente non loggato quando ha generato il riassunto)
    if (!this.lastSummaryId) {
      this.feedbackError = 'Questo riassunto non è salvato. Genera un nuovo riassunto dopo aver effettuato il login.';
      return;
    }
    
    this.isLoading = true;
    this.feedbackError = '';
    
    this.summarizerService.submitFeedback({
      summaryId: this.lastSummaryId,
      rating: this.feedbackRating,
      comment: this.feedbackComment || undefined
    }).subscribe({
      next: () => {
        this.isLoading = false;
        this.showFeedbackModal = false;
        alert('Grazie per il tuo feedback!');
      },
      error: (error) => {
        this.isLoading = false;
        this.feedbackError = error.error?.error || 'Errore durante l\'invio del feedback';
      }
    });
  }
  
  openChangePasswordModal(): void {
    this.showChangePasswordModal = true;
    // Se l'utente è loggato, precompila l'email, altrimenti lasciala vuota
    this.changePasswordEmail = this.isAuthenticated ? (this.currentUserEmail || '') : '';
    this.changePasswordNew = '';
    this.changePasswordConfirm = '';
    this.changePasswordError = '';
  }
  
  closeChangePasswordModal(): void {
    this.showChangePasswordModal = false;
    this.changePasswordEmail = '';
    this.changePasswordNew = '';
    this.changePasswordConfirm = '';
    this.changePasswordError = '';
  }
  
  handleChangePassword(event: Event): void {
    event.preventDefault();
    
    // Validazione
    if (!this.changePasswordEmail || !this.changePasswordNew || !this.changePasswordConfirm) {
      this.changePasswordError = 'Compila tutti i campi';
      return;
    }
    
    if (this.changePasswordNew !== this.changePasswordConfirm) {
      this.changePasswordError = 'Le password non corrispondono';
      return;
    }
    
    if (this.changePasswordNew.length < 6) {
      this.changePasswordError = 'La password deve essere di almeno 6 caratteri';
      return;
    }
    
    this.isLoading = true;
    this.changePasswordError = '';
    
    this.authService.resetPassword(this.changePasswordEmail, this.changePasswordNew).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.closeChangePasswordModal();
        alert('Password reimpostata con successo! Puoi ora effettuare il login.');
      },
      error: (error) => {
        this.isLoading = false;
        // Il backend restituisce testo semplice
        if (typeof error.error === 'string') {
          this.changePasswordError = error.error;
        } else if (error.message) {
          this.changePasswordError = error.message;
        } else {
          this.changePasswordError = 'Errore durante il cambio password';
        }
      }
    });
  }
}
