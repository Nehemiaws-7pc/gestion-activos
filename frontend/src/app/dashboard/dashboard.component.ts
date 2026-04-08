import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
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

    irA(ruta: string) {
        this.router.navigate([ruta]);
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
