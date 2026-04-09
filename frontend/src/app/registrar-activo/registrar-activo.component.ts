import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ActivoService, Activo } from '../core/services/activo.service';
import { EmpleadoService, Empleado } from '../core/services/empleado.service';
import Swal from 'sweetalert2';

@Component({
    selector: 'app-registrar-activo',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './registrar-activo.component.html',
    styleUrls: ['./registrar-activo.component.css']
})
export class RegistrarActivoComponent implements OnInit {

    nuevoActivo: Partial<Activo> = this.activoVacio();
    registrando = false;
    empleados: Empleado[] = [];

    constructor(
        private activoService: ActivoService,
        private empleadoService: EmpleadoService,
        private router: Router
    ) { }

    ngOnInit() {
        this.empleadoService.getAll().subscribe({
            next: data => this.empleados = data,
            error: () => { /* lista opcional, no mostrar error */ }
        });
    }

    registrar() {
        const a = this.nuevoActivo;
        if (!a.codigo || !a.descripcion || !a.valorAdquisicion) {
            Swal.fire('Campos incompletos', 'Código, descripción y valor son obligatorios', 'warning');
            return;
        }
        this.registrando = true;
        this.activoService.create(a).subscribe({
            next: () => {
                Swal.fire({
                    icon: 'success',
                    title: '¡Registrado!',
                    text: 'Activo agregado correctamente',
                    timer: 1800,
                    showConfirmButton: false
                }).then(() => {
                    this.nuevoActivo = this.activoVacio();
                });
            },
            error: () => Swal.fire('Error', 'No se pudo registrar el activo', 'error'),
            complete: () => this.registrando = false
        });
    }

    volver() {
        this.router.navigate(['/dashboard']);
    }

    private activoVacio(): Partial<Activo> {
        return {
            codigo: '',
            descripcion: '',
            valorAdquisicion: 0,
            fechaAdquisicion: '',
            estado: 'DISPONIBLE',
            empleadoCodigo: ''
        };
    }
}
