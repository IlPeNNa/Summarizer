import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Ottieni il token
    const token = this.authService.getToken();
    
    // Se esiste un token, aggiungi sempre l'header Authorization
    // Gli endpoint pubblici lo accetteranno ma non lo richiederanno
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    // Continua con la richiesta e gestisci errori 401 (Unauthorized)
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token scaduto o non valido → logout automatico
          this.authService.logoutLocal();
        }
        return throwError(() => error);
      })
    );
  }
}
