package com.gestionactivos.backend.controller;

import com.gestionactivos.backend.dto.UbicacionDTO;
import com.gestionactivos.backend.dto.UbicacionDetalleDTO;
import com.gestionactivos.backend.service.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
public class UbicacionController {

    private final UbicacionService service;

    @GetMapping
    public ResponseEntity<List<UbicacionDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PostMapping
    public ResponseEntity<UbicacionDTO> crear(@RequestBody UbicacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbicacionDTO> actualizar(@PathVariable Long id, @RequestBody UbicacionDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<UbicacionDetalleDTO> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerDetalle(id));
    }
}
