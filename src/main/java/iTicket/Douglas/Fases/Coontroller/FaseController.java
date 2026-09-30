package iTicket.Douglas.Fases.Coontroller;

import iTicket.Douglas.Fases.DTO.FaseDTO;
import iTicket.Douglas.Fases.DTO.PatchFaseDTO;
import iTicket.Douglas.Fases.DTO.ValidacionCrear;
import iTicket.Douglas.Fases.Service.FaseService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fases")
@RequiredArgsConstructor
public class FaseController {

    private final FaseService service;

    @PostMapping
    // Solo al crear se exige que las fechas estimadas sean hoy/futuras (grupo ValidacionCrear).
    public ResponseEntity<ApiResponse<FaseDTO>> nuevaFase(@Validated({Default.class, ValidacionCrear.class}) @RequestBody FaseDTO json) {
        FaseDTO dto = service.nuevaFase(json);
        log.info("Nueva fase ingresada: " + dto);
        ApiResponse<FaseDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FaseDTO>>> obtenerFases() {
        List<FaseDTO> lista = service.obtenerFases();
        log.info("Datos de fases consultados con éxito.");
        ApiResponse<List<FaseDTO>> respuesta = new ApiResponse<>(true, "Datos de fases consultados con éxito.", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/nombreFase/{nombreFase}")
    public ResponseEntity<ApiResponse<FaseDTO>> buscarPorNombreFase(@PathVariable String nombreFase) {
        FaseDTO dto = service.buscarPorNombreFase(nombreFase);
        log.info("Fase: " + nombreFase + ", consultada con éxito");
        ApiResponse<FaseDTO> respuesta = new ApiResponse<>(true, "Fase: " + nombreFase + ", consultada con éxito", dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("proyecto/{proyecto}")
    public ResponseEntity<ApiResponse<List<FaseDTO>>> buscarPorIdProyecto(@PathVariable Long proyecto) {
        List<FaseDTO> lista = service.buscarPorIdProyecto(proyecto);
        log.info("Se consultaron las fases asociadas al proyecto con id: " + proyecto);
        ApiResponse<List<FaseDTO>> respuesta = new ApiResponse<>(true, "Se consultaron las fases asociadas al proyecto con id: " + proyecto, lista);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    // Al editar solo se valida el grupo Default: las fechas estimadas de una fase ya
    // existente pueden legitimamente haber quedado en el pasado (ver FaseDTO/ValidacionCrear).
    public ResponseEntity<ApiResponse<FaseDTO>> actualizarFase(@PathVariable Long id, @Valid @RequestBody FaseDTO dto) {
        FaseDTO data = service.actualizarFase(id, dto);
        log.info("Fase con id: " + id + ", ha sido actualizada");
        ApiResponse<FaseDTO> respuesta = new ApiResponse<>(true, "Fase con id: " + id + ", ha sido actualizada", data);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarFase(@PathVariable Long id) {
        boolean eliminado = service.eliminarFase(id);
        if (eliminado) {
            log.info("Fase: " + id + " eliminada");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Fase: " + id + " eliminada");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Fase: " + id + ", no ha sido encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FaseDTO>> actualizarCampoFase(@PathVariable Long id, @Valid @RequestBody PatchFaseDTO dto) {
        FaseDTO data = service.actualizarCampoFase(id, dto);
        log.info("Se ha actualizado la fase: " + id);
        ApiResponse<FaseDTO> respuesta = new ApiResponse<>(true, "Se ha actualizado la fase: " + id, data);
        return ResponseEntity.ok(respuesta);
    }
}
