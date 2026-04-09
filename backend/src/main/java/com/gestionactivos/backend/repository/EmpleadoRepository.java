package com.gestionactivos.backend.repository;

import com.gestionactivos.backend.model.Empleado;
import com.gestionactivos.backend.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByCodigo(String codigo);
    List<Empleado> findByUbicacion(Ubicacion ubicacion);
}
