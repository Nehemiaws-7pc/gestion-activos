package com.gestionactivos.backend.repository;

import com.gestionactivos.backend.model.Activo;
import com.gestionactivos.backend.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivoRepository extends JpaRepository<Activo, Long> {
    Optional<Activo> findByCodigo(String codigo);
    List<Activo> findByEmpleado(Empleado empleado);
}
