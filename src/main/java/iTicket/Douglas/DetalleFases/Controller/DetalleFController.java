package iTicket.Douglas.DetalleFases.Controller;


import iTicket.Douglas.DetalleFases.DTO.DetalleFDTO;
import iTicket.Douglas.DetalleFases.Service.DetalleFService;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Tickets.DTO.TicketDTO;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping ("/api/detalleFase")
public class DetalleFController {

    private final DetalleFService service;

    public DetalleFController(DetalleFService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DetalleFDTO>> nuevoDetalleF(@Valid @RequestBody DetalleFDTO json){
        try{
            DetalleFDTO dto = service.nuevoDetalleF(json);
            if (dto != null){
                log.info("Nuevo detalle de fase ingresado: " + dto);
                ApiResponse<DetalleFDTO> exito = new ApiResponse<>(true, "Nuevo detalle de fase ingresado", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(exito);
            }
            log.warn("Intento de inserción fallido: " + json);
            ApiResponse<DetalleFDTO> respuesta = new ApiResponse<>(false, "Intento de insercion fallido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al ingresar los datos del detalle" + json);
            e.printStackTrace();
            ApiResponse<DetalleFDTO> error = new ApiResponse<>(false, "Error al ingresar los datos del detalle", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleFDTO>>> obtenerDetallesF(){
        try{
            List<DetalleFDTO> lista = service.obtenerDetallesF();
            if (lista != null){
                log.info("Datos de detalle de fase consultados");
                ApiResponse<List<DetalleFDTO>> exito = new ApiResponse<>(true, "Datos de detalle de fase consultados");
                return ResponseEntity.ok(exito);
            }
            log.warn("No se encontraron los datos de detalle de fase");
            ApiResponse<List<DetalleFDTO>> respuesta = new ApiResponse<>(false, "No se encontraron los datos de detalle de fase");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al obtener los detalles de fase");
            e.printStackTrace();
            ApiResponse<List<DetalleFDTO>> error = new ApiResponse<>(false, "Error al obtener los detalles de fase");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleFDTO>> actualizarDetalleF(@PathVariable Long id, @Valid @RequestBody DetalleFDTO dto){
        try {
            DetalleFDTO data = service.actualizarDetalleF(id, dto);
            if (data != null){
                log.info("Detalle de fase con ID: "+ id + " ha sido actualizado.");
                ApiResponse<DetalleFDTO> exito = new ApiResponse<>(true, "Detalle de fase con ID: " + id + " ha sido actualizado.", data);
                return ResponseEntity.ok(exito);
            }
            log.warn("No se pudo completar la actualización del detalle de fase con ID: "+ id);
            ApiResponse<DetalleFDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la actualización del detalle de fase con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }catch (Exception e){
            log.error("Error crítico al actualizar el detalle de fase con ID: " + id);
            e.printStackTrace();
            ApiResponse<DetalleFDTO> error = new ApiResponse<>(false, "Error crítico al actualizar el detalle de fase con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDetalleF(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarDetalleF(id);
            if (respuesta){
                log.info("Detalle de fase con ID: " + id+ ", eliminado");
                ApiResponse<Void> exito = new ApiResponse<>(true, "Detalle de fase con ID: " + id+ ", eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(exito);
            }
            log.info("Detalle de fase con ID: " + id+ ", no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Detalle de fase con ID: " + id+ ", no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error al eliminar el detalle de fase: " + id);
            e.printStackTrace();
            ApiResponse<Void> error = new ApiResponse<>(false, "Error al eliminar el detalle de la fase");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/idFase/{fase}")
    public ResponseEntity<ApiResponse<List<DetalleFDTO>>> obtenerPorIdFase(@PathVariable Long fase){
        try {
           List<DetalleFDTO> dto = service.obtenerPorIdFase(fase);
            if (dto != null) {
                log.info("Se obtuvieron los datos del detalle de la fase: " + fase);
                ApiResponse<List<DetalleFDTO>> exito = new ApiResponse<>(true, "Se obtuvieron los datos del detalle de la fase: " + fase, dto);
                return ResponseEntity.ok(exito);
            }
            log.warn("Datos no encontrados con ID: " + fase);
            ApiResponse<List<DetalleFDTO>> respuesta = new ApiResponse<>(false, "Detalle de fase con id: " + fase + ", no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al obtener el detalle de la fase: " + fase);
            e.printStackTrace();
            ApiResponse<List<DetalleFDTO>> error = new ApiResponse<>(false, "No se pudo obtener los datos del detalle de la fase: " + fase);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
