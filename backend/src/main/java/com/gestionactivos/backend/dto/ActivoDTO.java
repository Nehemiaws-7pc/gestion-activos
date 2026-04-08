package com.gestionactivos.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActivoDTO(
        Long id,
        String codigo,
        String descripcion,
        BigDecimal valorAdquisicion,
        LocalDate fechaAdquisicion,
        BigDecimal valorActual,
        String estado,
        String ubicacionActual,
        Long empleadoId,
        String empleadoCodigo,
        String empleadoNombre
) {}
