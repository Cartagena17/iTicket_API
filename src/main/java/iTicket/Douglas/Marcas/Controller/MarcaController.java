package iTicket.Douglas.Marcas.Controller;

import iTicket.Douglas.Marcas.DTO.MarcaDTO;
import iTicket.Douglas.Marcas.Service.MarcaService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping ("api/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService service;

    @PostMapping
    public ResponseEntity<ApiResponse<MarcaDTO>> nuevaMarca(@Valid @RequestBody MarcaDTO json){
        try {
            MarcaDTO dto = service.nuevaMarca(json);
            if (dto != null){
                log.info("Nuevo marca registrado +"+dto);
                ApiResponse<MarcaDTO> respuestaExito = new ApiResponse<>(true, "Datos registrados exitosamente" ,dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Intento de insercion fallida "+json);
            ApiResponse<MarcaDTO> respuestaFallida = new ApiResponse<>(false, "Intento de insercion fallida "+json);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        }catch (Exception e){
            log.error("El proceso presntó un fallo inesperado contacte con el administrador");
            e.printStackTrace();
            ApiResponse<MarcaDTO> respuestaFallida = new ApiResponse<>(false,"El proceso presntó un fallo inesperado contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MarcaDTO>>> obtenerDatos(){
        try {
            List<MarcaDTO> listaMarcas = service.obtenerTodo();
            if (listaMarcas != null){
                log.info("Datos de marca consultados");
                ApiResponse<List<MarcaDTO>> respuestaExito = new ApiResponse<>(true, "Datos de marca consultados",listaMarcas);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Datos de marcas no encontrados");
            ApiResponse<List<MarcaDTO>> respuestaNoEncontrada = new ApiResponse<>(false,"Datos de marcas no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("No se pudieron obtener los datos de las marcas");
            e.printStackTrace();
            ApiResponse<List<MarcaDTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos de las marcas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaDTO>> obtenerPorId (@PathVariable Long id){
        try {
            MarcaDTO dto = service.obtenerporId(id);
            if (dto != null){
                log.warn("Se obtuvieron los datos de la marca con id "+id);
                ApiResponse<MarcaDTO> respuestaExito = new ApiResponse<>(true,"Se obtuvieron los datos de la marca con id "+id, dto );
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("No se encontraron los datos de la marca con id "+id);
            ApiResponse<MarcaDTO> respuestaNoEncontrada = new ApiResponse<>(false,"No se encontraron los datos de la marca con id "+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("No se pudo obtener los datos de la marca con id "+id);
            e.printStackTrace();
            ApiResponse<MarcaDTO> respuestaFallida = new ApiResponse<>(false, "No se pudo obtener los datos de la marca con id "+id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar (@PathVariable Long id){
        try {
            boolean respuesta = service.eliminar(id);
            if (respuesta){
                log.info("Se logró eliminar la marca con id "+id);
                ApiResponse<Void> respuestaExito = new ApiResponse<>(true, "Se logró eliminar la marca con id "+id);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("la marca con id "+id+" no fue encontrada");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false,"la marca con id "+id+" no fue encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico, al eliminar la marca con id: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaFallida = new ApiResponse<>(false, "Error crítico, al eliminar la marca con id: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaDTO>> actualizar (@PathVariable Long id, @Valid @RequestBody MarcaDTO dto){
        try {
            MarcaDTO data = service.actualizar(id,dto);
            if (data != null){
                log.info("Se logro actualizar la marca con id "+id);
                ApiResponse<MarcaDTO> respuestaExito = new ApiResponse<>(true,"Se logro actualizar la marca con id "+id, data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("No se pudo actualizar la marca con id "+id);
            ApiResponse<MarcaDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo actualizar la marca con id "+id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        }catch (Exception e){
            log.error("Error crítico al actualizar la marca con id: " + id);
            e.printStackTrace();
            ApiResponse<MarcaDTO> respuestaFallida = new ApiResponse<>(false, "Error crítico al actualizar la marca con id: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

}
