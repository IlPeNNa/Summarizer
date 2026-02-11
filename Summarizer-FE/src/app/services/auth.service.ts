import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse, ResetPasswordRequest } from '../models/summarization.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private tokenKey = 'jwt_token';
  private userEmailKey = 'user_email';
  private userIdKey = 'user_id';
  
  // BehaviorSubject per notificare componenti quando l'utente fa login/logout
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Registrazione nuovo utente
   */
  register(email: string, password: string): Observable<AuthResponse> {
    const request: RegisterRequest = { email, password };
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => this.saveAuthData(response))
    );
  }

  /**
   * Login utente esistente
   */
  login(email: string, password: string): Observable<AuthResponse> {
    const request: LoginRequest = { email, password };
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => this.saveAuthData(response))
    );
  }

  /**
   * Logout utente (rimuove token dal localStorage)
   */
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        this.clearAuthData();
      })
    );
  }

  /**
   * Reset password (senza autenticazione)
   */
  resetPassword(email: string, newPassword: string): Observable<any> {
    const request: ResetPasswordRequest = { email, newPassword };
    return this.http.post(`${this.apiUrl}/reset-password`, request, { responseType: 'text' });
  }

  /**
   * Salva token e dati utente nel localStorage
   */
  private saveAuthData(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(this.userEmailKey, response.email);
    localStorage.setItem(this.userIdKey, response.userId.toString());
    this.isAuthenticatedSubject.next(true);
  }

  /**
   * Rimuove tutti i dati di autenticazione
   */
  private clearAuthData(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userEmailKey);
    localStorage.removeItem(this.userIdKey);
    this.isAuthenticatedSubject.next(false);
  }

  /**
   * Ottieni il token JWT
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Ottieni l'email dell'utente loggato
   */
  getUserEmail(): string | null {
    return localStorage.getItem(this.userEmailKey);
  }

  /**
   * Ottieni l'ID dell'utente loggato
   */
  getUserId(): number | null {
    const userId = localStorage.getItem(this.userIdKey);
    return userId ? parseInt(userId, 10) : null;
  }

  /**
   * Controlla se l'utente è autenticato
   */
  isAuthenticated(): boolean {
    return this.hasToken();
  }

  /**
   * Verifica se esiste un token
   */
  private hasToken(): boolean {
    return !!this.getToken();
  }

  /**
   * Logout manuale (senza chiamare il backend)
   */
  logoutLocal(): void {
    this.clearAuthData();
  }
}
