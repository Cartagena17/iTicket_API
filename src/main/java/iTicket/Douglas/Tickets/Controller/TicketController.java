package iTicket.Douglas.Tickets.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Tickets.DTO.TicketAsignacionDTO;
import iTicket.Douglas.Tickets.DTO.TicketDTO;
import iTicket.Douglas.Tickets.DTO.TicketResolucionDTO;
import iTicket.Douglas.Tickets.Service.TicketService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tickets")//Endpoint
public class TicketController {

    private final TicketService service;
    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TicketDTO>> nuevoTicket(@Valid @RequestBody TicketDTO json){
        try {
            TicketDTO dto = service.nuevoTicket(json); //Devuelve un TicketDTO que es la respuesta de la base
            if (dto != null){
                log.info("Nuevo ticket creado: " + dto);
                ApiResponse<TicketDTO> respuestaExito = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de insersión fallido: " + json);
            ApiResponse<TicketDTO> respuestaFallida = new ApiResponse<>(false, "Intento fallido de insersión");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó fallas inesperadas. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<TicketDTO> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketDTO>>> obtenerDatos(){
        try {
            List<TicketDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de tickets consultados");
                ApiResponse<List<TicketDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<TicketDTO>> respuestaNoEncontrada = new ApiResponse<>(false,"Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("El proceso presentó un fallo inesperado. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<List<TicketDTO>> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDTO>> obtenerTicketPorId(@PathVariable Long id){
        try {
            TicketDTO dto = service.buscarTicket(id);
            if (dto != null){
                log.info("Se obtuvieron los datos del ticket con ID: " + id);
                ApiResponse<TicketDTO> respuestaExito = new ApiResponse<>(true, "Se obtuvieron los datos del ticket con ID: " + id, dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados con ID: " + id);
            ApiResponse<TicketDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Datos no encontrados con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e){
            log.error("Error crítico al obtener del ticket con ID: " + id);
            e.printStackTrace();
            ApiResponse<TicketDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos del ticket");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTicket(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarData(id);
            if (respuesta){
                log.info("Ticket con ID: " + id+ ", eliminado");
                ApiResponse<Void> respuestExitosa = new ApiResponse<>(true, "Ticket con ID: " + id+ ", eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestExitosa);
            }
            log.info("Ticket con ID: " + id+ ", no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Ticket con ID: " + id+ ", no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al eliminar el ticket con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "Error crítico al eliminar la data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody TicketDTO dto){
        try {
            TicketDTO data = service.actualizarTicket(id, dto);
            if (data != null){
                log.info("Ticket con ID: "+ id + " ha sido actualizado.");
                ApiResponse<TicketDTO> respuestaExitosa = new ApiResponse<>(true, "Ticket con ID: " + id + " ha sido actualizado.", data);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización del ticket con ID: "+ id);
            ApiResponse<TicketDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización del ticket con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        }catch (Exception e){
            log.error("Error crítico al actualizar el ticket con ID: " + id);
            e.printStackTrace();
            ApiResponse<TicketDTO> respuestaError = new ApiResponse<>(false, "Error crítico al actualizar el ticket con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ApiResponse<TicketDTO>> buscarPorCodigo(@PathVariable String codigo){
        try {
            TicketDTO data = service.buscarPorCodigo(codigo);
            if (data != null){
                log.info("Ticket encontrado con código: " + codigo);
                ApiResponse<TicketDTO> respuestaExitosa = new ApiResponse<>(true, "Tickets encontrados con código: "+codigo, data);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("Datos no encontrados con código: " + codigo);
            ApiResponse<TicketDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Datos no encontrados con código: " + codigo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al obtener el ticket con código: " + codigo);
            e.printStackTrace();
            ApiResponse<TicketDTO> respuestaError = new ApiResponse<>(false, "Error crítico al obtener el ticket con código: " + codigo);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Proceso de búsqueda de tickets por asunto, devuelve una lista ya que busca varios por coincidencia
    @GetMapping("/asunto") //No usa @PathVariable ya que puede generar problemas si el asunto tiene espacios, caracteres especiales o es muy largo
    public ResponseEntity<ApiResponse<List<TicketDTO>>> buscarPorAsunto(@RequestParam String asunto){ //@RequestParam se utiliza para tomar el valor de los parametros de la query string de la URL (/tickets/asunto?asunto=impresora)
        try {
            List<TicketDTO> lista = service.buscarPorAsunto(asunto);
            if (!lista.isEmpty()){
                log.info("Tickets encontrados con asunto: " + asunto);
                ApiResponse<List<TicketDTO>> respuestExitosa = new ApiResponse<>(true, "Tickets encontrados con asunto: " + asunto, lista);
                return ResponseEntity.ok(respuestExitosa);
            }
            log.warn("Datos no encontrados con asunto: " + asunto);
            ApiResponse<List<TicketDTO>> respuestaNoEncontrada = new ApiResponse<>(false, "Tickets no encontrados con asunto: " + asunto);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al obtener ticket con asunto: " + asunto);
            e.printStackTrace();
            ApiResponse<List<TicketDTO>> respuestaError = new ApiResponse<>(false, "Error crítico al obtener tickets con asunto: " + asunto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PatchMapping("/{id}/asignacion")
    public ResponseEntity<ApiResponse<TicketAsignacionDTO>> asignarTicket(@PathVariable Long id, @Valid @RequestBody TicketAsignacionDTO dto) {
        try {
            boolean resultado = service.asignarTicket(id, dto);
            if (resultado){
                log.info("Ticket con ID: "+ id + " ha sido actualizado.");
                ApiResponse<TicketAsignacionDTO> respuestaExitosa = new ApiResponse<>(true, "Ticket con ID: " + id + " ha sido actualizado.", dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización del ticket con ID: "+ id);
            ApiResponse<TicketAsignacionDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización del ticket con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico al actualizar los datos. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<TicketAsignacionDTO> respuestaError = new ApiResponse<>(false, "Error crítico al actualizar el ticket con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PatchMapping("/{id}/reporte")
    public ResponseEntity<ApiResponse<TicketResolucionDTO>> asignarTicket(@PathVariable Long id, @Valid @RequestBody TicketResolucionDTO dto) {
        try {
            boolean resultado = service.reporteTicket(id, dto);
            if (resultado){
                log.info("El reporte del ticket con ID: "+ id + " ha sido actualizado.");
                ApiResponse<TicketResolucionDTO> respuestaExitosa = new ApiResponse<>(true, "Ticket con ID: " + id + " ha sido actualizado.", dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización del ticket con ID: "+ id);
            ApiResponse<TicketResolucionDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización del reporte del ticket con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico al actualizar los datos. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<TicketResolucionDTO> respuestaError = new ApiResponse<>(false, "Error crítico al actualizar el reporte del ticket con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
