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
@RequestMapping("/api/detalleta")
@RequiredArgsConstructor
public class DetalleTAController {

    private final DetalleTAService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DetalleTADTO>> nuevoDetalle(@Valid @RequestBody DetalleTADTO json) {
        try {
            DetalleTADTO dto = service.nuevoDetalle(json);
            if (dto != null) {
                log.info("Nuevo detalle TA registrado " + dto);
                ApiResponse<DetalleTADTO> respuestaExito = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de insercion fallida " + json);
            ApiResponse<DetalleTADTO> respuestaFallida = new ApiResponse<>(false, "Intento de insercion fallida");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado contacte con el administrador");
            e.printStackTrace();
            ApiResponse<DetalleTADTO> respuestaFallida = new ApiResponse<>(false, "El proceso presentó un fallo inesperado contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleTADTO>>> obtenerDatos() {
        try {
            List<DetalleTADTO> lista = service.obtenerTodo();
            log.info("Datos de detalle TA consultados");
            ApiResponse<List<DetalleTADTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
            return ResponseEntity.ok(respuestaExito);
        } catch (Exception e) {
            log.error("No se pudieron obtener los datos de detalle TA");
            e.printStackTrace();
            ApiResponse<List<DetalleTADTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleTADTO>> obtenerDatosId(@PathVariable Long id) {
        try {
            DetalleTADTO dto = service.obtenerPorId(id);
            if (dto != null) {
                log.info("Obtencion correcta de detalle TA con id " + id);
                ApiResponse<DetalleTADTO> respuestaExitosa = new ApiResponse<>(true, "Proceso completado", dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("Detalle TA no encontrado, id: " + id);
            ApiResponse<DetalleTADTO> respuestaNoEncontrada = new ApiResponse<>(false, "Detalle TA no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico en la obtención de detalle TA con id: " + id);
            e.printStackTrace();
            ApiResponse<DetalleTADTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleTADTO>> actualizarDetalle(@PathVariable Long id, @Valid @RequestBody DetalleTADTO dto) {
        try {
            DetalleTADTO data = service.actualizarData(id, dto);
            if (data != null) {
                log.info("El detalle TA con id " + id + " fue actualizado");
                ApiResponse<DetalleTADTO> respuestaExito = new ApiResponse<>(true, "Proceso completado", data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("El detalle TA con id " + id + " NO fue actualizado");
            ApiResponse<DetalleTADTO> respuestaNoCompletada = new ApiResponse<>(false, "Proceso no completado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico en la actualización de detalle TA con id: " + id);
            e.printStackTrace();
            ApiResponse<DetalleTADTO> respuestaError = new ApiResponse<>(false, "No se pudo actualizar el detalle seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id) {
        try {
            boolean respuesta = service.eliminarDetalle(id);
            if (respuesta) {
                log.info("El detalle TA con ID: " + id + " ha sido eliminado");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "El detalle TA con ID: " + id + " ha sido eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.warn("No se pudo completar el proceso de eliminar, id: " + id);
            ApiResponse<Void> noEncontrado = new ApiResponse<>(false, "El detalle TA con ID: " + id + " no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noEncontrado);
        } catch (Exception e) {
            log.error("Error crítico en la eliminación de detalle TA con id: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar el detalle seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
