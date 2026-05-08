package com.gestionactivos.backend.dto;

import java.math.BigDecimal;

public record BienAsignadoDTO(
        String codigoEmpleado,
        String nombreEmpleado,
        String codigoBien,
        String descripcionBien,
        BigDecimal costoBien
) {}
