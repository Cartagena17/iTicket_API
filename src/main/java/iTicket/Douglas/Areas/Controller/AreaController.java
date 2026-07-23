package iTicket.Douglas.Areas.Controller;

import iTicket.Douglas.Areas.DTO.AreaDTO;
import iTicket.Douglas.Areas.Service.AreaService;
import iTicket.Douglas.Marcas.DTO.MarcaDTO;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.geom.Area;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService service;

    @PostMapping
    public ResponseEntity<ApiResponse<AreaDTO>> nuevaArea(@Valid @RequestBody AreaDTO json) {
        try {
            AreaDTO dto = service.nuevaArea(json);
            if (dto != null){
                log.info("Nuevo area registrado +"+dto);
                ApiResponse<AreaDTO> respuestaExito = new ApiResponse<>(true, "Datos registrados exitosamente" ,dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de inserción fallida: "+json);
            ApiResponse<AreaDTO> respuestaFallida = new ApiResponse<>(false,"Intento de inserción fallida: "+json);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);

        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado, consulte con el administrador");
            e.printStackTrace();
            ApiResponse<AreaDTO> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AreaDTO>>> obetenerAreas() {
        try {
            List<AreaDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de areas consultados");
                ApiResponse<List<AreaDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<AreaDTO>> respuestaNoEncontrada = new ApiResponse<>(true, "Datos encontrados");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado, consulte con el administrador");
            e.printStackTrace();
            ApiResponse<List<AreaDTO>> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @GetMapping ("/{id}")
    public ResponseEntity<ApiResponse<AreaDTO>> obtenerAreaPorId(@PathVariable Long id) {
        try {
            AreaDTO dto = service.obtenerPorId(id);
            if (dto != null) {
                log.info("Área consultada: " + dto);
                ApiResponse<AreaDTO> respuestaExito = new ApiResponse<>(true, "Área encontrada", dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Área no encontrada, id: " + id);
            ApiResponse<AreaDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Área no encontrada", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado, consulte con el administrador");
            e.printStackTrace();
            ApiResponse<AreaDTO> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AreaDTO>> editar(@PathVariable Long id, @Valid @RequestBody AreaDTO json) {
        try {
            AreaDTO dto = service.editarArea(id, json);
            if (dto != null) {
                log.info("Área actualizada: " + dto);
                ApiResponse<AreaDTO> respuestaExito = new ApiResponse<>(true, "Área actualizada correctamente", dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Intento de actualización fallida, id no encontrado: " + id);
            ApiResponse<AreaDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Área no encontrada", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado, consulte con el administrador");
            e.printStackTrace();
            ApiResponse<AreaDTO> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminarArea(id);
            if (eliminado) {
                log.info("Área eliminada, id: " + id);
                ApiResponse<Void> respuestaExito = new ApiResponse<>(true, "Área eliminada correctamente", null);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExito);
            }
            log.warn("Intento de eliminación fallida, id no encontrado: " + id);
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Área no encontrada", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado, consulte con el administrador");
            e.printStackTrace();
            ApiResponse<Void> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

}
