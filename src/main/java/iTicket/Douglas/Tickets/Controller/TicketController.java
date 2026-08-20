package iTicket.Douglas.Tickets.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Tickets.DTO.*;
import iTicket.Douglas.Tickets.Service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService service;

    @PostMapping
    public ResponseEntity<ApiResponse<TicketDTO>> nuevoTicket(@Valid @RequestBody TicketDTO json) {
        TicketDTO dto = service.nuevoTicket(json);
        log.info("Nuevo ticket creado: " + dto);
        ApiResponse<TicketDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketDTO>>> obtenerDatos() {
        List<TicketDTO> lista = service.obtenerTodo();
        log.info("Datos de tickets consultados");
        ApiResponse<List<TicketDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDTO>> obtenerTicketPorId(@PathVariable Long id) {
        TicketDTO dto = service.buscarTicket(id);
        log.info("Se obtuvieron los datos del ticket con ID: " + id);
        ApiResponse<TicketDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del ticket con ID: " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTicket(@PathVariable Long id, @RequestParam Long idUsuario) {
        boolean eliminado = service.eliminarData(id, idUsuario);
        if (eliminado) {
            log.info("Ticket con ID: " + id + ", eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Ticket con ID: " + id + ", eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Ticket con ID: " + id + ", no fue encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody TicketDTO dto) {
        TicketDTO data = service.actualizarTicket(id, dto);
        log.info("Ticket con ID: " + id + " ha sido actualizado.");
        ApiResponse<TicketDTO> respuesta = new ApiResponse<>(true, "Ticket con ID: " + id + " ha sido actualizado.", data);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ApiResponse<TicketDTO>> buscarPorCodigo(@PathVariable String codigo) {
        TicketDTO data = service.buscarPorCodigo(codigo);
        log.info("Ticket encontrado con código: " + codigo);
        ApiResponse<TicketDTO> respuesta = new ApiResponse<>(true, "Ticket encontrado con código: " + codigo, data);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/asunto")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> buscarPorAsunto(@RequestParam String asunto) {
        List<TicketDTO> lista = service.buscarPorAsunto(asunto);
        log.info("Búsqueda de tickets con asunto: " + asunto);
        ApiResponse<List<TicketDTO>> respuesta = new ApiResponse<>(true, "Búsqueda completada para: " + asunto, lista);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}/asignacion")
    public ResponseEntity<ApiResponse<TicketAsignacionDTO>> asignarTicket(@PathVariable Long id, @Valid @RequestBody TicketAsignacionDTO dto, @RequestParam Long idUsuario) {
        boolean resultado = service.asignarTicket(id, dto, idUsuario);
        if (resultado) {
            log.info("Ticket con ID: " + id + " ha sido asignado.");
            ApiResponse<TicketAsignacionDTO> respuesta = new ApiResponse<>(true, "Ticket con ID: " + id + " ha sido asignado.", dto);
            return ResponseEntity.ok(respuesta);
        }
        ApiResponse<TicketAsignacionDTO> respuesta = new ApiResponse<>(false, "No se pudo completar la asignación del ticket con ID: " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PatchMapping("/{id}/reporte")
    public ResponseEntity<ApiResponse<TicketResolucionDTO>> reporteTicket(@PathVariable Long id, @Valid @RequestBody TicketResolucionDTO dto, @RequestParam Long idUsuario) {
        boolean resultado = service.reporteTicket(id, dto, idUsuario);
        if (resultado) {
            log.info("El reporte del ticket con ID: " + id + " ha sido registrado.");
            ApiResponse<TicketResolucionDTO> respuesta = new ApiResponse<>(true, "Reporte del ticket con ID: " + id + " registrado.", dto);
            return ResponseEntity.ok(respuesta);
        }
        ApiResponse<TicketResolucionDTO> respuesta = new ApiResponse<>(false, "No se pudo registrar el reporte del ticket con ID: " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @GetMapping("/indicadores/gestion/{idUsuario}")
    public ResponseEntity<ApiResponse<TickteIndicadoresEstadoDTO>> obtenerIndicadoresPorDepartamento(@PathVariable Long idUsuario) {
        TickteIndicadoresEstadoDTO dto = service.obtenerIndicadoresDepartamento(idUsuario);
        log.info("Indicadores de tickets consultados");
        ApiResponse<TickteIndicadoresEstadoDTO> respuesta = new ApiResponse<>(true, "Indicadores de estado obtenidos", dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/indicadores/{idUsuario}")
    public ResponseEntity<ApiResponse<TickteIndicadoresEstadoDTO>> obtenerIndicadoresPropios(@PathVariable Long idUsuario) {
        TickteIndicadoresEstadoDTO dto = service.obtenerIndicadoresPropios(idUsuario);
        log.info("Indicadores de tickets consultados");
        ApiResponse<TickteIndicadoresEstadoDTO> respuesta = new ApiResponse<>(true, "Indicadores de estado obtenidos", dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/resumen-semanal/{idUsuario}")
    public ResponseEntity<ApiResponse<List<TicketResumenDiaDTO>>> obtenerResumenSemanal(@PathVariable Long idUsuario) {
        List<TicketResumenDiaDTO> resumen = service.obtenerResumenSemanal(idUsuario);
        log.info("Resumen semanal de tickets consultado");
        ApiResponse<List<TicketResumenDiaDTO>> respuesta = new ApiResponse<>(true, "Resumen semanal obtenido", resumen);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/aprobaciones-pendientes")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> obtenerAprobacionesPendientes(@RequestParam(defaultValue = "5") int limite, @RequestParam Long idUsuarioAdmin) {
        List<TicketDTO> lista = service.obtenerAprobacionesPendientes(limite, idUsuarioAdmin);
        log.info("Se consultaron las aprobaciones pendientes");
        ApiResponse<List<TicketDTO>> respuesta = new ApiResponse<>(true, "Aprobaciones pendientes obtenidas", lista);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}/editar-creador")
    public ResponseEntity<ApiResponse<Void>> editarComoCreador(@PathVariable Long id, @Valid @RequestBody TicketEdicionCreadorDTO dto, @RequestParam Long idUsuario) {
        boolean resultado = service.editarComoCreador(id, dto, idUsuario);
        if (resultado) {
            log.info("Ticket con ID: " + id + " editado por el creador");
            return ResponseEntity.ok(new ApiResponse<>(true, "Ticket actualizado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Ticket con ID: " + id + " no encontrado"));
    }

    @PatchMapping("/{id}/gestion")
    public ResponseEntity<ApiResponse<Void>> editarComoGestor(@PathVariable Long id, @Valid @RequestBody TicketAsignacionDTO dto, @RequestParam Long idUsuario) {
        boolean resultado = service.editarComoAdmin(id, dto, idUsuario);
        if (resultado) {
            log.info("Ticket con ID: " + id + " editado por administrador");
            return ResponseEntity.ok(new ApiResponse<>(true, "Ticket actualizado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Ticket con ID: " + id + " no encontrado"));
    }

    @PatchMapping("/{id}/estado-asignado")
    public ResponseEntity<ApiResponse<Void>> editarEstadoAsignado(@PathVariable Long id, @Valid @RequestBody TicketEstadoDTO dto, @RequestParam Long idUsuario) {
        boolean resultado = service.editarEstado(id, dto, idUsuario);
        if (resultado) {
            log.info("Estado del ticket con ID: " + id + " actualizado por el usuario asignado");
            return ResponseEntity.ok(new ApiResponse<>(true, "Estado actualizado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Ticket con ID: " + id + " no encontrado"));
    }

    @GetMapping("/departamento")
    public ResponseEntity<ApiResponse<TicketPaginaDTO>> obtenerTicketsPorDepartamento(
            @RequestParam Long idUsuarioAdmin, @RequestParam(defaultValue = "1") int pagina, @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(required = false) String busqueda, @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        TicketPaginaDTO resultado = service.obtenerTicketsPorDepartamento(idUsuarioAdmin, pagina, tamano, busqueda, prioridad, estado, fecha);
        log.info("Tickets consultados por el usuario con ID: " + idUsuarioAdmin);
        ApiResponse<TicketPaginaDTO> respuesta = new ApiResponse<>(true, "Tickets obtenidos", resultado);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/tickets-asignados")
    public ResponseEntity<ApiResponse<TicketPaginaDTO>> obtenerPorTecnicoAsignado(
            @RequestParam Long idUsuario, @RequestParam(defaultValue = "1") int pagina, @RequestParam(defaultValue = "5") int tamano,
            @RequestParam(required = false) String busqueda, @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        TicketPaginaDTO resultado = service.obtenerTicketsAsignados(idUsuario, pagina, tamano, busqueda, prioridad, estado, fecha);
        log.info("Tickets asignados al usuario con ID: " + idUsuario + ", consultados");
        ApiResponse<TicketPaginaDTO> respuesta = new ApiResponse<>(true, "Tickets asignados obtenidos", resultado);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/mis-tickets")
    public ResponseEntity<ApiResponse<TicketPaginaDTO>> obtenerPorUsuario(
            @RequestParam Long idUsuario, @RequestParam(defaultValue = "1") int pagina, @RequestParam(defaultValue = "5") int tamano,
            @RequestParam(required = false) String busqueda, @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        TicketPaginaDTO resultado = service.obtenerTicketsPorUsuario(idUsuario, pagina, tamano, busqueda, prioridad, estado, fecha);
        log.info("Tickets del usuario con ID: " + idUsuario + ", consultados");
        ApiResponse<TicketPaginaDTO> respuesta = new ApiResponse<>(true, "Tickets obtenidos", resultado);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/reasignar/{id}")
    public ResponseEntity<ApiResponse<TicketReasignarDepDTO>> reasignarDepartamento(@PathVariable Long id, @Valid @RequestBody TicketReasignarDepDTO dto) {
        boolean resultado = service.reasignarDepartamento(id, dto);
        if (resultado) {
            log.info("El departamento del ticket con ID: " + id + ", se reasignó correctamente");
            ApiResponse<TicketReasignarDepDTO> respuesta = new ApiResponse<>(true, "El departamento del ticket con ID: " + id + ", se reasignó correctamente");
            return ResponseEntity.ok(respuesta);
        }
        ApiResponse<TicketReasignarDepDTO> respuesta = new ApiResponse<>(false, "No existe el ticket con ID: " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}