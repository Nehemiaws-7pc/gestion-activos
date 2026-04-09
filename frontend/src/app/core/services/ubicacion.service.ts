import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Ubicacion {
    id?: number;
    codigo: string;
    nombre: string;
    direccion: string;
    descripcion: string;
}

export interface ActivoDetalle {
    id: number;
    codigo: string;
    descripcion: string;
    valorAdquisicion: number;
    valorActual: number;
    estado: string;
    ubicacionActual: string;
}

export interface EmpleadoConActivos {
    id: number;
    codigo: string;
    nombre: string;
    apellido: string;
    departamento: string;
    cargo: string;
    activos: ActivoDetalle[];
}

export interface UbicacionDetalle {
    id: number;
    codigo: string;
    nombre: string;
    direccion: string;
    descripcion: string;
    empleados: EmpleadoConActivos[];
}

@Injectable({
    providedIn: 'root'
})
export class UbicacionService {

    constructor(private http: HttpClient) { }

    getAll(): Observable<Ubicacion[]> {
        return this.http.get<Ubicacion[]>(`${environment.apiUrl}/ubicaciones`);
    }

    crear(ubicacion: Ubicacion): Observable<Ubicacion> {
        return this.http.post<Ubicacion>(`${environment.apiUrl}/ubicaciones`, ubicacion);
    }

    actualizar(id: number, ubicacion: Ubicacion): Observable<Ubicacion> {
        return this.http.put<Ubicacion>(`${environment.apiUrl}/ubicaciones/${id}`, ubicacion);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/ubicaciones/${id}`);
    }

    getDetalle(id: number): Observable<UbicacionDetalle> {
        return this.http.get<UbicacionDetalle>(`${environment.apiUrl}/ubicaciones/${id}/detalle`);
    }
}
