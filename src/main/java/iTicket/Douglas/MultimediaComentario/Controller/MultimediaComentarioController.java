package iTicket.Douglas.MultimediaComentario.Controller;

import iTicket.Douglas.Modelos.DTO.ModeloDTO;
import iTicket.Douglas.MultimediaComentario.DTO.MultimediaComentarioDTO;
import iTicket.Douglas.MultimediaComentario.Service.MultimediaComentarioService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/multimediaComentarios")
public class MultimediaComentarioController {

    private final MultimediaComentarioService service;

    public MultimediaComentarioController(MultimediaComentarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> guardar(@RequestBody @Valid MultimediaComentarioDTO json){
        try {
            MultimediaComentarioDTO dto = service.nuevaMultimedia(json);
            if (dto != null) {
                log.info("Multimemedia comentario registrado " + dto);
                ApiResponse<MultimediaComentarioDTO> respuestaExito = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de insercion fallida " + json);
            ApiResponse<MultimediaComentarioDTO> respuestaFallida = new ApiResponse<>(false, "Intento de insercion fallida");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado contacte con el administrador");
            e.printStackTrace();
            ApiResponse<MultimediaComentarioDTO> respuestaFallida = new ApiResponse<>(false, "El proceso presentó un fallo inesperado contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MultimediaComentarioDTO>>> obtenerTodo(){
        try {
            List<MultimediaComentarioDTO> lista = service.obtenerTodo();
            if (lista != null) {
                log.info("Datos de multedia comentarios consultados");
                ApiResponse<List<MultimediaComentarioDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<MultimediaComentarioDTO>> respuestaNoEncontrada = new ApiResponse<>(true, "Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("No se pudieron obtener los datos de multimedia comentarios");
            e.printStackTrace();
            ApiResponse<List<MultimediaComentarioDTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos de multimedia comentarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> buscarPorId(@PathVariable Long id){
        try {
            MultimediaComentarioDTO dto = service.buscarPorId(id);
            if (dto != null) {
                log.info("Se obtuvieron los datos de multimedia comentarios con id " + id);
                ApiResponse<MultimediaComentarioDTO> respuestaExitosa = new ApiResponse<>(true, "Se obtuvieron los datos de multimedia comentarios con id " + id, dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se encontro multimedia comentario con id " + id);
            ApiResponse<MultimediaComentarioDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Multimedia comentario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico al obtener multimedia comentario con id " + id);
            e.printStackTrace();
            ApiResponse<MultimediaComentarioDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos de multimedia comentario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> actualizar(@PathVariable Long id, @RequestBody @Valid MultimediaComentarioDTO dto){
        try {
            MultimediaComentarioDTO data = service.actualizar(id, dto);
            if (data != null) {
                log.info("Se logró actualizar multimedia comentario con id "+id);
                ApiResponse<MultimediaComentarioDTO> respuestaExito = new ApiResponse<>(true, "Se logró actualizar multimedia comentario con id "+id, data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Multimedia comentarios con id " + id + " no fue actualizado");
            ApiResponse<MultimediaComentarioDTO> respuestaNoCompletada = new ApiResponse<>(false, "Proceso no completado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico en la actualización multimedia comentario con id " + id);
            e.printStackTrace();
            ApiResponse<MultimediaComentarioDTO> respuestaError = new ApiResponse<>(false, "No se pudo actualizar multimedia comentario seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarData(id);
            if (respuesta) {
                log.info("Se logró eliminar multimedia comentario con id "+id);
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "Multimedia comentario con ID: " + id + " ha sido eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.warn("No se pudo completar el proceso de eliminar id " + id);
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "El multimedia comentario con id " + id + " no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico en la eliminación multimedia comentario con id " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar lo seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
