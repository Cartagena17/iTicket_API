package iTicket.Douglas.Tickets.Service;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import iTicket.Douglas.Articulos.Repository.ArticuloRepository;
import iTicket.Douglas.Bitacoras.DTO.BitacoraDTO;
import iTicket.Douglas.Bitacoras.Repository.BitacoraRepository;
import iTicket.Douglas.Bitacoras.Service.BitacoraService;
import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Departamentos.Repository.DepartamentoRepository;
import iTicket.Douglas.DetalleGeneral.DTO.DetalleGDTO;
import iTicket.Douglas.DetalleGeneral.Entity.DetalleGEntity;
import iTicket.Douglas.DetalleGeneral.Repository.DetalleGRepository;
import iTicket.Douglas.DetalleGeneral.Service.DetalleGService;
import iTicket.Douglas.DetalleTA.DTO.DetalleTADTO;
import iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity;
import iTicket.Douglas.DetalleTA.Repository.DetalleTARepository;
import iTicket.Douglas.DetalleTA.Service.DetalleTAService;
import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import iTicket.Douglas.DetalleTS.Repository.DetalleTSRepository;
import iTicket.Douglas.DetalleTS.Service.DetalleTSService;
import iTicket.Douglas.Evidencias.Entity.EvidenciaEntity;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Tickets.DTO.*;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Tickets.Specification.TicketSpecifications;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository repo;
    private final DepartamentoRepository departamentosRepo;
    private final UsuarioRepository usuariosRepo;
    private final DetalleGService detalleGService;
    private final DetalleGRepository detalleGRepo;
    private final DetalleTAService detalleTAService;
    private final DetalleTARepository detalleTARepo;
    private final DetalleTSService detalleTSService;
    private final DetalleTSRepository detalleTSRepo;
    private final BitacoraService bitacoraService;
    private final BitacoraRepository bitacoraRepo;
    private final ArticuloRepository articuloRepo;

    @Transactional
    public TicketDTO nuevoTicket(@Valid TicketDTO dto) {
        dto.setCodigo(generarCodigo());
        dto.setEstado("Nuevo");
        TicketEntity entity = convertirAEntity(dto);
        TicketEntity entitySave = repo.save(entity);

        crearDetalleSegunTipo(entitySave, dto);
        registrarBitacora(entitySave, entitySave.getCreador().getIdUsuario());
        log.info("Nuevo ticket registrado: " + entitySave.getCodigo());
        return convertirADTOCompleto(entitySave);
    }

    @Transactional
    private void crearDetalleSegunTipo(TicketEntity ticket, TicketDTO dto) {
        switch (dto.getTipoTicket()) {
            case "General" -> {
                if (dto.getDescripcionUbicacion() == null || dto.getDescripcionUbicacion().isBlank()) {
                    throw new OperacionInvalidaException("Debe indicar la ubicación del problema.");
                }
                DetalleGDTO detalleDto = new DetalleGDTO();
                detalleDto.setTicket(ticket.getIdTicket());
                detalleDto.setDescripcionUbicacion(dto.getDescripcionUbicacion());
                detalleGService.nuevoDetalleG(detalleDto);
            }
            case "Articulo" -> {
                if (dto.getCodigosArticulos() == null || dto.getCodigosArticulos().isEmpty()) {
                    throw new OperacionInvalidaException("Debe agregar al menos un código de equipo/mobiliario.");
                }

                List<ArticuloEntity> articulos = new ArrayList<>();
                for (String codigo : dto.getCodigosArticulos()) {
                    ArticuloEntity articulo = articuloRepo.findByCodigoArticulo(codigo)
                            .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún artículo con código: " + codigo));
                    articulos.add(articulo);
                }

                long ubicacionesDistintas = articulos.stream().map(a -> a.getUbicacion().getId()).distinct().count();

                if (ubicacionesDistintas > 1) {
                    throw new OperacionInvalidaException("Los artículos seleccionados deben estar en la misma ubicación. No puedes reportar en un solo ticket equipos de distintos lugares.");
                }

                for (ArticuloEntity articulo : articulos) {
                    DetalleTADTO detalleDto = new DetalleTADTO();
                    detalleDto.setIdTicket(ticket.getIdTicket());
                    detalleDto.setIdArticulo(articulo.getIdArticulo());
                    detalleTAService.nuevoDetalle(detalleDto);
                }
            }
            case "Software" -> {
                if (dto.getDetallesSoftware() == null || dto.getDetallesSoftware().isEmpty()) {
                    throw new OperacionInvalidaException("Debe agregar al menos un software a instalar.");
                }
                for (DetalleTSDTO sw : dto.getDetallesSoftware()) {
                    sw.setTicket(ticket.getIdTicket());
                    detalleTSService.nuevoDetalleTS(sw);
                }
            }
            default -> throw new OperacionInvalidaException("Tipo de ticket no reconocido: " + dto.getTipoTicket());
        }
    }

    @Transactional
    private void registrarBitacora(TicketEntity ticket, Long idUsuarioAccion) {
        BitacoraDTO bitacoraDto = new BitacoraDTO();
        bitacoraDto.setIdTicket(ticket.getIdTicket());
        bitacoraDto.setCodigoTicket(ticket.getCodigo());
        bitacoraDto.setAsuntoTicket(ticket.getAsunto());
        bitacoraDto.setUsuario(idUsuarioAccion);
        bitacoraDto.setNuevoEstado(ticket.getEstado());
        bitacoraService.nuevaBitacora(bitacoraDto);
    }

    public String generarCodigo() {
        LocalDate hoy = LocalDate.now();
        String fecha = hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long siguienteValor = repo.obtenerSiguienteCodigo();
        String correlativo = String.format("%05d", siguienteValor);
        return "#" + fecha + "-" + correlativo;
    }

    public List<TicketDTO> obtenerTodo() {
        List<TicketEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public TicketDTO buscarTicket(Long id) {
        TicketEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un ticket con id " + id));
        return convertirADTOCompleto(entidad);
    }

    @Transactional
    public boolean eliminarData(Long id, Long idUsuarioSolicitante) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();
        UsuarioEntity solicitante = buscarUsuario(idUsuarioSolicitante);
        String rolSolicitante = solicitante.getRol().getNombreRol();
        boolean esCreador = ticket.getCreador().getIdUsuario().equals(idUsuarioSolicitante);
        boolean esAdmin = "Administrador".equalsIgnoreCase(rolSolicitante);

        if (esAdmin) {
            boolean resuelto = "Resuelto".equals(ticket.getEstado());
            boolean cerrado = "Cerrado".equals(ticket.getEstado());
            if (resuelto || cerrado) {
                throw new OperacionInvalidaException("No se puede eliminar un ticket ya resuelto y evaluado.");
            }
        } else if (esCreador) {
            if (!"Nuevo".equals(ticket.getEstado())) {
                throw new OperacionInvalidaException("No puedes eliminar un ticket que ya fue asignado.");
            }
        } else {
            throw new OperacionInvalidaException("No tienes permisos para eliminar este ticket.");
        }

        repo.delete(ticket);
        log.info("Ticket con id " + id + " eliminado");
        return true;
    }

    @Transactional
    public TicketDTO actualizarTicket(Long id, @Valid TicketDTO dto) {
        TicketEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un ticket con id " + id));

        entidad.setAsunto(dto.getAsunto());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setDepartamento(buscarDepartamento(dto.getDepartamento()));
        entidad.setDescripcionFalla(dto.getDescripcionFalla());
        entidad.setDescripcionSolucion(dto.getDescripcionSolucion());
        entidad.setFechaVencimiento(dto.getFechaVencimiento());
        if (dto.getTecnicoAsignado() != null) {
            entidad.setTecnicoAsignado(buscarUsuario(dto.getTecnicoAsignado()));
        }
        entidad.setPrioridad(dto.getPrioridad());
        entidad.setTipoTicket(dto.getTipoTicket());
        entidad.setEstado(dto.getEstado());

        TicketEntity datosGuardados = repo.save(entidad);
        log.info("Ticket con id " + id + " actualizado");
        return convertirADTOCompleto(datosGuardados);
    }

    public TicketDTO buscarPorCodigo(String codigo) {
        TicketEntity entidad = repo.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún ticket con código: " + codigo));
        return convertirADTO(entidad);
    }

    public List<TicketDTO> buscarPorAsunto(String asunto) {
        List<TicketEntity> registros = repo.findByAsuntoContainingIgnoreCase(asunto);
        return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private DepartamentoEntity buscarDepartamento(Long id) {
        return departamentosRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún departamento con ID: " + id));
    }

    private UsuarioEntity buscarUsuario(Long id) {
        return usuariosRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún usuario con ID: " + id));
    }

    private TicketEntity convertirAEntity(@Valid TicketDTO dto) {
        TicketEntity objEntity = new TicketEntity();
        objEntity.setCodigo(dto.getCodigo());
        objEntity.setAsunto(dto.getAsunto());
        objEntity.setDescripcion(dto.getDescripcion());
        objEntity.setDepartamento(buscarDepartamento(dto.getDepartamento()));
        objEntity.setDescripcionFalla(dto.getDescripcionFalla());
        objEntity.setDescripcionSolucion(dto.getDescripcionSolucion());
        objEntity.setFechaVencimiento(dto.getFechaVencimiento());
        objEntity.setPrioridad(dto.getPrioridad());
        objEntity.setTipoTicket(dto.getTipoTicket());
        objEntity.setCreador(buscarUsuario(dto.getCreador()));
        objEntity.setEstado(dto.getEstado());
        if (dto.getTecnicoAsignado() != null) {
            objEntity.setTecnicoAsignado(buscarUsuario(dto.getTecnicoAsignado()));
        }
        return objEntity;
    }

    private TicketDTO convertirADTO(@Valid TicketEntity entity) {
        TicketDTO objDTO = new TicketDTO();

        objDTO.setIdTicket(entity.getIdTicket());
        objDTO.setCodigo(entity.getCodigo());
        objDTO.setAsunto(entity.getAsunto());
        objDTO.setDescripcion(entity.getDescripcion());
        objDTO.setDepartamento(entity.getDepartamento().getIdDepartamento());
        objDTO.setNombreDepartamento(entity.getDepartamento().getNombreDepartamento());
        objDTO.setDescripcionFalla(entity.getDescripcionFalla());
        objDTO.setDescripcionSolucion(entity.getDescripcionSolucion());
        objDTO.setFechaVencimiento(entity.getFechaVencimiento());
        objDTO.setPrioridad(entity.getPrioridad());
        objDTO.setTipoTicket(entity.getTipoTicket());
        objDTO.setEstado(entity.getEstado());
        objDTO.setCreador(entity.getCreador().getIdUsuario());
        objDTO.setNombreCreador(entity.getCreador().getNombreUsuario());
        objDTO.setCorreoCreador(entity.getCreador().getCorreo());
        objDTO.setFechaCreacion(entity.getFechaCreacion());
        if (entity.getTecnicoAsignado() != null) {
            objDTO.setTecnicoAsignado(entity.getTecnicoAsignado().getIdUsuario());
            objDTO.setNombreTecnico(entity.getTecnicoAsignado().getNombreUsuario());
            objDTO.setCorreoTecnico(entity.getTecnicoAsignado().getCorreo());
        }

        return objDTO;
    }

    private TicketDTO convertirADTOCompleto(TicketEntity entity) {
        TicketDTO objDTO = convertirADTO(entity);
        obtenerDetallePorTipo(entity, objDTO);
        return objDTO;
    }

    @Transactional
    public boolean asignarTicket(Long id, @Valid TicketAsignacionDTO dto, Long idUsuarioAdmin) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();
        ticket.setFechaVencimiento(dto.getFechaVencimiento());
        ticket.setTecnicoAsignado(buscarUsuario(dto.getTecnicoAsignado()));
        ticket.setPrioridad(dto.getPrioridad());
        ticket.setEstado("Asignado");

        repo.save(ticket);
        registrarBitacora(ticket, idUsuarioAdmin);
        return true;
    }

    @Transactional
    public boolean reporteTicket(Long id, @Valid TicketResolucionDTO dto, Long idUsuarioTecnico) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();

        if (ticket.getTecnicoAsignado() == null || !ticket.getTecnicoAsignado().getIdUsuario().equals(idUsuarioTecnico)) {
            throw new OperacionInvalidaException("Solo el usuario asignado puede hacer un reporte de este ticket");
        }
        if (!List.of("En proceso", "En espera", "Vencido", "Resuelto").contains(ticket.getEstado())) {
            throw new OperacionInvalidaException("El ticket no se encuentra en un estado que permita generar el reporte");
        }
        ticket.setDescripcionFalla(dto.getDescripcionFalla());
        ticket.setDescripcionSolucion(dto.getDescripcionSolucion());
        ticket.setEstado("Resuelto");
        repo.save(ticket);
        registrarBitacora(ticket, idUsuarioTecnico);
        return true;
    }

    public TickteIndicadoresEstadoDTO obtenerIndicadoresDepartamento(Long idUsuario) {
        UsuarioEntity usuario = buscarUsuario(idUsuario);
        String tipoAdmin = usuario.getDepartamento().getTipoDepartamento();

        // 'Otro' no atiende tickets: sus indicadores van en cero.
        List<Object[]> filas = (tipoAdmin == null || "Otro".equalsIgnoreCase(tipoAdmin))
                ? List.of()
                : repo.contarTicketsPorEstadoYDepartamento(tipoAdmin);
        return construirIndicadores(filas);
    }

    public TickteIndicadoresEstadoDTO obtenerIndicadoresPropios(Long idUsuario) {
        List<Object[]> filas = repo.contarTicketsPorEstadoUsuario(idUsuario);
        return construirIndicadores(filas);
    }

    private TickteIndicadoresEstadoDTO construirIndicadores(List<Object[]> filas) {
        Map<String, Long> conteos = new HashMap<>();
        for (Object[] fila : filas) {
            conteos.put((String) fila[0], (Long) fila[1]);
        }

        long resueltos = conteos.getOrDefault("Resuelto", 0L);
        long asignados = conteos.getOrDefault("Asignado", 0L);
        long enProgreso = conteos.getOrDefault("En proceso", 0L);
        long enEspera = conteos.getOrDefault("En espera", 0L);
        long cerrados = conteos.getOrDefault("Cerrado", 0L);
        long nuevos = conteos.getOrDefault("Nuevo", 0L);
        long vencidos = conteos.getOrDefault("Vencido", 0L);

        return new TickteIndicadoresEstadoDTO(resueltos, asignados, enProgreso, enEspera, nuevos, cerrados, vencidos);
    }

    public List<TicketDTO> obtenerAprobacionesPendientes(int limite, Long idUsuarioAdmin) {
        UsuarioEntity admin = buscarUsuario(idUsuarioAdmin);
        String tipoAdmin = admin.getDepartamento().getTipoDepartamento();

        // Un admin de un departamento 'Otro' no atiende tickets, no tiene nada que aprobar
        if (tipoAdmin == null || "Otro".equalsIgnoreCase(tipoAdmin)) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, limite, Sort.by("idTicket").ascending());
        Page<TicketEntity> pagina = repo.findByEstadoAndDepartamento_TipoDepartamento("Nuevo", tipoAdmin, pageable);
        return pagina.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private void obtenerDetallePorTipo(TicketEntity entity, TicketDTO objDTO) {
        objDTO.setEvidencias(entity.getEvidencias().stream().map(EvidenciaEntity::getEvidenciaUrl).collect(Collectors.toList()));
        switch (entity.getTipoTicket()) {
            case "General":
                DetalleGEntity detalle = detalleGRepo.findByTicket_IdTicket(entity.getIdTicket())
                        .orElseThrow(() -> new RecursoNoEncontradoException("El ticket " + entity.getCodigo() + " es de tipo General pero no tiene detalle registrado."));
                objDTO.setUbicacion(detalle.getDescripcionUbicacion());
                break;
            case "Articulo":
                List<DetalleTAEntity> detalles = detalleTARepo.findByTicket_IdTicket(entity.getIdTicket());
                if (detalles.isEmpty()) {
                    throw new RecursoNoEncontradoException("El ticket " + entity.getCodigo() + " es de tipo Articulo pero no tiene ningún detalle registrado.");
                }
                objDTO.setCodigosArticulos(detalles.stream().map(d -> d.getArticulo().getCodigoArticulo()).collect(Collectors.toList()));
                detalles.stream().findFirst().ifPresent(d -> objDTO.setUbicacion(d.getArticulo().getUbicacion().getNombreUbicacion()));
                break;
            case "Software":
                List<DetalleTSEntity> detallesS = detalleTSRepo.findByTicket_IdTicket(entity.getIdTicket());
                if (detallesS.isEmpty()) {
                    throw new RecursoNoEncontradoException("El ticket " + entity.getCodigo() + " es de tipo Software pero no tiene ningún detalle registrado.");
                }
                objDTO.setDetallesSoftware(detallesS.stream().map(d -> {
                    DetalleTSDTO detallesSoftware = new DetalleTSDTO();
                    detallesSoftware.setNombreSoftware(d.getNombreSoftware());
                    detallesSoftware.setVersion(d.getVersion());
                    detallesSoftware.setDescripcionUbicaciones(d.getDescripcionUbicaciones());
                    return detallesSoftware;
                }).collect(Collectors.toList()));
                detallesS.stream().findFirst().ifPresent(d -> objDTO.setUbicacion(d.getDescripcionUbicaciones()));
                break;
        }
    }

    @Transactional
    public boolean editarComoCreador(Long id, @Valid TicketEdicionCreadorDTO dto, Long idUsuario) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();

        if (!ticket.getCreador().getIdUsuario().equals(idUsuario)) {
            throw new OperacionInvalidaException("El usuario no tiene permiso para editar este ticket");
        }
        if (!"Nuevo".equals(ticket.getEstado())) {
            throw new OperacionInvalidaException("Solo se puede editar el ticket mientras está en estado 'Nuevo'");
        }

        ticket.setAsunto(dto.getAsunto());
        ticket.setDescripcion(dto.getDescripcion());
        ticket.setDepartamento(buscarDepartamento(dto.getDepartamento()));
        repo.save(ticket);

        actualizarDetalleSegunTipo(ticket, dto);
        return true;
    }

    @Transactional
    private void actualizarDetalleSegunTipo(TicketEntity ticket, TicketEdicionCreadorDTO dto) {
        switch (ticket.getTipoTicket()) {
            case "General" -> {
                if (dto.getDescripcionUbicacion() == null || dto.getDescripcionUbicacion().isBlank()) {
                    throw new OperacionInvalidaException("Debe indicar la ubicación del problema.");
                }
                DetalleGDTO detalleDto = new DetalleGDTO();
                detalleDto.setTicket(ticket.getIdTicket());
                detalleDto.setDescripcionUbicacion(dto.getDescripcionUbicacion());
                detalleGService.actualizarDetalle(detalleDto);
            }
            case "Articulo" -> {
                if (dto.getCodigosArticulos() == null || dto.getCodigosArticulos().isEmpty()) {
                    throw new OperacionInvalidaException("Debe agregar al menos un código de equipo/mobiliario.");
                }
                detalleTAService.reemplazarDetalles(ticket, dto.getCodigosArticulos());
            }
            case "Software" -> {
                if (dto.getDetallesSoftware() == null || dto.getDetallesSoftware().isEmpty()) {
                    throw new OperacionInvalidaException("Debe agregar al menos un software a instalar.");
                }
                detalleTSService.reemplazarDetalles(ticket, dto.getDetallesSoftware());
            }
            default -> throw new OperacionInvalidaException("Tipo de ticket no reconocido: " + ticket.getTipoTicket());
        }
    }

    @Transactional
    public boolean editarComoAdmin(Long id, @Valid TicketAsignacionDTO dto, Long idUsuarioAdmin) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();

        List<String> estadosReasignables = List.of("Asignado", "En proceso", "En espera", "Vencido");
        if (!estadosReasignables.contains(ticket.getEstado())) {
            throw new OperacionInvalidaException("El ticket ya no puede reasignarse en su estado actual: " + ticket.getEstado());
        }

        ticket.setFechaVencimiento(dto.getFechaVencimiento());
        ticket.setPrioridad(dto.getPrioridad());
        ticket.setTecnicoAsignado(buscarUsuario(dto.getTecnicoAsignado()));
        ticket.setEstado("Asignado");

        repo.save(ticket);
        registrarBitacora(ticket, idUsuarioAdmin);
        return true;
    }

    @Transactional
    public boolean editarEstado(Long id, @Valid TicketEstadoDTO dto, Long idUsuario) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();

        if (ticket.getTecnicoAsignado() == null || !ticket.getTecnicoAsignado().getIdUsuario().equals(idUsuario)) {
            throw new OperacionInvalidaException("El usuario no tiene permiso para cambiar el estado de este ticket");
        }

        List<String> estadosPermitidos = List.of("En proceso", "En espera");
        if (!estadosPermitidos.contains(dto.getEstado())) {
            throw new OperacionInvalidaException("Solo puedes mover el ticket entre 'En proceso' y 'En espera'. Para marcarlo como resuelto, crea el reporte técnico.");
        }
        if (!List.of("Asignado", "En proceso", "En espera").contains(ticket.getEstado())) {
            throw new OperacionInvalidaException("El ticket no se encuentra en un estado que permita este cambio");
        }

        ticket.setEstado(dto.getEstado());
        repo.save(ticket);
        registrarBitacora(ticket, idUsuario);
        return true;
    }

    @Transactional
    public boolean reasignarDepartamento(Long id, @Valid TicketReasignarDepDTO dto) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();
        ticket.setDepartamento(buscarDepartamento(dto.getDepartamento()));
        repo.save(ticket);
        return true;
    }

    public TicketPaginaDTO obtenerTicketsPorDepartamento(Long idUsuarioAdmin, int pagina, int tamano, String busqueda, String prioridad, String estado, LocalDate fecha) {
        UsuarioEntity admin = buscarUsuario(idUsuarioAdmin);
        String tipoAdmin = admin.getDepartamento().getTipoDepartamento();

        Specification<TicketEntity> spec = TicketSpecifications.conTipoDepartamento(tipoAdmin);
        spec = aplicarFiltrosComunes(spec, busqueda, prioridad, estado, fecha);

        return paginarTickets(spec, pagina, tamano);
    }

    public TicketPaginaDTO obtenerTicketsAsignados(Long idUsuario, int pagina, int tamano, String busqueda, String prioridad, String estado, LocalDate fecha) {
        Specification<TicketEntity> spec = TicketSpecifications.conTecnico(idUsuario);
        spec = aplicarFiltrosComunes(spec, busqueda, prioridad, estado, fecha);

        return paginarTickets(spec, pagina, tamano);
    }

    public TicketPaginaDTO obtenerTicketsPorUsuario(Long idUsuario, int pagina, int tamano, String busqueda, String prioridad, String estado, LocalDate fecha) {
        Specification<TicketEntity> spec = TicketSpecifications.conUsuario(idUsuario);
        spec = aplicarFiltrosComunes(spec, busqueda, prioridad, estado, fecha);

        return paginarTickets(spec, pagina, tamano);
    }

    private Specification<TicketEntity> aplicarFiltrosComunes(Specification<TicketEntity> spec, String busqueda, String prioridad, String estado, LocalDate fecha) {
        if (busqueda != null && !busqueda.isBlank()) {
            spec = spec.and(TicketSpecifications.conBusqueda(busqueda));
        }
        if (prioridad != null && !prioridad.isBlank()) {
            spec = spec.and(TicketSpecifications.conPrioridad(prioridad));
        }
        if (estado != null && !estado.isBlank()) {
            spec = spec.and(TicketSpecifications.conEstado(estado));
        }
        if (fecha != null) {
            spec = spec.and(TicketSpecifications.conFechaCreacion(fecha));
        }
        return spec;
    }

    private TicketPaginaDTO paginarTickets(Specification<TicketEntity> spec, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina - 1, tamano, Sort.by("idTicket").descending());
        Page<TicketEntity> resultado = repo.findAll(spec, pageable);

        List<TicketDTO> tickets = resultado.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());
        return new TicketPaginaDTO(tickets, resultado.getTotalElements(), resultado.getTotalPages(), pagina);
    }

    @Transactional
    public boolean actualizarEstado(Long id, @Valid TicketEstadoDTO dto) {
        TicketEntity ticket = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con ID: " + id + " no existe"));

        ticket.setEstado(dto.getEstado());
        repo.save(ticket);
        return true;
    }

    private static final List<String> diasSemana = List.of("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom");

    public List<TicketResumenDiaDTO> obtenerResumenSemanal(Long idUsuario) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime fin = hoy.atTime(LocalTime.MAX);

        List<TicketEntity> tickets = repo.findByCreador_IdUsuarioAndFechaCreacionBetween(idUsuario, inicio, fin);

        Map<DayOfWeek, Long> conteosPorDia = tickets.stream().collect(Collectors.groupingBy(t -> t.getFechaCreacion().getDayOfWeek(), Collectors.counting()));

        List<TicketResumenDiaDTO> resumen = new ArrayList<>();
        for (DayOfWeek dia : DayOfWeek.values()) {
            resumen.add(new TicketResumenDiaDTO(diasSemana.get(dia.getValue() - 1), conteosPorDia.getOrDefault(dia, 0L)));
        }
        return resumen;
    }

    public Map<String, Long> obtenerResumenMensual(LocalDate fechaInicio, LocalDate fechaFin) {
        // Si no se pasan fechas usamos el mes actual, que es lo que promete
        // el titulo de la tarjeta del dashboard ("Durante este mes").
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        // El cierre se deriva del mes de fechaInicio, no del mes actual: si
        // llega solo fechaInicio, cerrar contra hoy daria un rango incoherente.
        if (fechaFin == null) {
            fechaFin = fechaInicio.withDayOfMonth(fechaInicio.lengthOfMonth());
        }

        if (fechaFin.isBefore(fechaInicio)) {
            LocalDate temp = fechaInicio;
            fechaInicio = fechaFin;
            fechaFin = temp;
            log.warn("Fechas invertidas, se corrigieron automáticamente.");
        }

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        // Las dos cifras son de FLUJO: cuanto entro y cuanto salio durante el
        // periodo. Agrupar TICKETS.ESTADO sobre FECHA_CREACION responde algo
        // distinto ("de lo creado este mes, cuanto esta cerrado hoy").

        // Entradas: tickets creados dentro del periodo (tabla TICKETS).
        Long abiertos = repo.contarTicketsTotalesRangoFechas(inicio, fin);

        // Salidas: tickets que pasaron a Resuelto/Cerrado dentro del periodo.
        // Sale de BITACORAS porque TICKETS no guarda la fecha de cierre.
        Long cerrados = bitacoraRepo.contarTicketsCerradosEnRango(inicio, fin);

        Map<String, Long> resumen = new HashMap<>();
        resumen.put("ticketsAbiertos", abiertos != null ? abiertos : 0L);
        resumen.put("ticketsCerrados", cerrados != null ? cerrados : 0L);

        log.info("Resumen {} a {}: {} abiertos, {} cerrados",
                fechaInicio, fechaFin, resumen.get("ticketsAbiertos"), resumen.get("ticketsCerrados"));
        return resumen;
    }
}