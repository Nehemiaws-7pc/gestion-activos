import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ActivoService, Activo, ActivoUpdate } from '../core/services/activo.service';
import { EmpleadoService, Empleado } from '../core/services/empleado.service';
import { AuthService } from '../core/services/auth.service';
import Swal from 'sweetalert2';

type SearchMode = 'empleado' | 'activo';
type EstadoKey = 'ASIGNADO' | 'DISPONIBLE' | 'BAJA';

interface SeccionActivos {
    prefijo: string;
    etiqueta: string;
    total: number;
    porEstado: Record<EstadoKey, Activo[]>;
}

@Component({
    selector: 'app-activos-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './activos-list.component.html',
    styleUrls: ['./activos-list.component.css']
})
export class ActivosListComponent implements OnInit {

    // ── Lista general ─────────────────────────────
    activos: Activo[] = [];
    cargandoActivos = false;
    secciones: SeccionActivos[] = [];
    expandidos = new Set<string>();
    readonly estadosOrden: EstadoKey[] = ['ASIGNADO', 'DISPONIBLE', 'BAJA'];

    private etiquetasPrefijo: Record<string, string> = {
        LAP: 'Laptops',
        PC: 'Computadoras de escritorio',
        MON: 'Monitores',
        IMP: 'Impresoras',
        TEL: 'Teléfonos',
        TAB: 'Tabletas',
        CAM: 'Cámaras',
        PROY: 'Proyectores',
        ESC: 'Escritorios',
        SIL: 'Sillas',
        VEH: 'Vehículos',
        HER: 'Herramientas',
    };

    // ── Registro ──────────────────────────────────
    nuevoActivo: Partial<Activo> = this.activoVacio();
    registrando = false;

    // ── Búsqueda ──────────────────────────────────
    searchMode: SearchMode = 'empleado';
    searchCodigo = '';
    buscando = false;

    empleadoEncontrado: Empleado | null = null;
    activosEmpleado: Activo[] = [];
    activoEncontrado: Activo | null = null;
    errorBusqueda: string | null = null;

    // ── Edición ───────────────────────────────────
    activoEditando: Activo | null = null;
    editForm: ActivoUpdate = this.editVacio();
    guardando = false;

