package iTicket.Douglas.MultimediaComentario.Controller;

import iTicket.Douglas.MultimediaComentario.DTO.MultimediaComentarioDTO;
import iTicket.Douglas.MultimediaComentario.Service.MultimediaComentarioService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/multimediaComentarios")
public class MultimediaComentarioController {

    private final MultimediaComentarioService service;

    public MultimediaComentarioController(MultimediaComentarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> nuevaMultimedia(@Valid @RequestBody MultimediaComentarioDTO json){

        try{
            MultimediaComentarioDTO dto = service.nuevaMultimedia(json);
            if (dto != null){
                log.info("Nueva Multimedia: " + dto);
                ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            }
            log.warn("Intento de insercion fallido: " + json);
            ApiResponse<MultimediaComentarioDTO> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presento fallas inesperadas. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @PostMapping(value = "/subir", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> subirMultimedia(@RequestParam("archivo") MultipartFile archivo, @RequestParam("idComentario") Long idComentario) {
        try {
            MultimediaComentarioDTO dto = service.subirMultimedia(archivo, idComentario);
            log.info("Multimedia subida y registrada para el comentario: " + idComentario);
            ApiResponse<MultimediaComentarioDTO> respuestaExito = new ApiResponse<>(true, "Multimedia subida correctamente", dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
        } catch (Exception e) {
            log.error("Error al subir multimedia para el comentario " + idComentario);
            e.printStackTrace();
            ApiResponse<MultimediaComentarioDTO> respuestaError = new ApiResponse<>(false, "No se pudo subir la multimedia");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping
    private ResponseEntity<ApiResponse<List<MultimediaComentarioDTO>>> obtenerDatos(){

        try {
            List<MultimediaComentarioDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de la Ubicacion consultados");
                ApiResponse<List<MultimediaComentarioDTO>> respuestaExito = new ApiResponse<>(true,"Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos No encontrados");
            ApiResponse<List<MultimediaComentarioDTO>> respuestaNoEncontrada = new ApiResponse<>(true,"Datos encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);

        } catch (Exception e) {
            log.error("El proceso presento errores inesperados. Consulte con un administrador");
            e.printStackTrace();
            ApiResponse<List<MultimediaComentarioDTO>> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> obtenerDatosPorId(@PathVariable Long id){

        try {
            MultimediaComentarioDTO dto = service.buscarPorId(id);
            if (dto != null){
                log.info("Se obtubieron los datos de la Multimedia del comentario" + dto);
                ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(true,"Se obtubieron los datos de la Multimedia del comentario mediante su ID: " + id, dto);
                return ResponseEntity.ok(respuesta);
            }
            log.info("Los datos de la Multimedia no se encontraron con el ID: " + id);
            ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(false,"Los datos de la Multimedia no se encontraron con el ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        } catch (Exception e) {
            log.error("Error critico al obtener la Multimedia con ID: " + id);
            e.printStackTrace(); // Muestra el lugar exacto de donde ocurrio el error en la ejecución
            ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(false, "Error al obtener el la Multimedia con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarMultimedia(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarData(id);
            if (respuesta){
                log.info("La Multimedia con ID: " + id + " Ya fue eliminado");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "La Multimedia con ID: " + id + "Ya fue eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body((respuestaExitosa));
            }
            log.info("La Multimedia con ID: " + id + " no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "La Multimedia con ID: " + id + " no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }
        catch (Exception e){
            log.error("Error critico al eliminar la Multimedia con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuesta = new ApiResponse<>(false, "Error al eliminar la Multimedia con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody MultimediaComentarioDTO dto){
        try{
            MultimediaComentarioDTO data = service.actualizar(id, dto);
            if (data != null){
                log.info("La Multimedia con ID: " + id + " ha sido actualizado");
                ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(true, "La Multimedia con ID: " + id + " ha sido actualizado", data);
                return ResponseEntity.ok(respuesta);
            }
            log.warn("No se pudo completar la actualización de la Multimedia con ID: " + id );
            ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la actualización la de Multimedia con ID: " + id );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error critico al actualizar la Multimedia con ID: " + id);
            e.printStackTrace();
            ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(false, "Error al actualizar la Mutimedia con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }
}
