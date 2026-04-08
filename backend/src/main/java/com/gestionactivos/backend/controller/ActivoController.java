package com.gestionactivos.backend.controller;

import com.gestionactivos.backend.dto.ActivoDTO;
import com.gestionactivos.backend.dto.ActivoUpdateDTO;
import com.gestionactivos.backend.service.ActivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activos")
@RequiredArgsConstructor
public class ActivoController {

    private final ActivoService service;

    @PostMapping
    public ResponseEntity<ActivoDTO> crear(@RequestBody ActivoDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<ActivoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<ActivoDTO> buscar(@RequestParam String codigo) {
        return ResponseEntity.ok(service.buscarPorCodigo(codigo));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<ActivoDTO> actualizar(@PathVariable Long id, @RequestBody ActivoUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
}
