package iTicket.Douglas.Comentarios.Controller;

import iTicket.Douglas.Articulos.DTO.ArticuloDTO;
import iTicket.Douglas.Comentarios.DTO.ComentarioDTO;
import iTicket.Douglas.Comentarios.Service.ComentarioService;
import iTicket.Douglas.Modelos.Controller.ModeloController;
import iTicket.Douglas.Modelos.DTO.ModeloDTO;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {
    private final ComentarioService service;

    public ComentarioController(ComentarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ComentarioDTO>> guardar(@RequestBody @Valid ComentarioDTO json){
        try {
            ComentarioDTO dto = service.nuevoComentario(json);
            if (dto != null){
                log.info("Nuevo comentario registrado");
                ApiResponse<ComentarioDTO> respuestaExito = new ApiResponse<>(true, "Datos registrados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de inserción fallida "+ json);
            ApiResponse<ComentarioDTO> respuestaFallida = new ApiResponse<>(false, "No se pudo registrar el artículo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado al registrar el comentario");
            e.printStackTrace();
            ApiResponse<ComentarioDTO> respuestaFallida = new ApiResponse<>(false, "El proceso presentó un fallo inesperado. Contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ComentarioDTO>>> obtenerTodo(){
        try {
            List<ComentarioDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de modelos consultados");
                ApiResponse<List<ComentarioDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<ComentarioDTO>> respuestaNoEncontrada = new ApiResponse<>(true, "Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("No se pudieron obtener los datos de los comentarios");
            e.printStackTrace();
            ApiResponse<List<ComentarioDTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos de los Comentarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComentarioDTO>> buscarPorId(@PathVariable Long id){
        try {
            ComentarioDTO dto = service.buscarComentarioPorId(id);
            if (dto != null){
                log.info("Se obtuvieron los datos del comentario con id " + id);
                ApiResponse<ComentarioDTO> respuestaExitosa = new ApiResponse<>(true, "Se obtuvieron los datos del comentario con id " + id, dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("Comentario no encontrado con id: " + id);
            ApiResponse<ComentarioDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Comentario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico al obtener comentario con id " + id);
            e.printStackTrace();
            ApiResponse<ComentarioDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos del comentario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ComentarioDTO>> actualizar(@PathVariable Long id, @RequestBody @Valid ComentarioDTO dto){
        try {
            ComentarioDTO data = service.actualizar(id, dto);
            if (data != null){
                log.info("Se logró actualizar el comentario con id "+id);
                ApiResponse<ComentarioDTO> respuestaExito = new ApiResponse<>(true, "Se logró actualizar el comentario con id "+id, data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("El comentario con id " + id + " no fue actualizado");
            ApiResponse<ComentarioDTO> respuestaNoCompletada = new ApiResponse<>(false, "Proceso no completado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico en la actualización de comentario con id: " + id);
            e.printStackTrace();
            ApiResponse<ComentarioDTO> respuestaError = new ApiResponse<>(false, "No se pudo actualizar el comentario seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarData(id);
            if (respuesta){
                log.info("Se logró eliminar el comentario con id "+id);
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "El comentario con ID: " + id + " ha sido eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.warn("No se pudo eliminar el comentario con id" + id);
            ApiResponse<Void> noEncontrado = new ApiResponse<>(false, "El comentario con ID: " + id + " no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noEncontrado);
        } catch (Exception e) {
            log.error("Error crítico en la eliminación de comentario con id " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar el comentario seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
