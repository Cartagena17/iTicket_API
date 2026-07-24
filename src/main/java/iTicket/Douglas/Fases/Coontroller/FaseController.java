package iTicket.Douglas.Fases.Coontroller;

import iTicket.Douglas.Fases.DTO.FaseDTO;
import iTicket.Douglas.Fases.DTO.PatchFaseDTO;
import iTicket.Douglas.Fases.Service.FaseService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fases")
public class FaseController {

    private final FaseService service;

    public FaseController(FaseService service) {
        this.service = service;
    }

    //Crear
    @PostMapping
    public ResponseEntity<ApiResponse<FaseDTO>> nuevaFase(@Valid @RequestBody FaseDTO json){
        try{
            FaseDTO dto = service.nuevaFase(json);
            if (dto != null){
                log.info("Nueva fase ingresada: " + dto);
                ApiResponse<FaseDTO> exito = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(exito);
            }
            log.warn("Intento de inserción fallida: " + json);
            ApiResponse<FaseDTO> respuesta = new ApiResponse<>(false, "Intento de inserción fallido", dto);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error en el proceso de inserción de la fase");
            e.printStackTrace();
            ApiResponse<FaseDTO> error = new ApiResponse<>(false, "Error en el proceso de inserción de la fase.", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FaseDTO>>> obtenerFases(){
        try {
            List<FaseDTO> lista = service.obtenerFases();
            if (lista != null){
                log.info("Datos de fases consultados con éxito.");
                ApiResponse<List<FaseDTO>> exito = new ApiResponse<>(true, "Datos de fases consultados con éxito.", lista);
                return ResponseEntity.ok(exito);
            }
            log.warn("No se econtraron los datos de las fases");
            ApiResponse<List<FaseDTO>> respuesta = new ApiResponse<>(false, "No se encontraron los datos");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al obtener las fases");
            e.printStackTrace();
            ApiResponse<List<FaseDTO>> error = new ApiResponse<>(false, "Error al obtener las fases");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FaseDTO>> buscarPorNombreFase(String id){
        try{
            FaseDTO dto = service.buscarPorNombreFase(id);
            if (dto != null){
                log.info("Fase: " + id + ", consultada con éxito");
                ApiResponse<FaseDTO> exito = new ApiResponse<>(true, "Fase: " + id + ", consultada con éxito");
                return ResponseEntity.ok(exito);
            }
            log.warn("No se ha encontrado la fase: " + id);
            ApiResponse<FaseDTO> respuesta = new ApiResponse<>(false, "No se ha encontrado la fase: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("No se pudo obtener la fase: " + id);
            e.printStackTrace();
            ApiResponse<FaseDTO> error = new ApiResponse<>(false, "No se pudo obtener la fase: " + id);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FaseDTO>> actualizarFase(@PathVariable Long id, @Valid @RequestBody FaseDTO dto){
        try {
            FaseDTO data = service.actualizarFase(id, dto);
            if (data != null){
                log.info("Fase con id: " + id + ", ha sido actualizada");
                ApiResponse<FaseDTO> exito = new ApiResponse<>(true, "Fase con id: " + id + ", ha sido actualizada", data);
                return ResponseEntity.ok(exito);
            }
            log.warn("No se pudo completar la actualización de la fase con id: " + id);
            ApiResponse<FaseDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la actualizacion de la fase con id: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al actualizar la fase con id: " + id);
            e.printStackTrace();
            ApiResponse<FaseDTO> error = new ApiResponse<>(false, "Error al actualizar la fase con id: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarFase(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarFase(id);
            if (respuesta){
                log.info("Fase: " + id + " eliminada");
                ApiResponse<Void> exito = new ApiResponse<>(true, "Fase: " + id + " eliminada");
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(exito);
            }
            log.warn("Fase: " + id + ", no ha sido encontrada");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Fase: " + id + ", no ha sido encontrada");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }
        catch (Exception e){
            log.error("Error en el proceso de eliminación de la fase con id: " + id);
            e.printStackTrace();
            ApiResponse<Void> error = new ApiResponse<>(false, "No se pudo eliminar la fase con id: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FaseDTO>> actualizarCampoFase(@PathVariable Long id, @Valid @RequestBody PatchFaseDTO dto){
        try {
            FaseDTO data = service.actualizarCampoFase(id, dto);
            if (data != null){
                log.info("Se ha actualizado la fase: " + id);
                ApiResponse<FaseDTO> exito = new ApiResponse<>(true, "Se ha actualizado la fase: " + id, data);
                return  ResponseEntity.ok(exito);
            }
            log.warn("No se pudo completar la actualización de la fase: " + id);
            ApiResponse<FaseDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la actualización de la fase: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al modificar la fase: " + id);
            e.printStackTrace();
            ApiResponse<FaseDTO> error = new ApiResponse<>(false, "Error al modificar la fase: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
