import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UbicacionService, Ubicacion, UbicacionDetalle, EmpleadoConActivos } from '../core/services/ubicacion.service';
import Swal from 'sweetalert2';

@Component({
    selector: 'app-ubicaciones',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './ubicaciones.component.html',
    styleUrls: ['./ubicaciones.component.css']
})
export class UbicacionesComponent implements OnInit {

    ubicaciones: Ubicacion[] = [];
    cargando = false;

    // Formulario nuevo
    nuevaUbicacion: Partial<Ubicacion> = this.ubicacionVacia();
    creando = false;
    mostrarFormNuevo = false;

    // Edición
    ubicacionEditando: Ubicacion | null = null;
    editForm: Partial<Ubicacion> = {};
    guardando = false;

    // Detalle (visualizar)
    detalle: UbicacionDetalle | null = null;
    cargandoDetalle = false;
    empleadoExpandido: number | null = null;

    constructor(
        private ubicacionService: UbicacionService,
        private router: Router
    ) { }

    ngOnInit() {
        this.cargar();
    }

    cargar() {
        this.cargando = true;
        this.ubicacionService.getAll().subscribe({
            next: data => { this.ubicaciones = data; this.cargando = false; },
            error: () => { Swal.fire('Error', 'No se pudo cargar la lista de ubicaciones', 'error'); this.cargando = false; }
        });
    }

    // ── Crear ──
    abrirFormNuevo() {
        this.nuevaUbicacion = this.ubicacionVacia();
        this.mostrarFormNuevo = true;
    }

    cancelarNuevo() {
        this.mostrarFormNuevo = false;
        this.nuevaUbicacion = this.ubicacionVacia();
    }

    crear() {
        const u = this.nuevaUbicacion;
        if (!u.codigo || !u.nombre) {
            Swal.fire('Campos incompletos', 'Código y nombre son obligatorios', 'warning');
            return;
        }
        this.creando = true;
        this.ubicacionService.crear(u as Ubicacion).subscribe({
            next: () => {
                Swal.fire({ icon: 'success', title: 'Creada', text: 'Ubicación registrada correctamente', timer: 1500, showConfirmButton: false });
                this.cancelarNuevo();
                this.cargar();
            },
            error: (err) => {
                const msg = err.status === 409 ? 'Ya existe una ubicación con ese código' : 'No se pudo crear la ubicación';
                Swal.fire('Error', msg, 'error');
            },
            complete: () => this.creando = false
        });
    }

    // ── Editar ──
    abrirEditar(ub: Ubicacion) {
        this.ubicacionEditando = ub;
        this.editForm = { ...ub };
    }

    cerrarEditar() {
        this.ubicacionEditando = null;
        this.editForm = {};
    }

    guardar() {
        if (!this.ubicacionEditando?.id) return;
        this.guardando = true;
        this.ubicacionService.actualizar(this.ubicacionEditando.id, this.editForm as Ubicacion).subscribe({
            next: () => {
                Swal.fire({ icon: 'success', title: 'Actualizada', text: 'Ubicación actualizada correctamente', timer: 1500, showConfirmButton: false });
                this.cerrarEditar();
                this.cargar();
            },
            error: (err) => {
                const msg = err.status === 409 ? 'Ya existe una ubicación con ese código' : 'No se pudo actualizar';
                Swal.fire('Error', msg, 'error');
            },
            complete: () => this.guardando = false
        });
    }

    // ── Eliminar ──
    eliminar(ub: Ubicacion) {
        Swal.fire({
            title: '¿Eliminar ubicación?',
            text: `${ub.nombre} (${ub.codigo}) será eliminada.`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#c62828',
            cancelButtonColor: '#546e7a',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(result => {
            if (!result.isConfirmed) return;
            this.ubicacionService.eliminar(ub.id!).subscribe({
                next: () => {
                    Swal.fire({ icon: 'success', title: 'Eliminada', timer: 1200, showConfirmButton: false });
                    this.cargar();
                },
                error: (err) => {
                    const msg = err.status === 409
                        ? 'La ubicación tiene empleados asignados. Reasígnalos primero.'
                        : 'No se pudo eliminar la ubicación';
                    Swal.fire('Error', msg, 'error');
                }
            });
        });
    }

    // ── Detalle / Visualizar ──
    verDetalle(ub: Ubicacion) {
        this.cargandoDetalle = true;
        this.empleadoExpandido = null;
        this.ubicacionService.getDetalle(ub.id!).subscribe({
            next: data => { this.detalle = data; this.cargandoDetalle = false; },
            error: () => { Swal.fire('Error', 'No se pudo cargar el detalle', 'error'); this.cargandoDetalle = false; }
        });
    }

    cerrarDetalle() {
        this.detalle = null;
        this.empleadoExpandido = null;
    }

    toggleEmpleado(empId: number) {
        this.empleadoExpandido = this.empleadoExpandido === empId ? null : empId;
    }

    estadoClass(estado: string): string {
        switch (estado) {
            case 'ASIGNADO': return 'estado-asignado';
            case 'DISPONIBLE': return 'estado-disponible';
            case 'BAJA': return 'estado-baja';
            default: return '';
        }
    }

    volver() {
        this.router.navigate(['/dashboard']);
    }

    private ubicacionVacia(): Partial<Ubicacion> {
        return { codigo: '', nombre: '', direccion: '', descripcion: '' };
    }
}
