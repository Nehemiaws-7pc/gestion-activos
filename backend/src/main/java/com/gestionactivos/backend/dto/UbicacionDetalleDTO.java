package com.gestionactivos.backend.dto;

import java.util.List;

public record UbicacionDetalleDTO(
        Long id,
        String codigo,
        String nombre,
        String direccion,
        String descripcion,
        List<EmpleadoConActivosDTO> empleados
) {
    public record EmpleadoConActivosDTO(
            Long id,
            String codigo,
            String nombre,
            String apellido,
            String departamento,
            String cargo,
            List<ActivoDTO> activos
    ) {}
}
