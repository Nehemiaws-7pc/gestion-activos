package com.stressservice.dto;

public record StressResult(
        int solicitados,
        int creados,
        int omitidos,
        long duracionMs,
        double promedioMsPorRegistro
) {}
