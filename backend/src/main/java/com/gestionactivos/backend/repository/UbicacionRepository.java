package com.gestionactivos.backend.repository;

import com.gestionactivos.backend.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
    Optional<Ubicacion> findByCodigo(String codigo);
}
