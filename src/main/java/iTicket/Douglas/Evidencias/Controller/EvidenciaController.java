package iTicket.Douglas.Evidencias.Controller;

import iTicket.Douglas.Evidencias.DTO.EvidenciaDTO;
import iTicket.Douglas.Evidencias.Service.EvidenciaService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/evidencias")//Endpoint
public class EvidenciaController {

    //Inyectamos la capa de servicios
    private final EvidenciaService service;

    public EvidenciaController(EvidenciaService service) {
        this.service = service;
    }

    //Método POST
    @PostMapping
    public ResponseEntity<ApiResponse<EvidenciaDTO>> nuevaEvidencia(@Valid @RequestBody EvidenciaDTO json){
        try{
            EvidenciaDTO dto = service.nuevaEvidencia(json);
            if (dto != null){
                log.info("Nueva evidencia registrada: " + dto);
                ApiResponse<EvidenciaDTO> respuesta = new ApiResponse<>(true, "Datos ingresados exitosamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            }
            log.warn("Intento de insersión fallido: " + json);
            ApiResponse<EvidenciaDTO> respuestaFallida = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e){
            log.error("El proceso presentó fallas inesperadas. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<EvidenciaDTO> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Petición GET
    @GetMapping
    public ResponseEntity<ApiResponse<List<EvidenciaDTO>>> obtenerDatos(){
        try {
            List<EvidenciaDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de evidencias consultados");
                ApiResponse<List<EvidenciaDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<EvidenciaDTO>> respuestaNoEncontrada = new ApiResponse<>(false,"Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("El proceso presentó un fallo inesperado. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<List<EvidenciaDTO>> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Método para buscar evidencias por id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvidenciaDTO>> obtenerEvidenciaPorId(@PathVariable Long id){
        try {
            EvidenciaDTO dto = service.buscarEvidencia(id);
            if (dto != null){
                log.info("Se obtuvieron los datos de la evidencia con ID: " + id);
                ApiResponse<EvidenciaDTO> respuestaExito = new ApiResponse<>(true, "Se obtuvieron los datos la evidencia con ID: " + id, dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados con ID: " + id);
            ApiResponse<EvidenciaDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Datos no encontrados con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e){
            log.error("Error crítico al obtener la evidencia con ID: " + id);
            e.printStackTrace();
            ApiResponse<EvidenciaDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos de la evidencia");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Método para obtener evidenicas por ticket
    @GetMapping("/ticket/{idTicket}")
    public ResponseEntity<ApiResponse<List<EvidenciaDTO>>> obtenerEvidenciasPorTicket(@PathVariable Long idTicket){
        try {
            List<EvidenciaDTO> lista = service.obtenerEvidenciasPorTicket(idTicket);

            if (!lista.isEmpty()) {
                log.info("Evidencias consultadas para el ticket de ID: " + idTicket);
                ApiResponse<List<EvidenciaDTO>> respuestaExito = new ApiResponse<>(true, "Evidencias encontradas", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("No se encontraron evidencias para el ticket de ID: " + idTicket);
            ApiResponse<List<EvidenciaDTO>> respuestNoEncontrada = new ApiResponse<>(false, "Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestNoEncontrada);
        }catch (RuntimeException e) {
            //Cuando el id de ticket no existe
            log.warn("Ticket no encontrado: " + e.getMessage());
            ApiResponse<List<EvidenciaDTO>> respuestaTicketNoExiste = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaTicketNoExiste);
        }catch (Exception e){
            log.error("El proceso presentó fallas inesperadas. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<List<EvidenciaDTO>> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eilimarEvidencia(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarData(id);
            if (respuesta){
                log.info("Evidencia con ID: " + id+ " eliminada");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "Evidencia con ID: " + id+ "eliminada");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.info("Evidencia con ID: " + id+ ", no fue encontrada");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Evidencia con ID: " + id+ ", no fue encontrada");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al eliminar la evidencia con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "Error crítico al eliminar la data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

}
