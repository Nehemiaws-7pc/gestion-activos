import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ActivosListComponent } from './activos-list/activos-list.component';
import { RegistrarActivoComponent } from './registrar-activo/registrar-activo.component';
import { EmpleadosComponent } from './empleados/empleados.component';
import { UbicacionesComponent } from './ubicaciones/ubicaciones.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'activos', component: ActivosListComponent, canActivate: [authGuard] },
    { path: 'activos/nuevo', component: RegistrarActivoComponent, canActivate: [authGuard] },
    { path: 'empleados', component: EmpleadosComponent, canActivate: [authGuard] },
    { path: 'ubicaciones', component: UbicacionesComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: 'login' }
];
