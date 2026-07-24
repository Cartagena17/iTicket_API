package iTicket.Douglas.Comentarios.Controller;

import iTicket.Douglas.Comentarios.DTO.ComentarioDTO;
import iTicket.Douglas.Comentarios.Service.ComentarioService;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Ubicaciones.DTO.UbicacionDTO;
import iTicket.Douglas.Ubicaciones.Service.UbicacionService;
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
    public ResponseEntity<ApiResponse<ComentarioDTO>> nuevoComentario(@Valid @RequestBody ComentarioDTO json){

        try{
            ComentarioDTO dto = service.nuevoComentario(json);
            if (dto != null){
                log.info("Nuevo comentario: " + dto);
                ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            }
            log.warn("Intento de insercion fallido: " + json);
            ApiResponse<ComentarioDTO> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presento fallas inesperadas. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @GetMapping
    private ResponseEntity<ApiResponse<List<ComentarioDTO>>> obtenerDatos(){

        try {
            List<ComentarioDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de los Comentarios consultados");
                ApiResponse<List<ComentarioDTO>> respuestaExito = new ApiResponse<>(true,"Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos No encontrados");
            ApiResponse<List<ComentarioDTO>> respuestaNoEncontrada = new ApiResponse<>(true,"Datos encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);

        } catch (Exception e) {
            log.error("El proceso presento errores inesperados. Consulte con un administrador");
            e.printStackTrace();
            ApiResponse<List<ComentarioDTO>> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComentarioDTO>> obtenerDatosPorId(@PathVariable Long id){

        try {
            ComentarioDTO dto = service.buscarComentarioPorId(id);
            if (dto != null){
                log.info("Se obtubieron los datos de el comentario" + dto);
                ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(true,"Se obtubieron los datos de el comentario mediante su ID: " + id, dto);
                return ResponseEntity.ok(respuesta);
            }
            log.info("Los datos de el comentario no se encontraron con el ID: " + id);
            ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(false,"Los datos de el Comentario no se encontraron con el ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        } catch (Exception e) {
            log.error("Error critico al obtener el Comentario con ID: " + id);
            e.printStackTrace(); // Muestra el lugar exacto de donde ocurrio el error en la ejecución
            ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(false, "Error al obtener el Comentario con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarComentario(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarData(id);
            if (respuesta){
                log.info("El Comentario con ID: " + id + " Ya fue eliminado");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "El Comecntario con ID: " + id + "Ya fue eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body((respuestaExitosa));
            }
            log.info("El Comentario con ID: " + id + " no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "El Comentario con ID: " + id + " no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }
        catch (Exception e){
            log.error("Error critico al eliminar el Comentario con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuesta = new ApiResponse<>(false, "Error al eliminar el Comentario con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ComentarioDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody ComentarioDTO dto){
        try{
            ComentarioDTO data = service.actualizar(id, dto);
            if (data != null){
                log.info("El Comentario con ID: " + id + " ha sido actualizado");
                ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(true, "El Comentario con ID: " + id + " ha sido actualizado", data);
                return ResponseEntity.ok(respuesta);
            }
            log.warn("No se pudo completar la actualización de el Comentario con ID: " + id );
            ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la actualización de el Comentario con ID: " + id );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error critico al actualizar el Comentario con ID: " + id);
            e.printStackTrace();
            ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(false, "Error al actualizar el Comentario con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }
}
