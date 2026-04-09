import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EmpleadoService, Empleado } from '../core/services/empleado.service';
import { UbicacionService, Ubicacion } from '../core/services/ubicacion.service';
import Swal from 'sweetalert2';

@Component({
    selector: 'app-empleados',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './empleados.component.html',
    styleUrls: ['./empleados.component.css']
})
export class EmpleadosComponent implements OnInit {

    empleados: Empleado[] = [];
    ubicaciones: Ubicacion[] = [];
    cargando = false;

    // Formulario nuevo
    nuevoEmpleado: Partial<Empleado> = this.empleadoVacio();
    creando = false;
    mostrarFormNuevo = false;

    // Edición
    empleadoEditando: Empleado | null = null;
    editForm: Partial<Empleado> = {};
    guardando = false;

    get puedeEditar(): boolean {
        const role = sessionStorage.getItem('role');
        return role === 'ADMIN' || role === 'GERENTE';
    }

    constructor(
        private empleadoService: EmpleadoService,
        private ubicacionService: UbicacionService,
        private router: Router
    ) { }

    ngOnInit() {
        this.cargar();
        this.cargarUbicaciones();
    }

    cargarUbicaciones() {
        this.ubicacionService.getAll().subscribe({
            next: data => this.ubicaciones = data
        });
    }

    cargar() {
        this.cargando = true;
        this.empleadoService.getAll().subscribe({
            next: data => { this.empleados = data; this.cargando = false; },
            error: () => { Swal.fire('Error', 'No se pudo cargar la lista de empleados', 'error'); this.cargando = false; }
        });
    }

    abrirFormNuevo() {
        this.nuevoEmpleado = this.empleadoVacio();
        this.mostrarFormNuevo = true;
    }

    cancelarNuevo() {
        this.mostrarFormNuevo = false;
        this.nuevoEmpleado = this.empleadoVacio();
    }

    crear() {
        const e = this.nuevoEmpleado;
        if (!e.codigo || !e.nombre) {
            Swal.fire('Campos incompletos', 'Código y nombre son obligatorios', 'warning');
            return;
        }
        this.creando = true;
        this.empleadoService.crear(e as Empleado).subscribe({
            next: () => {
                Swal.fire({ icon: 'success', title: '¡Creado!', text: 'Empleado registrado correctamente', timer: 1500, showConfirmButton: false });
                this.cancelarNuevo();
                this.cargar();
            },
            error: (err) => {
                const msg = err.status === 409 ? 'Ya existe un empleado con ese código' : 'No se pudo crear el empleado';
                Swal.fire('Error', msg, 'error');
            },
            complete: () => this.creando = false
        });
    }

    abrirEditar(emp: Empleado) {
        this.empleadoEditando = emp;
        this.editForm = { ...emp };
    }

    cerrarEditar() {
        this.empleadoEditando = null;
        this.editForm = {};
    }

    guardar() {
        if (!this.empleadoEditando?.id) return;
        this.guardando = true;
        this.empleadoService.actualizar(this.empleadoEditando.id, this.editForm as Empleado).subscribe({
            next: () => {
                Swal.fire({ icon: 'success', title: '¡Actualizado!', text: 'Empleado actualizado correctamente', timer: 1500, showConfirmButton: false });
                this.cerrarEditar();
                this.cargar();
            },
            error: (err) => {
                const msg = err.status === 409 ? 'Ya existe un empleado con ese código' : 'No se pudo actualizar el empleado';
                Swal.fire('Error', msg, 'error');
            },
            complete: () => this.guardando = false
        });
    }

    eliminar(emp: Empleado) {
        Swal.fire({
            title: '¿Eliminar empleado?',
            text: `${emp.nombre} ${emp.apellido} (${emp.codigo}) será eliminado.`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#c62828',
            cancelButtonColor: '#546e7a',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(result => {
            if (!result.isConfirmed) return;
            this.empleadoService.eliminar(emp.id!).subscribe({
                next: () => {
                    Swal.fire({ icon: 'success', title: 'Eliminado', timer: 1200, showConfirmButton: false });
                    this.cargar();
                },
                error: (err) => {
                    const msg = err.status === 409
                        ? err.error?.message ?? 'El empleado tiene activos asignados'
                        : 'No se pudo eliminar el empleado';
                    Swal.fire('Error', msg, 'error');
                }
            });
        });
    }

    volver() {
        this.router.navigate(['/dashboard']);
    }

    private empleadoVacio(): Partial<Empleado> {
        return { codigo: '', nombre: '', apellido: '', departamento: '', cargo: '', ubicacionId: null };
    }
}
