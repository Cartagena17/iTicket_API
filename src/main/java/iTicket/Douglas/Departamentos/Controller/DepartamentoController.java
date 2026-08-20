package iTicket.Douglas.Departamentos.Controller;

import iTicket.Douglas.Departamentos.DTO.DepartamentoDTO;
import iTicket.Douglas.Departamentos.Service.DepartamentoService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
public class DepartamentoController {

    private final DepartamentoService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DepartamentoDTO>> nuevoDepartamento(@Valid @RequestBody DepartamentoDTO json) {
        DepartamentoDTO dto = service.nuevoDepartamento(json);
        log.info("Nuevo departamento registrado " + dto);
        ApiResponse<DepartamentoDTO> respuesta = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartamentoDTO>>> obtenerDatos() {
        List<DepartamentoDTO> lista = service.obetenerTodo();
        ApiResponse<List<DepartamentoDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartamentoDTO>> obtenerDatosId(@PathVariable Long id) {
        DepartamentoDTO dto = service.obtenerPorId(id);
        ApiResponse<DepartamentoDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del departamento con id " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartamentoDTO>> actualizarDepartamento(@PathVariable Long id, @Valid @RequestBody DepartamentoDTO dto) {
        DepartamentoDTO data = service.actualizar(id, dto);
        log.info("Se logró actualizar el departamento con id " + id);
        ApiResponse<DepartamentoDTO> respuesta = new ApiResponse<>(true, "Se logró actualizar el departamento con id " + id, data);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminar(id);
        if (eliminado) {
            log.info("Se logró eliminar el departamento con id " + id);
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Se logró eliminar el departamento con id " + id);
            return ResponseEntity.ok(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "No se logró eliminar el departamento con id " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @GetMapping("/asignables/{idUsuario}")
    public ResponseEntity<ApiResponse<List<DepartamentoDTO>>> obtenerAsignables(@PathVariable Long idUsuario) {
        List<DepartamentoDTO> lista = service.obtenerDepartamentosAsignables(idUsuario);
        log.info("Departamentos asignables consultados para usuario: " + idUsuario);
        ApiResponse<List<DepartamentoDTO>> respuesta = new ApiResponse<>(true, "Departamentos asignables encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }
}