package iTicket.Douglas.DetalleTS.Controller;


import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Service.DetalleTSService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping ("/api/detalle_TS")
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
                log.info("Nuevo detalle ingresado: " + dto);
                ApiResponse<DetalleTSDTO> exito = new ApiResponse<>(true, "Nuevo detalle ingresado: " + dto);
                return  ResponseEntity.ok(exito);
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
                log.info("El proceso de obtencion se completo con exito");
                ApiResponse<List<DetalleTSDTO>> exito = new ApiResponse<>(true, "El proceso de obtencion se completo con exito", lista);
                return  ResponseEntity.ok(exito);
            }
            log.warn("No se encontraron los datos");
            ApiResponse<List<DetalleTSDTO>> respuesta = new ApiResponse<>(false, "No se encontraron los datos");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        catch (Exception e){
            log.error("El proceso de obtención no pudo completarse");
            e.printStackTrace();
            ApiResponse<List<DetalleTSDTO>> error = new ApiResponse<>(false, "El proceso de obtencion no puedo completarse");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
