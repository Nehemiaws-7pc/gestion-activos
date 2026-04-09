import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

    constructor(
        private router: Router,
        private authService: AuthService
    ) { }

    get rolActual(): string {
        return sessionStorage.getItem('role') ?? '';
    }

    get puedeEditar(): boolean {
        const role = sessionStorage.getItem('role');
        return role === 'ADMIN' || role === 'GERENTE';
    }

    irA(ruta: string) {
        this.router.navigate([ruta]);
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
