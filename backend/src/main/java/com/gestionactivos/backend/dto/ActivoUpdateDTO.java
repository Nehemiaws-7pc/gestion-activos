package com.gestionactivos.backend.dto;

import java.math.BigDecimal;

public record ActivoUpdateDTO(
        String descripcion,
        BigDecimal valorActual,
        String estado,
        String empleadoCodigo
) {}
