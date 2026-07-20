package iTicket.Douglas.Modelos.Controller;

import iTicket.Douglas.Modelos.DTO.ModeloDTO;
import iTicket.Douglas.Modelos.Service.ModeloService;
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
@RequestMapping("api/modelos")
@RequiredArgsConstructor
public class ModeloController {

    private final ModeloService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ModeloDTO>> nuevoModelo(@Valid @RequestBody ModeloDTO json) {
        try {
            ModeloDTO dto = service.nuevoModelo(json);
            if (dto != null) {
                log.info("Nuevo modelo registrado " + dto);
                ApiResponse<ModeloDTO> respuestaExito = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de insercion fallida " + json);
            ApiResponse<ModeloDTO> respuestaFallida = new ApiResponse<>(false, "Intento de insercion fallida");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado contacte con el administrador");
            e.printStackTrace();
            ApiResponse<ModeloDTO> respuestaFallida = new ApiResponse<>(false, "El proceso presentó un fallo inesperado contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModeloDTO>>> obtenerDatos() {
        try {
            List<ModeloDTO> lista = service.obtenerTodo();
            log.info("Datos de modelos consultados");
            ApiResponse<List<ModeloDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
            return ResponseEntity.ok(respuestaExito);
        } catch (Exception e) {
            log.error("No se pudieron obtener los datos de los modelos");
            e.printStackTrace();
            ApiResponse<List<ModeloDTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos de los modelos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModeloDTO>> obtenerDatosId(@PathVariable Long id) {
        try {
            ModeloDTO dto = service.obtenerPorId(id);
            if (dto != null) {
                log.info("Se obtuvieron los datos del modelo con id " + id);
                ApiResponse<ModeloDTO> respuestaExitosa = new ApiResponse<>(true, "Se obtuvieron los datos del modelo con id " + id, dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("Modelo no encontrado con id: " + id);
            ApiResponse<ModeloDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Modelo no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico al obtener de modelo con id " + id);
            e.printStackTrace();
            ApiResponse<ModeloDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos del modelo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModeloDTO>> actualizarModelo(@PathVariable Long id, @Valid @RequestBody ModeloDTO dto) {
        try {
            ModeloDTO data = service.actualizarData(id, dto);
            if (data != null) {
                log.info("Se logró actualizar el modelo con id "+id);
                ApiResponse<ModeloDTO> respuestaExito = new ApiResponse<>(true, "Se logró actualizar el modelo con id "+id, data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("El modelo con id " + id + " no fue actualizado");
            ApiResponse<ModeloDTO> respuestaNoCompletada = new ApiResponse<>(false, "Proceso no completado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico en la actualización de modelo con id: " + id);
            e.printStackTrace();
            ApiResponse<ModeloDTO> respuestaError = new ApiResponse<>(false, "No se pudo actualizar el modelo seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id) {
        try {
            boolean respuesta = service.eliminarModelo(id);
            if (respuesta) {
                log.info("Se logró eliminar el modelo con id "+id);
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "El modelo con ID: " + id + " ha sido eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.warn("No se pudo completar el proceso de eliminar, id: " + id);
            ApiResponse<Void> noEncontrado = new ApiResponse<>(false, "El modelo con ID: " + id + " no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noEncontrado);
        } catch (Exception e) {
            log.error("Error crítico en la eliminación de modelo con id: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar el modelo seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

}
