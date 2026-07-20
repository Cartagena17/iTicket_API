package iTicket.Douglas.Prioridades.Controller;

import iTicket.Douglas.Prioridades.DTO.PrioridadDTO;
import iTicket.Douglas.Prioridades.Service.PrioridadService;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Tickets.DTO.TicketDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/prioridades")//Endpoint
public class PrioridadController {

    //Inyectar capa de servicios
    private final PrioridadService service;

    public PrioridadController(PrioridadService service) {
        this.service = service;
    }

    //Petición POST
    @PostMapping
    public ResponseEntity<ApiResponse<PrioridadDTO>> nuevaPrioridad(@Valid @RequestBody PrioridadDTO json){
        try {
            PrioridadDTO dto = service.nuevaPrioridad(json);
            if (dto != null){
                log.info("Nueva prioridad creada: " + dto);
                ApiResponse<PrioridadDTO> respuestaExito = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de insersión fallido: " + json);
            ApiResponse<PrioridadDTO> respuestaFallida = new ApiResponse<>(false, "Intento fallido de insersión");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó fallas inesperadas. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<PrioridadDTO> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Petición GET
    @GetMapping
    public ResponseEntity<ApiResponse<List<PrioridadDTO>>> obtenerDatos(){
        try {
            List<PrioridadDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de prioridades consultados");
                ApiResponse<List<PrioridadDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<PrioridadDTO>> respuestaNoEncontrada = new ApiResponse<>(false,"Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("El proceso presentó un fallo inesperado. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<List<PrioridadDTO>> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Método para buscar prioridad por id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrioridadDTO>> obtenerPrioridadPorId(@PathVariable Long id){
        try {
            PrioridadDTO dto = service.buscarPrioridad(id);
            if (dto != null){
                log.info("Se obtuvieron los datos de la prioridad con ID: " + id);
                ApiResponse<PrioridadDTO> respuestaExito = new ApiResponse<>(true, "Se obtuvieron los datos la prioridad con ID: " + id, dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados con ID: " + id);
            ApiResponse<PrioridadDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Datos no encontrados con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e){
            log.error("Error crítico al obtener la prioridad con ID: " + id);
            e.printStackTrace();
            ApiResponse<PrioridadDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos de la prioridad");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Petición DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPrioridad(@PathVariable Long id){
        try {
            boolean respueta = service.eliminarData(id);
            if (respueta){
                log.info("Prioridad con ID: " + id+ ", eliminada");
                ApiResponse<Void> respuestExitosa = new ApiResponse<>(true, "Prioridad con ID: " + id+ ", eliminada");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestExitosa);
            }
            log.info("Prioridad con ID: " + id+ ", no fue encontrada");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Prioridad con ID: " + id+ ", no fue encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al eliminar la prioridad con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "Error crítico al eliminar la data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Petición PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PrioridadDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody PrioridadDTO dto){
        try {
            PrioridadDTO data = service.actualizarPrioridad(id, dto);
            if (data != null){
                log.info("Prioridad con ID: "+ id + " ha sido actualizada.");
                ApiResponse<PrioridadDTO> respuestaExitosa = new ApiResponse<>(true, "Prioridad con ID: " + id + ", ha sido actualizada.", data);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización de la prioridad con ID: "+ id);
            ApiResponse<PrioridadDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización de la prioridad con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        }catch (Exception e){
            log.error("Error crítico al actualizar la prioridad con ID: " + id);
            e.printStackTrace();
            ApiResponse<PrioridadDTO> respuestaError = new ApiResponse<>(false, "Error crítico al actualizar la prioridad con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
