package iTicket.Douglas.Proyectos.Controller;

import iTicket.Douglas.Proyectos.DTO.ProyectoDTO;
import iTicket.Douglas.Proyectos.DTO.ProyectoPaginaDTO;
import iTicket.Douglas.Proyectos.Service.ProyectoService;
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
@CrossOrigin
@RequestMapping("api/proyectos")
@RequiredArgsConstructor
public class ProyectoController {

    private final ProyectoService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ProyectoDTO>> nuevoProyecto(@Valid @RequestBody ProyectoDTO json) {
        ProyectoDTO dto = service.crearProyecto(json);
        log.info("Nuevo proyecto creado: " + dto);
        ApiResponse<ProyectoDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProyectoDTO>>> obtenerDatos() {
        List<ProyectoDTO> lista = service.obtenerTodo();
        log.info("Datos de proyectos consultados");
        ApiResponse<List<ProyectoDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/pagina")
    public ResponseEntity<ApiResponse<ProyectoPaginaDTO>> obtenerProyectosPaginados(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int tamano) {
        ProyectoPaginaDTO resultado = service.obtenerPaginado(pagina, tamano);
        log.info("Proyectos paginados consultados (página " + pagina + ")");
        ApiResponse<ProyectoPaginaDTO> respuesta = new ApiResponse<>(true, "Proyectos obtenidos", resultado);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProyectoDTO>> obtenerProyectoPorId(@PathVariable Long id) {
        ProyectoDTO dto = service.obtenerPorId(id);
        log.info("Se obtuvieron los datos del proyecto con ID: " + id);
        ApiResponse<ProyectoDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del proyecto con ID: " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarProyecto(@PathVariable Long id) {
        boolean eliminado = service.eliminarProyecto(id);
        if (eliminado) {
            log.info("Proyecto con ID: " + id + ", eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Proyecto con ID: " + id + ", eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Proyecto con ID: " + id + ", no fue encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProyectoDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody ProyectoDTO dto) {
        ProyectoDTO data = service.actualizarProyecto(id, dto);
        log.info("Proyecto con ID: " + id + " ha sido actualizado.");
        ApiResponse<ProyectoDTO> respuesta = new ApiResponse<>(true, "Proyecto con ID: " + id + " ha sido actualizado.", data);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/nombre")
    public ResponseEntity<ApiResponse<List<ProyectoDTO>>> buscarPorNombre(@RequestParam String nombre) {
        List<ProyectoDTO> lista = service.buscarPorNombre(nombre);
        log.info("Búsqueda de proyectos con nombre: " + nombre);
        ApiResponse<List<ProyectoDTO>> respuesta = new ApiResponse<>(true, "Búsqueda completada para: " + nombre, lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<ApiResponse<List<ProyectoDTO>>> buscarPorTipo(@PathVariable String tipo) {
        List<ProyectoDTO> lista = service.buscarPorTipo(tipo);
        log.info("Búsqueda de proyectos de tipo: " + tipo);
        ApiResponse<List<ProyectoDTO>> respuesta = new ApiResponse<>(true, "Búsqueda completada para tipo: " + tipo, lista);
        return ResponseEntity.ok(respuesta);
    }
}