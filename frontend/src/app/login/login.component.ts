import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import Swal from 'sweetalert2';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {
    username: string = '';
    password: string = '';
    loading: boolean = false;

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    onLogin() {
        if (!this.username || !this.password) {
            Swal.fire({
                icon: 'warning',
                title: 'Campos incompletos',
                text: 'Por favor ingresa usuario y contraseña'
            });
            return;
        }

        this.loading = true;

        this.authService.login(this.username, this.password).subscribe({
            next: () => {
                Swal.fire({
                    icon: 'success',
                    title: '¡Bienvenido!',
                    text: 'Inicio de sesión exitoso',
                    timer: 1500,
                    showConfirmButton: false
                }).then(() => {
                    this.router.navigate(['/dashboard']);
                });
            },
            error: (err) => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error de acceso',
                    text: 'Usuario o contraseña incorrectos'
                });
                this.loading = false;
            }
        });
    }
}