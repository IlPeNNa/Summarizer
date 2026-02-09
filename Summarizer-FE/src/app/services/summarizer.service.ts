import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SummarizationRequest, SummarizationResponse, DownloadRequest } from '../models/summarization.model';

@Injectable({
  providedIn: 'root'
})
export class SummarizerService {
  private readonly apiUrl = 'http://localhost:8080/api/summarize';

  constructor(private http: HttpClient) {}

  /**
   * Riassume un testo
   */
  summarize(request: SummarizationRequest): Observable<SummarizationResponse> {
    return this.http.post<SummarizationResponse>(this.apiUrl, request);
  }

  /**
   * Upload e riassumi un file
   */
  uploadAndSummarize(file: File, minLength: number, maxLength: number, format: string = 'paragraph'): Observable<SummarizationResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('minLength', minLength.toString());
    formData.append('maxLength', maxLength.toString());
    formData.append('format', format);
    
    return this.http.post<SummarizationResponse>(`${this.apiUrl}/upload`, formData);
  }

  /**
   * Estrai testo da un file senza riassumerlo
   */
  extractTextFromFile(file: File): Observable<{text: string, length: number, filename: string}> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<{text: string, length: number, filename: string}>(`${this.apiUrl}/extract`, formData);
  }

  /**
   * Download riassunto come TXT
   */
  downloadTxt(text: string): Observable<Blob> {
    const request: DownloadRequest = { text };
    return this.http.post(`${this.apiUrl}/download/txt`, request, { 
      responseType: 'blob' 
    });
  }

  /**
   * Download riassunto come DOCX
   */
  downloadDocx(text: string): Observable<Blob> {
    const request: DownloadRequest = { text };
    return this.http.post(`${this.apiUrl}/download/docx`, request, { 
      responseType: 'blob' 
    });
  }

  /**
   * Download riassunto come PDF
   */
  downloadPdf(text: string): Observable<Blob> {
    const request: DownloadRequest = { text };
    return this.http.post(`${this.apiUrl}/download/pdf`, request, { 
      responseType: 'blob' 
    });
  }

  /**
   * Verifica health del servizio NLP
   */
  checkHealth(): Observable<any> {
    return this.http.get(`${this.apiUrl}/health`);
  }

  /**
   * Helper per scaricare un blob
   */
  downloadBlob(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
