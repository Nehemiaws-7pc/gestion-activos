import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Activo {
    id?: number;
    codigo: string;
    descripcion: string;
    valorAdquisicion: number;
    fechaAdquisicion: string;
    valorActual?: number;
    estado: string;
    ubicacionActual: string;
    empleadoId?: number;
    empleadoCodigo?: string;
    empleadoNombre?: string;
}

export interface ActivoUpdate {
    descripcion: string;
    valorActual: number;
    estado: string;
    ubicacionActual: string;
    empleadoCodigo: string;
}

@Injectable({
    providedIn: 'root'
})
export class ActivoService {

    constructor(private http: HttpClient) { }

    getAll(): Observable<Activo[]> {
        return this.http.get<Activo[]>(`${environment.apiUrl}/activos`);
    }

    create(activo: Partial<Activo>): Observable<Activo> {
        return this.http.post<Activo>(`${environment.apiUrl}/activos`, activo);
    }

    buscarPorCodigo(codigo: string): Observable<Activo> {
        return this.http.get<Activo>(`${environment.apiUrl}/activos/buscar`, {
            params: { codigo }
        });
    }

    actualizar(id: number, dto: ActivoUpdate): Observable<Activo> {
        return this.http.put<Activo>(`${environment.apiUrl}/activos/${id}`, dto);
    }
}
