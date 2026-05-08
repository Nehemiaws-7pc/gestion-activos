package com.gestionactivos.backend.repository;

import com.gestionactivos.backend.dto.BienAsignadoDTO;
import com.gestionactivos.backend.model.Activo;
import com.gestionactivos.backend.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivoRepository extends JpaRepository<Activo, Long> {
    Optional<Activo> findByCodigo(String codigo);
    List<Activo> findByEmpleado(Empleado empleado);

    @Query("""
        SELECT new com.gestionactivos.backend.dto.BienAsignadoDTO(
            e.codigo,
            CONCAT(e.nombre, ' ', COALESCE(e.apellido, '')),
            a.codigo,
            a.descripcion,
            a.valorAdquisicion
        )
        FROM Activo a
        JOIN a.empleado e
        WHERE e.codigo = :codigo AND a.estado = 'ASIGNADO'
        ORDER BY a.codigo
    """)
    List<BienAsignadoDTO> findBienesAsignadosByEmpleadoCodigo(@Param("codigo") String codigo);
}
