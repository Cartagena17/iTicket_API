package iTicket.Douglas.DetalleTA.Controller;

import iTicket.Douglas.DetalleTA.DTO.DetalleTADTO;
import iTicket.Douglas.DetalleTA.Service.DetalleTAService;
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
@RequestMapping("/api/detalleta")
@RequiredArgsConstructor
public class DetalleTAController {

    private final DetalleTAService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DetalleTADTO>> nuevoDetalle(@Valid @RequestBody DetalleTADTO json) {
        DetalleTADTO dto = service.nuevoDetalle(json);
        log.info("Nuevo detalle TA registrado " + dto);
        ApiResponse<DetalleTADTO> respuesta = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleTADTO>>> obtenerDatos() {
        List<DetalleTADTO> lista = service.obtenerTodo();
        log.info("Datos de detalle TA consultados");
        ApiResponse<List<DetalleTADTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleTADTO>> obtenerDatosId(@PathVariable Long id) {
        DetalleTADTO dto = service.obtenerPorId(id);
        log.info("Obtención correcta de detalle TA con id " + id);
        ApiResponse<DetalleTADTO> respuesta = new ApiResponse<>(true, "Proceso completado", dto);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleTADTO>> actualizarDetalle(@PathVariable Long id, @Valid @RequestBody DetalleTADTO dto) {
        DetalleTADTO data = service.actualizarData(id, dto);
        log.info("El detalle TA con id " + id + " fue actualizado");
        ApiResponse<DetalleTADTO> respuesta = new ApiResponse<>(true, "Proceso completado", data);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id) {
        boolean eliminado = service.eliminarDetalle(id);
        if (eliminado) {
            log.info("El detalle TA con ID: " + id + " ha sido eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "El detalle TA con ID: " + id + " ha sido eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "El detalle TA con ID: " + id + " no se encontró");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}