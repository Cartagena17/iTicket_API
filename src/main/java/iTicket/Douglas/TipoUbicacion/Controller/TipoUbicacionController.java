package iTicket.Douglas.TipoUbicacion.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.TipoUbicacion.DTO.TipoUbicacionDTO;
import iTicket.Douglas.TipoUbicacion.Service.TipoUbicacionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tipoubicacion")
public class TipoUbicacionController {

    private final TipoUbicacionService service;

    public TipoUbicacionController(TipoUbicacionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TipoUbicacionDTO>> nuevoTipoUbicacion(@Valid @RequestBody TipoUbicacionDTO json){

        try{
            TipoUbicacionDTO dto = service.nuevoTipoUbicacion(json);
            if (dto != null){
                log.info("Nuevo Tipo Ubicacion: " + dto);
                ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            }
            log.warn("Intento de insercion fallido: " + json);
            ApiResponse<TipoUbicacionDTO> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presento fallas inesperadas. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @GetMapping
    private ResponseEntity<ApiResponse<List<TipoUbicacionDTO>>> obtenerDatos(){

        try {
            List<TipoUbicacionDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de Tipo Ubicacion consultados");
                ApiResponse<List<TipoUbicacionDTO>> respuestaExito = new ApiResponse<>(true,"Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos No encontrados");
            ApiResponse<List<TipoUbicacionDTO>> respuestaNoEncontrada = new ApiResponse<>(true,"Datos encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);

        } catch (Exception e) {
            log.error("El proceso presento errores inesperados. Consulte con un administrador");
            e.printStackTrace();
            ApiResponse<List<TipoUbicacionDTO>> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoUbicacionDTO>> obtenerDatosPorId(@PathVariable Long id){

        try {
            TipoUbicacionDTO dto = service.buscarNombreTipoUbicacion(id);
            if (dto != null){
                log.info("Se obtubieron los datos de Tipo Ubicacion" + dto);
                ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(true,"Se obtubieron los datos de Tipo Ubicacion mediante su ID: " + id, dto);
                return ResponseEntity.ok(respuesta);
            }
            log.info("Los datos de Tipo Ubicacion no se encontraron con el ID: " + id);
            ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(false,"Los datos de Tipo Ubicacion no se encontraron con el ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        } catch (Exception e) {
            log.error("Error critico al obtener el Tipo Ubicacion con ID: " + id);
            e.printStackTrace(); // Muestra el lugar exacto de donde ocurrio el error en la ejecución
            ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(false, "Error al obtener el Tipo Ubicacion con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTipoUbicacion(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarData(id);
            if (respuesta){
                log.info("Tipo Ubicacion con ID: " + id + " Ya fue eliminado");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "Tipo Ubicacion con ID: " + id + "Ya fue eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body((respuestaExitosa));
            }
            log.info("Tipo Ubicacion con ID: " + id + " no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Tipo Ubicacion con ID: " + id + " no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }
        catch (Exception e){
            log.error("Error critico al eliminar el Tipo de Ubicacion con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuesta = new ApiResponse<>(false, "Error al eliminar el Tipo de Ubicacion con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoUbicacionDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody TipoUbicacionDTO dto){
        try{
            TipoUbicacionDTO data = service.actualizar(id, dto);
            if (data != null){
                log.info("Tipo Ubicacion con ID: " + id + " ha sido actualizado");
                ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(true, "Tipo Ubicacion con ID: " + id + " ha sido actualizado", data);
                return ResponseEntity.ok(respuesta);
            }
            log.warn("No se pudo completar la actualización del Tipo de Ubicacion con ID: " + id );
            ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la actualización del Tipo de Ubicacion con ID: " + id );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error critico al actualizar el Tipo de Ubicacion con ID: " + id);
            e.printStackTrace(); // Muestra el lugar exacto de donde ocurrio el error en la ejecución
            ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(false, "Error al actualizar el Tipo de Ubicacion con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @GetMapping("/nombreTipoUbicacion/{nombreTipoUbicacion}")
    public ResponseEntity<ApiResponse<TipoUbicacionDTO>> buscarPorNombreTipoUbicacion(@PathVariable String nombreTipoUbicacion){
        try{

            TipoUbicacionDTO data = service.buscarNombreTipoUbicacion(nombreTipoUbicacion);
            if (data != null){
                log.info("Tipo de Ubicacion encontrado con nombre: " + nombreTipoUbicacion);
                ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del Tipo de Ubicacion con nombre: " + nombreTipoUbicacion, data);
                return ResponseEntity.ok(respuesta);
            }
            log.info("Tipo de Ubicacion no encontrado: " + nombreTipoUbicacion);
            ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(false, "Tipo de Ubicacion no encontrado: " + nombreTipoUbicacion);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("Error critico al obtener el Tipo de Ubicacion con nombre: " + nombreTipoUbicacion);
            e.printStackTrace();
            ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(false, "Error al obtener el Tipo de Ubicacion con nombre: " + nombreTipoUbicacion);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }
}
