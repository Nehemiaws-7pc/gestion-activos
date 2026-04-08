import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

interface AuthResponse {
    token: string;
    role: string;
}

interface JwtPayload {
    sub: string;
    role: string;
    exp: number;
    iat: number;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) { }

    login(username: string, password: string): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, {
            username,
            password
        }).pipe(
            tap(response => {
                sessionStorage.setItem('token', response.token);
                sessionStorage.setItem('role', response.role);
                this.currentUserSubject.next(response);
            })
        );
    }

    logout() {
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('role');
        this.currentUserSubject.next(null);
    }

    getToken(): string | null {
        return sessionStorage.getItem('token');
    }

    isLoggedIn(): boolean {
        const token = this.getToken();
        if (!token) return false;
        return !this.isTokenExpired(token);
    }

    private isTokenExpired(token: string): boolean {
        try {
            const payload = this.decodePayload(token);
            return payload.exp * 1000 < Date.now();
        } catch {
            return true;
        }
    }

    private decodePayload(token: string): JwtPayload {
        const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
        const json = atob(base64);
        return JSON.parse(json) as JwtPayload;
    }
}
