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
@RequestMapping("api/marcas")
@RequiredArgsConstructor
@CrossOrigin
public class MarcaController {

    private final MarcaService service;

    @PostMapping
    public ResponseEntity<ApiResponse<MarcaDTO>> nuevaMarca(@Valid @RequestBody MarcaDTO json) {
        MarcaDTO dto = service.nuevaMarca(json);
        log.info("Nueva marca registrada: " + dto);
        ApiResponse<MarcaDTO> respuesta = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MarcaDTO>>> obtenerDatos() {
        List<MarcaDTO> listaMarcas = service.obtenerTodo();
        ApiResponse<List<MarcaDTO>> respuesta = new ApiResponse<>(true, "Datos de marca consultados", listaMarcas);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaDTO>> obtenerPorId(@PathVariable Long id) {
        MarcaDTO dto = service.obtenerporId(id);
        log.info("Se obtuvieron los datos de la marca con id " + id);
        ApiResponse<MarcaDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos de la marca con id " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminar(id);
        if (eliminado) {
            log.info("Se logró eliminar la marca con id " + id);
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Se logró eliminar la marca con id " + id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "La marca con id " + id + " no fue encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody MarcaDTO dto) {
        MarcaDTO data = service.actualizar(id, dto);
        log.info("Se logró actualizar la marca con id " + id);
        ApiResponse<MarcaDTO> respuesta = new ApiResponse<>(true, "Se logró actualizar la marca con id " + id, data);
        return ResponseEntity.ok(respuesta);
    }
}