package com.gestionactivos.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    private String descripcion;
    private BigDecimal valorAdquisicion;
    private LocalDate fechaAdquisicion;
    private BigDecimal valorActual;

    private String estado; // DISPONIBLE, ASIGNADO, BAJA

    private String ubicacionActual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;
}
