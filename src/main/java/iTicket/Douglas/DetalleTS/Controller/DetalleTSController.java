package iTicket.Douglas.DetalleTS.Controller;


import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Service.DetalleTSService;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Ubicaciones.Service.UbicacionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping ("/api/detalleTS")
public class DetalleTSController {

    //Inyectar la capa service en el controller
    private final DetalleTSService service;

    public DetalleTSController(DetalleTSService service) {
        this.service = service;
    }

    //Agregar
    @PostMapping
    public ResponseEntity<ApiResponse<DetalleTSDTO>> agregarDetalleTS(@Valid @RequestBody DetalleTSDTO json){
        try{
            DetalleTSDTO dto = service.nuevoDetalleTS(json);
            if (dto != null){
                log.info("Nuevo detalle ingresado: " + json);
                ApiResponse<DetalleTSDTO> exito = new ApiResponse<>(true, "Nuevo detalle ingresado: ", dto);
                return  ResponseEntity.status(HttpStatus.CREATED).body(exito);
            }
            log.warn("Error al ingresar el nuevo detalle" + json);
            ApiResponse<DetalleTSDTO> respuesta = new ApiResponse<>(false, "Error al ingresar el nuevo detalle");
            return  ResponseEntity.ok(respuesta);
        }
        catch (Exception e){
            log.error("El proceso de ingreso no pudo completarse");
            e.printStackTrace();
            ApiResponse<DetalleTSDTO> error = new ApiResponse<>(false, "No pudo completarse el proceso de ingreso", json);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Obtener
    @GetMapping
    public  ResponseEntity<ApiResponse<List<DetalleTSDTO>>> obtenerDetalleTS(){
        try{
            List<DetalleTSDTO> lista = service.obtenerTodos();
            if(lista != null){
                log.info("Se obtuvo con exito el detalle de ticket.");
                ApiResponse<List<DetalleTSDTO>> exito = new ApiResponse<>(true, "El proceso de obtencion se completo con exito", lista);
                return  ResponseEntity.ok(exito);
            }
            log.warn("No se encontraron los datos.");
            ApiResponse<List<DetalleTSDTO>> respuesta = new ApiResponse<>(false, "No se encontraron los datos");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al obtener los detalles del ticket.");
            e.printStackTrace();
            ApiResponse<List<DetalleTSDTO>> error = new ApiResponse<>(false, "Error al obtener los detalles del ticket.");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Metodo para obtener detalles por id de ticket, quitar comentario al unir las demas partes
    @GetMapping("/detalleTSIdTicket/{ticket}")
    public ResponseEntity<ApiResponse<DetalleTSDTO>> buscarDetalleTSIdTicket(@PathVariable TicketEntity ticket){
        try{
            DetalleTSDTO datos = service.obtenerDetalleTSIdTicket(ticket);
            if (datos != null){
                log.info("Se obtuvo con exito el detalle del ticket: " + ticket);
                ApiResponse<DetalleTSDTO> exito = new ApiResponse<>(true, "Se obtuvo con exito el detalle del ticket: " + ticket);
                return  ResponseEntity.ok(exito);
            }
            log.warn("No se pudo encontrar el detalle del ticket: " + ticket);
            ApiResponse<DetalleTSDTO> respuesta = new ApiResponse<>(false, "No se pudo encontrar el detalle del ticket: " + ticket);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al obtener el detalle del ticket: " + ticket);
            e.printStackTrace();
            ApiResponse<DetalleTSDTO> error = new ApiResponse<>(false, "Error al obtener el detalle del ticket: " + ticket);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Editar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleTSDTO>> actualizarDetalleTS(@PathVariable Long id, @Valid @RequestBody DetalleTSDTO dto){
        try{
            DetalleTSDTO datos = service.actualizarDetalleTS(id, dto);
            if (datos != null){
                log.info("Detalle de ticket:" + id + ", actualizado con exito");
                ApiResponse<DetalleTSDTO> exito = new ApiResponse<>(true, "Detalle de ticket: " + id + ", actualizado con exito" + dto);
                return  ResponseEntity.ok(exito);
            }
            log.warn("No se pudo completar la actualizacion del detalle del ticket:" + id);
            ApiResponse<DetalleTSDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la actualizacion del detalle del ticket: " + id);
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al actualizar el detalle del ticket: " + id);
            ApiResponse<DetalleTSDTO> error = new ApiResponse<>(false, "Error al actualizar el detalle del ticket con id: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDetalleTS(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarDetalleTS(id);
            if (respuesta){
                log.info("Detalle de ticket: " + id + ", eliminado");
                ApiResponse<Void> exito = new ApiResponse<>(true, "Detalle de ticket: " + id + ", eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(exito);
            }
            log.warn("Detalle de ticket: " + id + ", no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Detalle de ticket: " + id + ", no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }
        catch (Exception e){
            log.error("Error al eliminar el detalle del ticket: " + id);
            ApiResponse<Void> error = new ApiResponse<>(false, "Error al eliminar el detalle del ticket" + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
