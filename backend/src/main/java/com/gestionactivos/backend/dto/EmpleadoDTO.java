package com.gestionactivos.backend.dto;

public record EmpleadoDTO(
        Long id,
        String codigo,
        String nombre,
        String apellido,
        String departamento,
        String cargo
) {}
