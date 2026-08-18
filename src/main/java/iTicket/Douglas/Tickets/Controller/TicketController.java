package iTicket.Douglas.Tickets.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Tickets.DTO.*;
import iTicket.Douglas.Tickets.Service.TicketService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/tickets")//Endpoint
public class TicketController {

    private final TicketService service;
    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TicketDTO>> nuevoTicket(@Valid @RequestBody TicketDTO json){
        try {
            TicketDTO dto = service.nuevoTicket(json); //Devuelve un TicketDTO que es la respuesta de la base
            if (dto != null){
                log.info("Nuevo ticket creado: " + dto);
                ApiResponse<TicketDTO> respuestaExito = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de insersión fallido: " + json);
            ApiResponse<TicketDTO> respuestaFallida = new ApiResponse<>(false, "Intento fallido de insersión");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó fallas inesperadas. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<TicketDTO> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketDTO>>> obtenerDatos(){
        try {
            List<TicketDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de tickets consultados");
                ApiResponse<List<TicketDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<TicketDTO>> respuestaNoEncontrada = new ApiResponse<>(false,"Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("El proceso presentó un fallo inesperado. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<List<TicketDTO>> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDTO>> obtenerTicketPorId(@PathVariable Long id){
        try {
            TicketDTO dto = service.buscarTicket(id);
            if (dto != null){
                log.info("Se obtuvieron los datos del ticket con ID: " + id);
                ApiResponse<TicketDTO> respuestaExito = new ApiResponse<>(true, "Se obtuvieron los datos del ticket con ID: " + id, dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados con ID: " + id);
            ApiResponse<TicketDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Datos no encontrados con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e){
            log.error("Error crítico al obtener del ticket con ID: " + id);
            e.printStackTrace();
            ApiResponse<TicketDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos del ticket");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTicket(@PathVariable Long id, @RequestParam Long idUsuario){
        try {
            boolean respuesta = service.eliminarData(id,idUsuario
            );
            if (respuesta){
                log.info("Ticket con ID: " + id+ ", eliminado");
                ApiResponse<Void> respuestExitosa = new ApiResponse<>(true, "Ticket con ID: " + id+ ", eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestExitosa);
            }
            log.info("Ticket con ID: " + id+ ", no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Ticket con ID: " + id+ ", no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al eliminar el ticket con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "Error crítico al eliminar la data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody TicketDTO dto){
        try {
            TicketDTO data = service.actualizarTicket(id, dto);
            if (data != null){
                log.info("Ticket con ID: "+ id + " ha sido actualizado.");
                ApiResponse<TicketDTO> respuestaExitosa = new ApiResponse<>(true, "Ticket con ID: " + id + " ha sido actualizado.", data);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización del ticket con ID: "+ id);
            ApiResponse<TicketDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización del ticket con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        }catch (Exception e){
            log.error("Error crítico al actualizar el ticket con ID: " + id);
            e.printStackTrace();
            ApiResponse<TicketDTO> respuestaError = new ApiResponse<>(false, "Error crítico al actualizar el ticket con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ApiResponse<TicketDTO>> buscarPorCodigo(@PathVariable String codigo){
        try {
            TicketDTO data = service.buscarPorCodigo(codigo);
            if (data != null){
                log.info("Ticket encontrado con código: " + codigo);
                ApiResponse<TicketDTO> respuestaExitosa = new ApiResponse<>(true, "Tickets encontrados con código: "+codigo, data);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("Datos no encontrados con código: " + codigo);
            ApiResponse<TicketDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Datos no encontrados con código: " + codigo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al obtener el ticket con código: " + codigo);
            e.printStackTrace();
            ApiResponse<TicketDTO> respuestaError = new ApiResponse<>(false, "Error crítico al obtener el ticket con código: " + codigo);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Proceso de búsqueda de tickets por asunto, devuelve una lista ya que busca varios por coincidencia
    @GetMapping("/asunto") //No usa @PathVariable ya que puede generar problemas si el asunto tiene espacios, caracteres especiales o es muy largo
    public ResponseEntity<ApiResponse<List<TicketDTO>>> buscarPorAsunto(@RequestParam String asunto){ //@RequestParam se utiliza para tomar el valor de los parametros de la query string de la URL (/tickets/asunto?asunto=impresora)
        try {
            List<TicketDTO> lista = service.buscarPorAsunto(asunto);
            if (!lista.isEmpty()){
                log.info("Tickets encontrados con asunto: " + asunto);
                ApiResponse<List<TicketDTO>> respuestExitosa = new ApiResponse<>(true, "Tickets encontrados con asunto: " + asunto, lista);
                return ResponseEntity.ok(respuestExitosa);
            }
            log.warn("Datos no encontrados con asunto: " + asunto);
            ApiResponse<List<TicketDTO>> respuestaNoEncontrada = new ApiResponse<>(false, "Tickets no encontrados con asunto: " + asunto);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al obtener ticket con asunto: " + asunto);
            e.printStackTrace();
            ApiResponse<List<TicketDTO>> respuestaError = new ApiResponse<>(false, "Error crítico al obtener tickets con asunto: " + asunto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PatchMapping("/{id}/asignacion")
    public ResponseEntity<ApiResponse<TicketAsignacionDTO>> asignarTicket(@PathVariable Long id, @Valid @RequestBody TicketAsignacionDTO dto, @RequestParam Long idUsuario) { //@RequestParam Long idUsuario debe sustituirse
        try {
            boolean resultado = service.asignarTicket(id, dto, idUsuario);
            if (resultado){
                log.info("Ticket con ID: "+ id + " ha sido actualizado.");
                ApiResponse<TicketAsignacionDTO> respuestaExitosa = new ApiResponse<>(true, "Ticket con ID: " + id + " ha sido actualizado.", dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización del ticket con ID: "+ id);
            ApiResponse<TicketAsignacionDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización del ticket con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico al actualizar los datos. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<TicketAsignacionDTO> respuestaError = new ApiResponse<>(false, "Error crítico al actualizar el ticket con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PatchMapping("/{id}/reporte")
    public ResponseEntity<ApiResponse<TicketResolucionDTO>> resporteTicket(@PathVariable Long id, @Valid @RequestBody TicketResolucionDTO dto, @RequestParam Long idUsuario) { //@RequestParam Long idUsuario es temporal, debe sustituirse
        try {
            boolean resultado = service.reporteTicket(id, dto, idUsuario);
            if (resultado){
                log.info("El reporte del ticket con ID: "+ id + " ha sido actualizado.");
                ApiResponse<TicketResolucionDTO> respuestaExitosa = new ApiResponse<>(true, "Ticket con ID: " + id + " ha sido actualizado.", dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización del ticket con ID: "+ id);
            ApiResponse<TicketResolucionDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización del reporte del ticket con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico al actualizar los datos. Consulte con el administrador");
            e.printStackTrace();
            ApiResponse<TicketResolucionDTO> respuestaError = new ApiResponse<>(false, "Error crítico al actualizar el reporte del ticket con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/indicadores/gestion/{idUsuario}")
    public ResponseEntity<ApiResponse<TickteIndicadoresEstadoDTO>> obtenerIndicadoresPorDepartamento(@PathVariable Long idUsuario){
        try {
            TickteIndicadoresEstadoDTO dto = service.obtenerIndicadoresDepartamento(idUsuario);
            log.info("Indicadores de tickets consultados");
            ApiResponse<TickteIndicadoresEstadoDTO> respuesta = new ApiResponse<>(true, "Indicadores de estado obtenidos", dto);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener los indicadores de estado de los tickets");
            e.printStackTrace();
            ApiResponse<TickteIndicadoresEstadoDTO> respuestaError = new ApiResponse<>(false, "No se pudieron obtener los indicadores de estado de los tickets");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/indicadores/{idUsuario}")
    public ResponseEntity<ApiResponse<TickteIndicadoresEstadoDTO>> obtenerIndicadoresPropios(@PathVariable Long idUsuario){
        try {
            TickteIndicadoresEstadoDTO dto = service.obtenerIndicadoresPropios(idUsuario);
            log.info("Indicadores de tickets consultados");
            ApiResponse<TickteIndicadoresEstadoDTO> respuesta = new ApiResponse<>(true, "Indicadores de estado obtenidos", dto);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener los indicadores de estado de los tickets");
            e.printStackTrace();
            ApiResponse<TickteIndicadoresEstadoDTO> respuestaError = new ApiResponse<>(false, "No se pudieron obtener los indicadores de estado de los tickets");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/resumen-semanal/{idUsuario}")
    public ResponseEntity<ApiResponse<List<TicketResumenDiaDTO>>> obtenerResumenSemanal(@PathVariable Long idUsuario){
        try {
            List<TicketResumenDiaDTO> resumen = service.obtenerResumenSemanal(idUsuario);
            log.info("Resumen semanal de tickets consultado");
            ApiResponse<List<TicketResumenDiaDTO>> respuesta = new ApiResponse<>(true, "Resumen semanal obtenido", resumen);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener el resumen semanal de tickets");
            e.printStackTrace();
            ApiResponse<List<TicketResumenDiaDTO>> respuestaError = new ApiResponse<>(false, "No se pudo obtener el resumen semanal");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/aprobaciones-pendientes")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> obtenerAprobacionesPendientes(@RequestParam(defaultValue = "5") int limite, @RequestParam Long idUsuarioAdmin) {
        try {
            List<TicketDTO> lista = service.obtenerAprobacionesPendientes(limite, idUsuarioAdmin);
            log.info("Se consultaron las aprobaciones pendientes" );
            ApiResponse<List<TicketDTO>> respuesta = new ApiResponse<>(true, "Aprobaciones pendientes obtenidas", lista);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener aprobaciones pendientes");
            e.printStackTrace();
            ApiResponse<List<TicketDTO>> respuestaError = new ApiResponse<>(false, "No se pudieron obtener las aprobaciones pendientes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PatchMapping("/{id}/editar-creador")
    public ResponseEntity<ApiResponse<Void>> editarComoCreador(@PathVariable Long id, @Valid @RequestBody TicketEdicionCreadorDTO dto, @RequestParam Long idUsuario) { //Temporal hasta implementar JWT
        try {
            boolean resultado = service.editarComoCreador(id, dto, idUsuario);
            if (resultado) {
                log.info("Ticket con ID: " + id + " editado por el creador");
                return ResponseEntity.ok(new ApiResponse<>(true, "Ticket actualizado correctamente"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Ticket con ID: " + id + " no encontrado"));
        } catch (RuntimeException e) {
            log.warn("No se pudo editar el ticket con ID: " + id + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Error crítico al editar el ticket con ID: " + id);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "El proceso no se pudo completar"));
        }
    }

    @PatchMapping("/{id}/gestion")
    public ResponseEntity<ApiResponse<Void>> editarComoGestor(@PathVariable Long id, @Valid @RequestBody TicketAsignacionDTO dto, @RequestParam Long idUsuario) {
        try {
            boolean resultado = service.editarComoAdmin(id, dto, idUsuario);
            if (resultado) {
                log.info("Ticket con ID: " + id + " editado por administrador");
                return ResponseEntity.ok(new ApiResponse<>(true, "Ticket actualizado correctamente"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Ticket con ID: " + id + " no encontrado"));
        } catch (RuntimeException e) {
            log.warn("No se pudo editar el ticket con ID: " + id + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Error crítico al editar el ticket con ID: " + id);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "El proceso no se pudo completar"));
        }
    }

    @PatchMapping("/{id}/estado-asignado")
    public ResponseEntity<ApiResponse<Void>> editarEstadoAsignado(@PathVariable Long id, @Valid @RequestBody TicketEstadoDTO dto, @RequestParam Long idUsuario) {
        try {
            boolean resultado = service.editarEstado(id, dto, idUsuario);
            if (resultado) {
                log.info("Estado del ticket con ID: " + id + " actualizado por el usuario asignado");
                return ResponseEntity.ok(new ApiResponse<>(true, "Estado actualizado correctamente"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Ticket con ID: " + id + " no encontrado"));
        } catch (RuntimeException e) {
            log.warn("No se pudo actualizar el estado del ticket con ID: " + id + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Error crítico al actualizar el estado del ticket con ID: " + id);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "El proceso no se pudo completar"));
        }
    }

    @GetMapping("/departamento")
    public ResponseEntity<ApiResponse<TicketPaginaDTO>> obtenerTicketsPorDepartamento(@RequestParam Long idUsuarioAdmin, @RequestParam(defaultValue = "1") int pagina, @RequestParam(defaultValue = "10") int tamano, @RequestParam(required = false) String busqueda, @RequestParam(required = false) String prioridad, @RequestParam(required = false) String estado, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            TicketPaginaDTO resultado = service.obtenerTicketsPorDepartamento(idUsuarioAdmin, pagina, tamano, busqueda, prioridad, estado, fecha);
            log.info("Tickets consultados por el usuario con ID: " + idUsuarioAdmin);
            ApiResponse<TicketPaginaDTO> respuesta = new ApiResponse<>(true, "Tickets obtenidos", resultado);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener los tickets del departamento");
            e.printStackTrace();
            ApiResponse<TicketPaginaDTO> respuestaError = new ApiResponse<>(false, "No se pudieron obtener los tickets");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/tickets-asignados")
    public ResponseEntity<ApiResponse<TicketPaginaDTO>> obtenerPorTecnicoAsignado(@RequestParam Long idUsuario, @RequestParam(defaultValue = "1") int pagina, @RequestParam(defaultValue = "5") int tamano, @RequestParam(required = false) String busqueda, @RequestParam(required = false) String prioridad, @RequestParam(required = false) String estado, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            TicketPaginaDTO resultado = service.obtenerTicketsAsignados(idUsuario, pagina, tamano, busqueda, prioridad, estado, fecha);
            log.info("Tickets asignados al usuario con ID: " + idUsuario + ", consultados");
            ApiResponse<TicketPaginaDTO> respuesta = new ApiResponse<>(true, "Tickest asignados obtenidos", resultado);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener los tickest asignados al usuario con ID: " + idUsuario);
            e.printStackTrace();
            ApiResponse<TicketPaginaDTO> respuestaError = new ApiResponse<>(false, "No se pudieron obtener los tickets asignados");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/mis-tickets")
    public ResponseEntity<ApiResponse<TicketPaginaDTO>> obtenerPorUsuario(@RequestParam Long idUsuario, @RequestParam(defaultValue = "1") int pagina, @RequestParam(defaultValue = "5") int tamano, @RequestParam(required = false) String busqueda, @RequestParam(required = false) String prioridad, @RequestParam(required = false) String estado, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            TicketPaginaDTO resultado = service.obtenerTicketsPorUsuario(idUsuario, pagina, tamano, busqueda, prioridad, estado, fecha);
            log.info("Tickets del usuario con ID: " + idUsuario + ", consultados");
            ApiResponse<TicketPaginaDTO> respuesta = new ApiResponse<>(true, "Tickest obtenidos", resultado);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener los tickest del usuario con ID: " + idUsuario);
            e.printStackTrace();
            ApiResponse<TicketPaginaDTO> respuestaError = new ApiResponse<>(false, "No se pudieron obtener los tickets");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PatchMapping("/reasignar/{id}")
    public  ResponseEntity<ApiResponse<TicketReasignarDepDTO>> reasignarDepartamento(@PathVariable Long id, @Valid @RequestBody TicketReasignarDepDTO dto){
        try {
            boolean resultado = service.reasignarDepartamento(id, dto);
            if (resultado){
                ApiResponse<TicketReasignarDepDTO> respuesta = new ApiResponse<>(true, "El departamento del ticket con ID: "+ id + ", se reasignó corretamente");
                log.info("El departamento del ticket con ID: "+ id + ", se reasignó corretamente");
                return ResponseEntity.ok(respuesta);
            }
            ApiResponse<TicketReasignarDepDTO> respestError = new ApiResponse<>(false, "No existe el ticket con ID: " + id );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respestError);
        }catch (Exception e) {
            log.error("Error al reasignar el departamento de ticket con ID: " + id);
            e.printStackTrace();
            ApiResponse<TicketReasignarDepDTO> respuestaError = new ApiResponse<>(false, "Error al reasignar el departamento de ticket con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
