package iTicket.Douglas.Bitacoras.Controller;


import iTicket.Douglas.Bitacoras.DTO.BitacorasDTO;
import iTicket.Douglas.Bitacoras.Service.BitacorasService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.Position;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bitacoras")
public class BitacorasController {

    private final BitacorasService service;

    public BitacorasController(BitacorasService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BitacorasDTO>> nuevaBitacora(@Valid @RequestBody BitacorasDTO json){
        try{
            BitacorasDTO dto = service.nuevaBitacora(json);
            if (dto != null){
                log.info("Nueva bitacora registrada" + dto);
                ApiResponse<BitacorasDTO> exito = new ApiResponse<>(true, "Proceso completado exitosamente", dto);
                return ResponseEntity.ok(exito);
            }
            log.warn("No se pudo registrar la bitacora" + json);
            ApiResponse<BitacorasDTO> bitacoraNoIngresada = new ApiResponse<>(false, "No se pudo registrar la bitacora", json);
            return ResponseEntity.ok(bitacoraNoIngresada);
        }
        catch (Exception e){
            log.error("La inserción de datos falló");
            e.printStackTrace();
            ApiResponse<BitacorasDTO> error = new ApiResponse<>(false,"Falló al ingresar los datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BitacorasDTO>>> obtenerBitacoras(){
        try{
            List<BitacorasDTO> lista = service.obtenerBitacoras();
            if (lista != null){
                log.info("Se obtuvieron con exito las bitacoras");
                ApiResponse<List<BitacorasDTO>> exito = new ApiResponse<>(true, "Se obtuvieron con exito las bitacoras", lista);
                return  ResponseEntity.ok(exito);
            }
            log.info("Bitacoras no encontradas");
            ApiResponse<List<BitacorasDTO>> bitacoraNoEncontrada = new ApiResponse<>(false, "Bitacoras no encontradas");
            return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(bitacoraNoEncontrada);
        }
        catch (Exception e){
            log.error("Ocurrio un error en la obtencion de las bitacoras");
            e.printStackTrace();
            ApiResponse<List<BitacorasDTO>> error = new ApiResponse<>(false, "Falló al obtener los datos", null);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BitacorasDTO>> obtenerBitacoraPorId(@PathVariable Long id){
        try{
            BitacorasDTO dto = service.obtenerBitacoraPorId(id);
            if (dto != null){
                log.info("Se obtuvo la bitacora: " + id);
                ApiResponse<BitacorasDTO> exito = new ApiResponse<>(true, "Se obtuvo la bitacora: " + id, dto);
                return ResponseEntity.ok(exito);
            }
            log.info("Bitacora con id: " + id + "no encontrada");
            ApiResponse<BitacorasDTO> bitacoraNoEncontrada = new ApiResponse<>(false, "Bitacora con id:" + id + "no encontrada");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(bitacoraNoEncontrada);
        }
        catch (Exception e){
            log.error("Ocurrio un error al obtener la bitacora con id: " + id);
            e.printStackTrace();
            ApiResponse<BitacorasDTO> error = new ApiResponse<>(false, "Falló al obtener los datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
