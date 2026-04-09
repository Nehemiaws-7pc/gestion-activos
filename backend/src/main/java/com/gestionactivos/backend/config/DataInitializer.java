package com.gestionactivos.backend.config;

import com.gestionactivos.backend.model.Activo;
import com.gestionactivos.backend.model.Empleado;
import com.gestionactivos.backend.model.Role;
import com.gestionactivos.backend.model.Ubicacion;
import com.gestionactivos.backend.model.User;
import com.gestionactivos.backend.repository.ActivoRepository;
import com.gestionactivos.backend.repository.EmpleadoRepository;
import com.gestionactivos.backend.repository.UbicacionRepository;
import com.gestionactivos.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpleadoRepository empleadoRepository;
    private final ActivoRepository activoRepository;
    private final UbicacionRepository ubicacionRepository;

    @Override
    public void run(String... args) {
        seedUsers();
        seedUbicaciones();
        seedEmpleados();
        seedActivos();
    }

    private void seedUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(buildUser("admin", "admin123", Role.ADMIN));
        }
        if (userRepository.findByUsername("gerente").isEmpty()) {
            userRepository.save(buildUser("gerente", "gerente123", Role.GERENTE));
        }
        if (userRepository.findByUsername("empleado").isEmpty()) {
            userRepository.save(buildUser("empleado", "empleado123", Role.EMPLEADO));
        }
    }

    private void seedUbicaciones() {
        if (ubicacionRepository.count() > 0) return;

        ubicacionRepository.save(buildUbicacion("UBI-001", "Sede Central",       "Av. Principal 123, Ciudad", "Oficina principal de la empresa"));
        ubicacionRepository.save(buildUbicacion("UBI-002", "Sucursal Norte",     "Calle Norte 456, Ciudad",   "Sucursal zona norte"));
        ubicacionRepository.save(buildUbicacion("UBI-003", "Data Center",        "Parque Tecnológico 789",    "Centro de datos principal"));
    }

    private void seedEmpleados() {
        if (empleadoRepository.count() > 0) return;

        Ubicacion sedeCentral = ubicacionRepository.findByCodigo("UBI-001").orElse(null);
        Ubicacion sucursalNorte = ubicacionRepository.findByCodigo("UBI-002").orElse(null);
        Ubicacion dataCenter = ubicacionRepository.findByCodigo("UBI-003").orElse(null);

        empleadoRepository.save(buildEmpleado("EMP-001", "Carlos", "Méndez",   "Tecnología",   "Desarrollador Senior", sedeCentral));
        empleadoRepository.save(buildEmpleado("EMP-002", "Laura",  "Castillo",  "Contabilidad", "Contadora",           sedeCentral));
        empleadoRepository.save(buildEmpleado("EMP-003", "Diego",  "Ramírez",   "Tecnología",   "Diseñador UX",        sucursalNorte));
        empleadoRepository.save(buildEmpleado("EMP-004", "Ana",    "González",  "RRHH",         "Gestora de Talento",  sucursalNorte));
        empleadoRepository.save(buildEmpleado("EMP-005", "Marco",  "Velásquez", "Gerencia",     "Gerente de Proyecto", dataCenter));
    }

    private void seedActivos() {
        if (activoRepository.count() > 0) return;

        Empleado emp1 = empleadoRepository.findByCodigo("EMP-001").orElseThrow();
        Empleado emp2 = empleadoRepository.findByCodigo("EMP-002").orElseThrow();
        Empleado emp3 = empleadoRepository.findByCodigo("EMP-003").orElseThrow();

        activoRepository.save(buildActivo("LAP-001", "Laptop Dell XPS 15",
                new BigDecimal("8500.00"), new BigDecimal("7200.00"),
                LocalDate.of(2024, 3, 10), "ASIGNADO", "Oficina TI - 3B", emp1));

        activoRepository.save(buildActivo("MON-001", "Monitor LG UltraWide 34\"",
                new BigDecimal("3200.00"), new BigDecimal("2800.00"),
                LocalDate.of(2024, 3, 10), "ASIGNADO", "Oficina TI - 3B", emp1));

        activoRepository.save(buildActivo("LAP-002", "Laptop HP EliteBook 840",
                new BigDecimal("6800.00"), new BigDecimal("5900.00"),
                LocalDate.of(2023, 11, 5), "ASIGNADO", "Oficina Contabilidad - 2A", emp2));

        activoRepository.save(buildActivo("TAB-001", "iPad Pro 12.9\"",
                new BigDecimal("4500.00"), new BigDecimal("3800.00"),
                LocalDate.of(2024, 1, 20), "ASIGNADO", "Oficina Diseño - 1C", emp3));

        activoRepository.save(buildActivo("IMP-001", "Impresora HP LaserJet Pro",
                new BigDecimal("2200.00"), new BigDecimal("2200.00"),
                LocalDate.of(2025, 2, 1), "DISPONIBLE", "Bodega Principal", null));

        activoRepository.save(buildActivo("PRY-001", "Proyector Epson PowerLite",
                new BigDecimal("5000.00"), new BigDecimal("4500.00"),
                LocalDate.of(2024, 6, 15), "DISPONIBLE", "Sala de Reuniones A", null));

        activoRepository.save(buildActivo("SRV-001", "Servidor Dell PowerEdge R740",
                new BigDecimal("45000.00"), new BigDecimal("42000.00"),
                LocalDate.of(2023, 8, 1), "DISPONIBLE", "Data Center - Rack 3", null));

        activoRepository.save(buildActivo("CAM-001", "Cámara Canon EOS R5",
                new BigDecimal("9800.00"), new BigDecimal("0.00"),
                LocalDate.of(2021, 5, 10), "BAJA", "Bodega de Bajas", null));
    }

    private User buildUser(String username, String password, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role);
        return u;
    }

    private Ubicacion buildUbicacion(String codigo, String nombre, String direccion, String descripcion) {
        Ubicacion u = new Ubicacion();
        u.setCodigo(codigo);
        u.setNombre(nombre);
        u.setDireccion(direccion);
        u.setDescripcion(descripcion);
        return u;
    }

    private Empleado buildEmpleado(String codigo, String nombre, String apellido,
                                   String departamento, String cargo, Ubicacion ubicacion) {
        Empleado e = new Empleado();
        e.setCodigo(codigo);
        e.setNombre(nombre);
        e.setApellido(apellido);
        e.setDepartamento(departamento);
        e.setCargo(cargo);
        e.setUbicacion(ubicacion);
        return e;
    }

    private Activo buildActivo(String codigo, String descripcion,
                               BigDecimal valorAdq, BigDecimal valorActual,
                               LocalDate fecha, String estado,
                               String ubicacion, Empleado empleado) {
        Activo a = new Activo();
        a.setCodigo(codigo);
        a.setDescripcion(descripcion);
        a.setValorAdquisicion(valorAdq);
        a.setValorActual(valorActual);
        a.setFechaAdquisicion(fecha);
        a.setEstado(estado);
        a.setUbicacionActual(ubicacion);
        a.setEmpleado(empleado);
        return a;
    }
}
