import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Empleado {
    id?: number;
    codigo: string;
    nombre: string;
    apellido: string;
    departamento: string;
    cargo: string;
}

@Injectable({
    providedIn: 'root'
})
export class EmpleadoService {

    constructor(private http: HttpClient) { }

    getAll(): Observable<Empleado[]> {
        return this.http.get<Empleado[]>(`${environment.apiUrl}/empleados`);
    }

    buscarPorCodigo(codigo: string): Observable<Empleado> {
        return this.http.get<Empleado>(`${environment.apiUrl}/empleados/buscar`, {
            params: { codigo }
        });
    }

    getActivosPorEmpleado(codigo: string): Observable<any[]> {
        return this.http.get<any[]>(`${environment.apiUrl}/empleados/${codigo}/activos`);
    }

    crear(empleado: Empleado): Observable<Empleado> {
        return this.http.post<Empleado>(`${environment.apiUrl}/empleados`, empleado);
    }

    actualizar(id: number, empleado: Empleado): Observable<Empleado> {
        return this.http.put<Empleado>(`${environment.apiUrl}/empleados/${id}`, empleado);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/empleados/${id}`);
    }
}
