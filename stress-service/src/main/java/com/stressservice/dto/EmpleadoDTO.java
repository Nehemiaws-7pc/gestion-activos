package com.stressservice.dto;

public record EmpleadoDTO(
        Long id,
        String codigo,
        String nombre,
        String apellido,
        String departamento,
        String cargo,
        Long ubicacionId,
        String ubicacionNombre
) {}
