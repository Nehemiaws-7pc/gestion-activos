package com.gestionactivos.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ubicaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String direccion;
    private String descripcion;
}
