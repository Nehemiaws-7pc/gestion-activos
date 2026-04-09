package com.gestionactivos.backend.service;

import com.gestionactivos.backend.dto.ActivoDTO;
import com.gestionactivos.backend.dto.EmpleadoDTO;
import com.gestionactivos.backend.model.Empleado;
import com.gestionactivos.backend.model.Ubicacion;
import com.gestionactivos.backend.repository.ActivoRepository;
import com.gestionactivos.backend.repository.EmpleadoRepository;
import com.gestionactivos.backend.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final ActivoRepository activoRepository;
    private final ActivoService activoService;
    private final UbicacionRepository ubicacionRepository;

    public List<EmpleadoDTO> listarTodos() {
        return empleadoRepository.findAll().stream().map(this::toDTO).toList();
    }

    public EmpleadoDTO buscarPorCodigo(String codigo) {
        return toDTO(findOrThrow(codigo));
    }

    public List<ActivoDTO> getActivosDeEmpleado(String codigo) {
        Empleado emp = findOrThrow(codigo);
        return activoRepository.findByEmpleado(emp).stream()
                .map(activoService::toDTO)
                .toList();
    }

    public EmpleadoDTO crear(EmpleadoDTO dto) {
        if (empleadoRepository.findByCodigo(dto.codigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un empleado con código: " + dto.codigo());
        }
        Empleado e = new Empleado();
        mapFields(e, dto);
        return toDTO(empleadoRepository.save(e));
    }

    public EmpleadoDTO actualizar(Long id, EmpleadoDTO dto) {
        Empleado e = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        // Si cambia el código, verificar que no exista ya
        if (!e.getCodigo().equals(dto.codigo()) &&
                empleadoRepository.findByCodigo(dto.codigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un empleado con código: " + dto.codigo());
        }
        mapFields(e, dto);
        return toDTO(empleadoRepository.save(e));
    }

    public void eliminar(Long id) {
        Empleado e = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        long activosAsignados = activoRepository.findByEmpleado(e).stream()
                .filter(a -> "ASIGNADO".equals(a.getEstado()))
                .count();

        if (activosAsignados > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El empleado tiene " + activosAsignados + " activo(s) asignado(s). Desasígnalos primero.");
        }
        empleadoRepository.delete(e);
    }

    private void mapFields(Empleado e, EmpleadoDTO dto) {
        e.setCodigo(dto.codigo());
        e.setNombre(dto.nombre());
        e.setApellido(dto.apellido());
        e.setDepartamento(dto.departamento());
        e.setCargo(dto.cargo());

        if (dto.ubicacionId() != null) {
            Ubicacion ub = ubicacionRepository.findById(dto.ubicacionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ubicación no encontrada"));
            e.setUbicacion(ub);
        } else {
            e.setUbicacion(null);
        }
    }

    public EmpleadoDTO toDTO(Empleado e) {
        Ubicacion ub = e.getUbicacion();
        return new EmpleadoDTO(e.getId(), e.getCodigo(), e.getNombre(),
                e.getApellido(), e.getDepartamento(), e.getCargo(),
                ub != null ? ub.getId() : null,
                ub != null ? ub.getNombre() : null);
    }

    private Empleado findOrThrow(String codigo) {
        return empleadoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Empleado no encontrado: " + codigo));
    }
}
