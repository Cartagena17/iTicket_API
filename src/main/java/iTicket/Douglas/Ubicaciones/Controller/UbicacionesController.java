package iTicket.Douglas.Ubicaciones.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.TipoUbicacion.DTO.TipoUbicacionDTO;
import iTicket.Douglas.TipoUbicacion.Service.TipoUbicacionService;
import iTicket.Douglas.Ubicaciones.DTO.UbicacionesDTO;
import iTicket.Douglas.Ubicaciones.Service.UbicacionesService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionesController {

    private final UbicacionesService service;

    public UbicacionesController(UbicacionesService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UbicacionesDTO>> nuevaUbicacion(@Valid @RequestBody UbicacionesDTO json){

        try{
            UbicacionesDTO dto = service.nuevaUbicacion(json);
            if (dto != null){
                log.info("Nueva Ubicacion: " + dto);
                ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            }
            log.warn("Intento de insercion fallido: " + json);
            ApiResponse<UbicacionesDTO> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presento fallas inesperadas. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @GetMapping
    private ResponseEntity<ApiResponse<List<UbicacionesDTO>>> obtenerDatos(){

        try {
            List<UbicacionesDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de la Ubicacion consultados");
                ApiResponse<List<UbicacionesDTO>> respuestaExito = new ApiResponse<>(true,"Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos No encontrados");
            ApiResponse<List<UbicacionesDTO>> respuestaNoEncontrada = new ApiResponse<>(true,"Datos encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);

        } catch (Exception e) {
            log.error("El proceso presento errores inesperados. Consulte con un administrador");
            e.printStackTrace();
            ApiResponse<List<UbicacionesDTO>> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UbicacionesDTO>> obtenerDatosPorId(@PathVariable Long id){

        try {
            UbicacionesDTO dto = service.buscarUbicacionPorId(id);
            if (dto != null){
                log.info("Se obtubieron los datos de la Ubicacion" + dto);
                ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(true,"Se obtubieron los datos de la Ubicacion mediante su ID: " + id, dto);
                return ResponseEntity.ok(respuesta);
            }
            log.info("Los datos de la Ubicacion no se encontraron con el ID: " + id);
            ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(false,"Los datos de la Ubicacion no se encontraron con el ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        } catch (Exception e) {
            log.error("Error critico al obtener la Ubicacion con ID: " + id);
            e.printStackTrace(); // Muestra el lugar exacto de donde ocurrio el error en la ejecución
            ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(false, "Error al obtener el la Ubicacion con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarUbicacion(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarData(id);
            if (respuesta){
                log.info("La Ubicacion con ID: " + id + " Ya fue eliminado");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "La Ubicacion con ID: " + id + "Ya fue eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body((respuestaExitosa));
            }
            log.info("La Ubicacion con ID: " + id + " no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "La Ubicacion con ID: " + id + " no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }
        catch (Exception e){
            log.error("Error critico al eliminar la Ubicacion con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuesta = new ApiResponse<>(false, "Error al eliminar la Ubicacion con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UbicacionesDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody UbicacionesDTO dto){
        try{
            UbicacionesDTO data = service.actualizar(id, dto);
            if (data != null){
                log.info("La Ubicacion con ID: " + id + " ha sido actualizado");
                ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(true, "La Ubicacion con ID: " + id + " ha sido actualizado", data);
                return ResponseEntity.ok(respuesta);
            }
            log.warn("No se pudo completar la actualización de la Ubicacion con ID: " + id );
            ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la actualización la de Ubicacion con ID: " + id );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error critico al actualizar la Ubicacion con ID: " + id);
            e.printStackTrace();
            ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(false, "Error al actualizar la Ubicacion con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }

    @GetMapping("/nombreUbicacion/{nombreUbicacion}")
    public ResponseEntity<ApiResponse<UbicacionesDTO>> buscarUbicacionPorNombre(@PathVariable String nombreUbicacion){
        try{

            UbicacionesDTO data = service.buscarUbicacionPorNombre(nombreUbicacion);
            if (nombreUbicacion != null){
                log.info("La Ubicacion encontrada con nombre: " + nombreUbicacion);
                ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos de la Ubicacion con nombre: " + nombreUbicacion, data);
                return ResponseEntity.ok(respuesta);
            }
            log.info("La Ubicacion no encontrada: " + nombreUbicacion);
            ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(false, "La Ubicacion no encontrado: " + nombreUbicacion);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("Error critico al obtener la Ubicacion con nombre: " + nombreUbicacion);
            e.printStackTrace();
            ApiResponse<UbicacionesDTO> respuesta = new ApiResponse<>(false, "Error al obtener la Ubicacion con abreviatura: " + nombreUbicacion);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((respuesta));
        }
    }
}
