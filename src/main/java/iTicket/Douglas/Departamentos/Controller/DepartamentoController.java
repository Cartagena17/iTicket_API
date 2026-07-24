package iTicket.Douglas.Departamentos.Controller;

import iTicket.Douglas.Departamentos.DTO.DepartamentoDTO;
import iTicket.Douglas.Departamentos.Service.DepartamentoService;
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
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
public class DepartamentoController {

    private final DepartamentoService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DepartamentoDTO>> nuevoDepartamento (@Valid @RequestBody DepartamentoDTO json){
        try {
            DepartamentoDTO dto = service.nuevoDepartamento(json);
            if (dto != null){
                log.info("Nuevo departamento registrado " + dto);
                ApiResponse<DepartamentoDTO> respuestaExito = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Intento de insercion fallida " + json);
            ApiResponse<DepartamentoDTO> respuestaFallida = new ApiResponse<>(false, "Intento de insercion fallida ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado contacte con el administrador");
            e.printStackTrace();
            ApiResponse<DepartamentoDTO> respuestaFallida = new ApiResponse<>(false, "El proceso presentó un fallo inesperado contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartamentoDTO>>> obtenerDatos(){
        try {
            List<DepartamentoDTO> lista = service.obetenerTodo();
            if (lista != null) {
                log.info("Datos de departamentos consultados");
                ApiResponse<List<DepartamentoDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Datos de departamentos no encontrados");
            ApiResponse<List<DepartamentoDTO>> respuestaNoEncontrada = new ApiResponse<>(false, "Datos de departamentos no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("No se pudieron obtener los datos de los departamentos");
            e.printStackTrace();
            ApiResponse<List<DepartamentoDTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos de los departamentos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartamentoDTO>> obtenerDatosId(@PathVariable Long id) {
        try {
            DepartamentoDTO dto = service.obtenerPorId(id);
            if (dto != null) {
                log.info("Se obtuvieron los datos del departamento con id " + id);
                ApiResponse<DepartamentoDTO> respuestaExito = new ApiResponse<>(true, "Se obtuvieron los datos del departamento con id " + id, dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("No se encontraron los datos del departamento con id " + id);
            ApiResponse<DepartamentoDTO> respuestaNoEncontrada = new ApiResponse<>(false, "No se encontraron los datos del departamento con id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico al obtener los datos del departamento con id  " + id);
            e.printStackTrace();
            ApiResponse<DepartamentoDTO> respuestaError = new ApiResponse<>(false, "Error crítico al obtener los datos del departamento con id  " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartamentoDTO>> actualizarDepartamento(@PathVariable Long id, @Valid @RequestBody DepartamentoDTO dto) {
        try {
            DepartamentoDTO data = service.actualizar(id, dto);
            if (data != null) {
                log.info("Se logró actualizar el departamento con id "+id);
                ApiResponse<DepartamentoDTO> respuestaExito = new ApiResponse<>(true, "Se logró actualizar el departamento con id "+id, data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("No se pudo actualizar el departamento con id "+id);
            ApiResponse<DepartamentoDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo actualizar el departamento con id "+id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico en la actualizar el departamento con id " + id);
            e.printStackTrace();
            ApiResponse<DepartamentoDTO> respuestaError = new ApiResponse<>(false, "Error crítico en la actualizar el departamento con id " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        try {
            boolean respuesta = service.eliminar(id);
            if (respuesta) {
                log.info("Se logró eliminar el departamento con id "+id);
                ApiResponse<Void> respuestaExito = new ApiResponse<>(true, "Se logró eliminar el departamento con id "+id);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("No se logro eliminar el departamento con id "+id);
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "No se logro eliminar el departamento con id "+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico al eliminar el departamento con id " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaFallida = new ApiResponse<>(false, "Error crítico al eliminar el departamento con id " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }
}