    constructor(
        private activoService: ActivoService,
        private empleadoService: EmpleadoService,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit() {
        this.cargarActivos();
    }

    // ── Rol ───────────────────────────────────────
    get puedeEditar(): boolean {
        const role = sessionStorage.getItem('role');
        return role === 'ADMIN' || role === 'GERENTE';
    }

    get rolActual(): string {
        return sessionStorage.getItem('role') ?? '';
    }

    // ── Lista general ─────────────────────────────
    cargarActivos() {
        this.cargandoActivos = true;
        this.activoService.getAll().subscribe({
            next: data => {
                this.activos = data;
                this.secciones = this.agruparPorPrefijo(data);
                this.cargandoActivos = false;
            },
            error: () => { Swal.fire('Error', 'No se pudo cargar la lista de activos', 'error'); this.cargandoActivos = false; }
        });
    }

    private agruparPorPrefijo(activos: Activo[]): SeccionActivos[] {
        const mapa = new Map<string, SeccionActivos>();
        for (const a of activos) {
            const prefijo = this.extraerPrefijo(a.codigo);
            let seccion = mapa.get(prefijo);
            if (!seccion) {
                seccion = {
                    prefijo,
                    etiqueta: this.etiquetasPrefijo[prefijo] ?? `Activos ${prefijo}`,
                    total: 0,
                    porEstado: { ASIGNADO: [], DISPONIBLE: [], BAJA: [] }
                };
                mapa.set(prefijo, seccion);
            }
            const estado = (a.estado as EstadoKey);
            if (seccion.porEstado[estado]) seccion.porEstado[estado].push(a);
            seccion.total++;
        }
        return Array.from(mapa.values()).sort((a, b) => a.prefijo.localeCompare(b.prefijo));
    }

    private extraerPrefijo(codigo: string): string {
        if (!codigo) return 'OTROS';
        const idx = codigo.indexOf('-');
        return (idx > 0 ? codigo.substring(0, idx) : codigo).toUpperCase();
    }

    toggleSeccion(prefijo: string) {
        if (this.expandidos.has(prefijo)) this.expandidos.delete(prefijo);
        else this.expandidos.add(prefijo);
    }

    estaExpandida(prefijo: string): boolean {
        return this.expandidos.has(prefijo);
    }

    // ── Registro ──────────────────────────────────
    registrarActivo() {
        const a = this.nuevoActivo;
        if (!a.codigo || !a.descripcion || !a.valorAdquisicion) {
            Swal.fire('Campos incompletos', 'Código, descripción y valor son obligatorios', 'warning');
            return;
        }
        this.registrando = true;
        this.activoService.create(a).subscribe({
            next: () => {
                Swal.fire('¡Registrado!', 'Activo agregado correctamente', 'success');
                this.nuevoActivo = this.activoVacio();
                this.cargarActivos();
            },
            error: () => Swal.fire('Error', 'No se pudo registrar el activo', 'error'),
            complete: () => this.registrando = false
        });
    }

    // ── Búsqueda ──────────────────────────────────
    setSearchMode(mode: SearchMode) {
        this.searchMode = mode;
        this.limpiarBusqueda();
    }

    buscar() {
        const codigo = this.searchCodigo.trim();
        if (!codigo) return;
        this.limpiarBusqueda();
        this.buscando = true;

        if (this.searchMode === 'empleado') {
            this.buscarPorEmpleado(codigo);
        } else {
            this.buscarPorActivo(codigo);
        }
    }

    private buscarPorEmpleado(codigo: string) {
        this.empleadoService.buscarPorCodigo(codigo).subscribe({
            next: emp => {
                this.empleadoEncontrado = emp;
                this.empleadoService.getActivosPorEmpleado(codigo).subscribe({
                    next: activos => {
                        this.activosEmpleado = activos;
                        this.buscando = false;
                    },
                    error: () => { this.buscando = false; }
                });
            },
            error: () => {
                this.errorBusqueda = `No se encontró el empleado con código "${codigo}"`;
                this.buscando = false;
            }
        });
    }

    private buscarPorActivo(codigo: string) {
        this.activoService.buscarPorCodigo(codigo).subscribe({
            next: activo => {
                this.activoEncontrado = activo;
                this.buscando = false;
            },
            error: () => {
                this.errorBusqueda = `No se encontró el activo con código "${codigo}"`;
                this.buscando = false;
            }
        });
    }

    limpiarBusqueda() {
        this.empleadoEncontrado = null;
        this.activosEmpleado = [];
        this.activoEncontrado = null;
        this.errorBusqueda = null;
    }

    get hayResultado(): boolean {
        return this.empleadoEncontrado !== null || this.activoEncontrado !== null;
    }

    // ── Edición ───────────────────────────────────
    abrirEditar(activo: Activo) {
        this.activoEditando = activo;
        this.editForm = {
            descripcion: activo.descripcion,
            valorActual: activo.valorActual ?? 0,
            estado: activo.estado,
            empleadoCodigo: activo.empleadoCodigo ?? ''
        };
    }

    cerrarEditar() {
        this.activoEditando = null;
        this.editForm = this.editVacio();
    }

    guardarEdicion() {
        if (!this.activoEditando?.id) return;
        this.guardando = true;
        this.activoService.actualizar(this.activoEditando.id, this.editForm).subscribe({
            next: actualizado => {
                Swal.fire('¡Guardado!', 'Activo actualizado correctamente', 'success');
                this.cerrarEditar();
                this.cargarActivos();
                // Actualizar resultado de búsqueda si estaba ahí
                if (this.activoEncontrado?.id === actualizado.id) {
                    this.activoEncontrado = actualizado;
                }
                const idx = this.activosEmpleado.findIndex(a => a.id === actualizado.id);
                if (idx !== -1) this.activosEmpleado[idx] = actualizado;
            },
            error: err => {
                const msg = err.status === 403
                    ? 'No tienes permiso para editar activos'
                    : 'No se pudo guardar el cambio';
                Swal.fire('Error', msg, 'error');
            },
            complete: () => this.guardando = false
        });
    }

    // ── Logout ────────────────────────────────────
    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }

    volver() {
        this.router.navigate(['/dashboard']);
    }

    // ── Helpers ───────────────────────────────────
    estadoClass(estado: string): string {
        return { 'DISPONIBLE': 'badge-disponible', 'ASIGNADO': 'badge-asignado', 'BAJA': 'badge-baja' }[estado] ?? '';
    }

    private activoVacio(): Partial<Activo> {
        return { codigo: '', descripcion: '', valorAdquisicion: 0, fechaAdquisicion: '', estado: 'DISPONIBLE' };
    }

    private editVacio(): ActivoUpdate {
        return { descripcion: '', valorActual: 0, estado: 'DISPONIBLE', empleadoCodigo: '' };
    }
}
