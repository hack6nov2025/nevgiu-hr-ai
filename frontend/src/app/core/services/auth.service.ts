import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';

const STORAGE_KEY = 'finance_ai_auth';

export interface AuthState {
  token: string;
  firstName: string;
  lastName: string;
  userId: string;
}

/**
 * Auth service for login / register and JWT storage.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private authStateSubject =
    new BehaviorSubject<AuthState | null>(this.loadFromStorage());

  authState$ = this.authStateSubject.asObservable();

  // TODO: change to your VM public IP in prod
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  register(payload: {
    firstName: string;
    lastName: string;
    userId: string;
    password: string;
  }) {
    return this.http.post<AuthState>(
      `${this.baseUrl}/register`,
      payload
    ).pipe(
      tap(state => this.setAuthState(state))
    );
  }

  login(userId: string, password: string) {
    return this.http.post<AuthState>(
      `${this.baseUrl}/login`,
      { userId, password }
    ).pipe(
      tap(state => this.setAuthState(state))
    );
  }

  logout(): void {
    this.setAuthState(null);
  }

  getToken(): string | null {
    return this.authStateSubject.value?.token ?? null;
  }

  private setAuthState(state: AuthState | null) {
    this.authStateSubject.next(state);
    if (state) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }

  private loadFromStorage(): AuthState | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as AuthState;
    } catch {
      return null;
    }
  }

}
