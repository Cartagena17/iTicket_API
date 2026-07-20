package iTicket.Douglas.DetalleGeneral.Controller;


import iTicket.Douglas.DetalleGeneral.DTO.DetalleGDTO;
import iTicket.Douglas.DetalleGeneral.Service.DetalleGService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping ("/api/DetalleG")
public class DetalleGController {

    private final DetalleGService service;

    public DetalleGController(DetalleGService service) {
        this.service = service;
    }

    //Crear
    @PostMapping
    public ResponseEntity<ApiResponse<DetalleGDTO>> nuevoDetalleG(@Valid @RequestBody DetalleGDTO json){
        try {
            DetalleGDTO dto = service.nuevoDetalleG(json);
            if (dto != null){
                log.info("Nuevo detalle ingresado: " + dto);
                ApiResponse<DetalleGDTO> exito = new ApiResponse<>(true, "Datos ingresados exitosamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(exito);
            }
            log.warn("Intento de insercion fallido: " + json);
            ApiResponse<DetalleGDTO> respuesta = new ApiResponse<>(false, "Intento de insercion fallido");
            return ResponseEntity.ok(respuesta);
        }
        catch (Exception e){
            log.error("Error al ingresar el detalle del ticket");
            e.printStackTrace();
            ApiResponse<DetalleGDTO> error = new ApiResponse<>(false, "Error en el proceso de insercion" + json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Obtener todos
    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleGDTO>>> obtenerDetallesG(){
        try {
            List<DetalleGDTO> lista = service.obtenerDetallesG();
            if (lista != null){
                log.info("Se obtuvieron con exito los detalles");
                ApiResponse<List<DetalleGDTO>> exito = new ApiResponse<>(true, "Detalles encontrados: " + lista);
                return ResponseEntity.ok(exito);
            }
            log.warn("No se encontraron los detalles");
            ApiResponse<List<DetalleGDTO>> respuesta = new ApiResponse<>(false, "No se encontraron los detalles");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        catch (Exception e){
            log.error("No se pudieron obtener los detalles");
            e.printStackTrace();
            ApiResponse<List<DetalleGDTO>> error = new ApiResponse<>(false, "No se pudieron obtener los datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Metodo para obtener detalle de ticket general por id de ticket, quitar comentario cuando se unan las demas partes
//    @GetMapping ("/detalleGIdTicket/{ticket}")
//    public ResponseEntity<ApiResponse<DetalleGDTO>> buscarDetalleGIdTicket(@PathVariable TicketsEntity ticket){
//        try{
//            DetalleGDTO datos = service.obtenerDetallesIdTicket(ticket);
//            if (datos != null){
//                log.info("Se obtuvo con exito el detalle del ticket: " + ticket);
//                ApiResponse<DetalleGDTO> exito = new ApiResponse<>(true, "Se obtuvo con exito el detalle del ticket: " + ticket);
//                return  ResponseEntity.ok(exito);
//            }
//            log.warn("No se pudo encontrar el detalle del ticket: " + ticket);
//            ApiResponse<DetalleGDTO> respuesta = new ApiResponse<>(false, "No se pudo encontrar el detalle del ticket: " + ticket);
//            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
//        }
//        catch (Exception e){
//            log.error("Error al obtener el detalle del ticket: " + ticket);
//            e.printStackTrace();
//            ApiResponse<DetalleGDTO> error = new ApiResponse<>(false, "Error al obtener el detalle del ticket: " + ticket);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }

    //Editar
    @PutMapping("/{id}")
    public  ResponseEntity<ApiResponse<DetalleGDTO>> actualizarDetalleG(@PathVariable Long id, @Valid @RequestBody DetalleGDTO dto){
        try{
            DetalleGDTO datos = service.actualizarDetalleG(id, dto);
            if (datos != null){
                log.info("Detalle de ticket: " + id + ", ha sido actualizado");
                ApiResponse<DetalleGDTO> exito = new ApiResponse<>(true, "Detalle de ticket: " + id + ", ha sido actualizado", dto);
                return  ResponseEntity.ok(exito);
            }
            log.warn("No se pudo actualizar el detalle del ticket: " + id);
            ApiResponse<DetalleGDTO> respuesta = new ApiResponse<>(false, "No se pudo actualizar el detalle del ticket: " + id);
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al actualizar el detalle del ticket: " + id);
            e.printStackTrace();
            ApiResponse<DetalleGDTO> error = new ApiResponse<>(false, "Error al actualizar el detalle del ticket: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDetalleG(@PathVariable Long id){
        try{
            boolean respuesta = service.eliminarDetalleG(id);
            if (respuesta){
                log.info("Detalle de ticket: " + id + ", eliminado");
                ApiResponse<Void> exito = new ApiResponse<>(true, "Detalle de ticket: " + id + ", eliminado");
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(exito);
            }
            log.warn("Detalle de ticket: " + id + ", no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Detalle de ticket: " + id + ", no fue encontrado");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }
        catch (Exception e){
            log.error("Error al eliminar el detalle del ticket: " + id);
            e.printStackTrace();
            ApiResponse<Void> error = new ApiResponse<>(false, "Error al eliminar el detalle del ticket: " + id);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleGDTO>> actualizarCampoDetalleG(@PathVariable Long id, @RequestBody DetalleGDTO dto){
        try {
            DetalleGDTO datos = service.actualizarCampoDetalleG(id, dto);
            if (datos != null){
                log.info("Ticket: " + id + ", ha sido actualizado");
                ApiResponse<DetalleGDTO> exito = new ApiResponse<>(true, "Campo de ticket: " + id + ", ha sido actualizado", dto);
                return  ResponseEntity.ok(exito);
            }
            log.warn("No se pudo actualizar el ticket: " + id);
            ApiResponse<DetalleGDTO> respuesta = new ApiResponse<>(false, "No se pudo actualizar el detalle del ticket: " + id);
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al actualizar el ticket: " + id);
            e.printStackTrace();
            ApiResponse<DetalleGDTO> error = new ApiResponse<>(false, "Error al actualizar el ticket: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
