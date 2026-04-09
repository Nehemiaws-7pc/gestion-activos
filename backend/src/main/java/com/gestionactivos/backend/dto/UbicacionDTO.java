package com.gestionactivos.backend.dto;

public record UbicacionDTO(
        Long id,
        String codigo,
        String nombre,
        String direccion,
        String descripcion
) {}
