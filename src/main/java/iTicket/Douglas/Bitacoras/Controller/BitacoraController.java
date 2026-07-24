package iTicket.Douglas.Bitacoras.Controller;


import iTicket.Douglas.Bitacoras.DTO.BitacoraDTO;
import iTicket.Douglas.Bitacoras.Service.BitacoraService;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping ("/api/bitacoras")
public class BitacoraController {

    private final BitacoraService service;

    public BitacoraController(BitacoraService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BitacoraDTO>> nuevaBitacora(@Valid @RequestBody BitacoraDTO json){
        try{
            BitacoraDTO dto = service.nuevaBitacora(json);
            if (dto != null){
                log.info("Nueva bitacora registrada" + dto);
                ApiResponse<BitacoraDTO> exito = new ApiResponse<>(true, "Proceso completado exitosamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(exito);
            }
            log.warn("No se pudo registrar la bitacora" + json);
            ApiResponse<BitacoraDTO> bitacoraNoIngresada = new ApiResponse<>(false, "No se pudo registrar la bitacora", json);
            return ResponseEntity.ok(bitacoraNoIngresada);
        }
        catch (Exception e){
            log.error("La inserción de datos falló");
            e.printStackTrace();
            ApiResponse<BitacoraDTO> error = new ApiResponse<>(false,"Falló al ingresar los datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BitacoraDTO>>> obtenerBitacoras(){
        try{
            List<BitacoraDTO> lista = service.obtenerBitacoras();
            if (lista != null){
                log.info("Se obtuvieron con exito las bitacoras");
                ApiResponse<List<BitacoraDTO>> exito = new ApiResponse<>(true, "Se obtuvieron con exito las bitacoras", lista);
                return  ResponseEntity.ok(exito);
            }
            log.info("Bitacoras no encontradas");
            ApiResponse<List<BitacoraDTO>> bitacoraNoEncontrada = new ApiResponse<>(false, "Bitacoras no encontradas");
            return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(bitacoraNoEncontrada);
        }
        catch (Exception e){
            log.error("Ocurrio un error en la obtencion de las bitacoras");
            e.printStackTrace();
            ApiResponse<List<BitacoraDTO>> error = new ApiResponse<>(false, "Falló al obtener los datos");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Metodo para obtenere bitacora por id de ticket
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BitacoraDTO>> obtenerBitacoraIdTicket(@PathVariable TicketEntity idTicket ){
        try {
            BitacoraDTO dto = service.obtenerBitacoraIdTicket(idTicket);
            if (dto != null){
                log.info("Se obtuvo la bitacora con idticket: " + idTicket);
                ApiResponse<BitacoraDTO> exito = new ApiResponse<>(true, "Se obtuvo la bitacora con idticket: " + idTicket);
                return ResponseEntity.ok(exito);
            }
            log.warn("Bitacora con idticket: " + idTicket + ", no encontrada");
            ApiResponse<BitacoraDTO> respuesta = new ApiResponse<>(false, "Bitacora con idticket: " + idTicket + ", no encontrada");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("Ocurrio un error al obtener la bitacora con ticket: " + idTicket);
            e.printStackTrace();
            ApiResponse<BitacoraDTO> error = new ApiResponse<>(false, "Error al obtener la bitacora: " + idTicket);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
