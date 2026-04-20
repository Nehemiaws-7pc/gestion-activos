package com.gestionactivos.backend.controller;

import com.gestionactivos.backend.dto.ActivoDTO;
import com.gestionactivos.backend.dto.ActivoUpdateDTO;
import com.gestionactivos.backend.service.ActivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.gestionactivos.backend.util.QrUtil;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/activos")
@RequiredArgsConstructor
public class ActivoController {

    private final ActivoService service;
    private final QrUtil qrUtil;

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

    @GetMapping(value = "/{codigo}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generarQr(@PathVariable String codigo) {
        try {
            // Verifica que el activo existe antes de generar el QR
            service.buscarPorCodigo(codigo);
            byte[] qr = qrUtil.generarQr("ACT:" + codigo, 300);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qr);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<ActivoDTO>> listarPaginado(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<ActivoDTO> actualizar(@PathVariable Long id, @RequestBody ActivoUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
}
