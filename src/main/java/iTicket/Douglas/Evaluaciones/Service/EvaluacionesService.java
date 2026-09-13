package iTicket.Douglas.Evaluaciones.Service;

import iTicket.Douglas.Bitacoras.Repository.BitacoraRepository;
import iTicket.Douglas.Evaluaciones.DTO.EvaluacionesDTO;
import iTicket.Douglas.Evaluaciones.DTO.MetricasDTO;
import iTicket.Douglas.Evaluaciones.Entity.EvaluacionesEntity;
import iTicket.Douglas.Evaluaciones.Repository.EvaluacionesRepository;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoDuplicadoException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Tickets.DTO.TicketEstadoDTO;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Tickets.Service.TicketService;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvaluacionesService {

    private final EvaluacionesRepository repo;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final UsuarioRepository usuarioRepository;
    private final BitacoraRepository bitacoraRepo;

    private String resolverTipoDepartamento(Long idUsuarioAdmin) {
        return usuarioRepository.findById(idUsuarioAdmin)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún usuario con id: " + idUsuarioAdmin))
                .getDepartamento().getTipoDepartamento();
    }

    @Transactional
    public EvaluacionesDTO nuevaEvaluacion(@Valid EvaluacionesDTO dto,  Long idUsuarioCreador) {
        if (repo.findByTicket_IdTicket(dto.getIdTicket()).isPresent()) {
            throw new RecursoDuplicadoException("El ticket con ID " + dto.getIdTicket() + " ya cuenta con una evaluación.");
        }

        TicketEntity ticketExistente = ticketRepository.findById(dto.getIdTicket())
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con ID " + dto.getIdTicket() + " no existe."));

        if (!ticketExistente.getCreador().getIdUsuario().equals(idUsuarioCreador)) {
            throw new OperacionInvalidaException("Solo el creador del ticket puede evaluarlo.");
        }

        if (!"Resuelto".equalsIgnoreCase(ticketExistente.getEstado())) {
            throw new OperacionInvalidaException("Solo los tickets en estado 'Resuelto' pueden ser evaluados. El ticket actualmente está: " + ticketExistente.getEstado());
        }

        EvaluacionesEntity entity = convertirAEntity(dto, ticketExistente);
        EvaluacionesEntity entitySave = repo.save(entity);

        TicketEstadoDTO dtoT = new TicketEstadoDTO();
        dtoT.setEstado("Cerrado");
        ticketService.actualizarEstado(dto.getIdTicket(), dtoT, idUsuarioCreador);

        log.info("Nueva evaluación registrada: " + entitySave.getIdEvaluacion());
        return convertirADTO(entitySave);
    }

    public List<EvaluacionesDTO> obtenerTodas() {
        return repo.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public EvaluacionesDTO buscarPorId(Long id) {
        EvaluacionesEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una evaluación con id " + id));
        return convertirADTO(entidad);
    }

    public EvaluacionesDTO buscarPorTicket(Long idTicket) {
        EvaluacionesEntity entidad = repo.findByTicket_IdTicket(idTicket)
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con id " + idTicket + " no tiene evaluación registrada"));
        return convertirADTO(entidad);
    }

    @Transactional
    public EvaluacionesDTO actualizarEvaluacion(Long id, @Valid EvaluacionesDTO dto) {
        EvaluacionesEntity entity = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La evaluación con ID " + id + " no existe."));

        if (dto.getIdTicket() != null && !dto.getIdTicket().equals(entity.getTicket().getIdTicket())) {
            if (repo.findByTicket_IdTicket(dto.getIdTicket()).isPresent()) {
                throw new RecursoDuplicadoException("El nuevo ticket con ID " + dto.getIdTicket() + " ya cuenta con una evaluación.");
            }
            TicketEntity ticketExistente = ticketRepository.findById(dto.getIdTicket())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con ID " + dto.getIdTicket() + " no existe."));
            entity.setTicket(ticketExistente);
        }

        entity.setCalificacion(dto.getCalificacion());
        entity.setComentario(dto.getComentario());

        EvaluacionesEntity entitySave = repo.save(entity);
        log.info("Evaluación con id " + id + " actualizada");
        return convertirADTO(entitySave);
    }

    @Transactional
    public boolean eliminarEvaluacion(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public long contarEvaluaciones() {
        return repo.countEvaluaciones();
    }

    public Page<EvaluacionesDTO> obtenerEvaluacionesPaginadas(Long idUsuarioAdmin, String busqueda, Double calificacion, LocalDate fecha, Pageable pageable) {
        String tipoDepartamento = resolverTipoDepartamento(idUsuarioAdmin);
        String filtroBusqueda = (busqueda != null && !busqueda.trim().isEmpty()) ? busqueda.trim() : null;
        Double filtroCalificacion = (calificacion != null && calificacion > 0) ? calificacion : null;

        LocalDateTime fechaInicio = (fecha != null) ? fecha.atStartOfDay() : null;
        LocalDateTime fechaFin = (fecha != null) ? fecha.atTime(LocalTime.MAX) : null;

        Page<EvaluacionesEntity> paginaEntities = repo.buscarPorFiltrosPaginado(tipoDepartamento, filtroBusqueda, filtroCalificacion, fechaInicio, fechaFin, pageable);
        return paginaEntities.map(this::convertirADTO);
    }

    public MetricasDTO obtenerMetricas(Long idUsuarioAdmin, String busqueda, Double calificacion, LocalDate fecha) {
        String tipoDepartamento = resolverTipoDepartamento(idUsuarioAdmin);
        String filtroBusqueda = (busqueda != null && !busqueda.trim().isEmpty()) ? busqueda.trim() : null;
        Double filtroCalificacion = (calificacion != null && calificacion > 0) ? calificacion : null;

        LocalDateTime fechaInicio = (fecha != null) ? fecha.atStartOfDay() : null;
        LocalDateTime fechaFin = (fecha != null) ? fecha.atTime(LocalTime.MAX) : null;

        Object[] res = repo.obtenerMetricasRaw(tipoDepartamento, filtroBusqueda, filtroCalificacion, fechaInicio, fechaFin);

        if (res == null || res.length == 0 || res[0] == null) {
            return new MetricasDTO(0L, 0.0, 0L, 0L);
        }

        Object[] fila = (res[0] instanceof Object[]) ? (Object[]) res[0] : res;

        Long total = (fila.length > 0 && fila[0] != null) ? ((Number) fila[0]).longValue() : 0L;
        Double promedioRaw = (fila.length > 1 && fila[1] != null) ? ((Number) fila[1]).doubleValue() : 0.0;
        Double promedio = Math.round(promedioRaw * 10.0) / 10.0;
        Long positivas = (fila.length > 2 && fila[2] != null) ? ((Number) fila[2]).longValue() : 0L;
        Long negativas = (fila.length > 3 && fila[3] != null) ? ((Number) fila[3]).longValue() : 0L;

        return new MetricasDTO(total, promedio, positivas, negativas);
    }

    public List<Long> obtenerDistribucionCalificacionesPorTecnico(Long idUsuarioTecnico) {
        Object[] res = repo.obtenerDistribucionCalificacionesPorTecnico(idUsuarioTecnico);
        Object[] fila = (res != null && res.length > 0 && res[0] instanceof Object[]) ? (Object[]) res[0] : res;

        List<Long> distribucion = new java.util.ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Object valor = (fila != null && fila.length > i) ? fila[i] : null;
            distribucion.add(valor != null ? ((Number) valor).longValue() : 0L);
        }
        return distribucion;
    }

    public List<Long> obtenerDistribucionCalificacionesPorUsuario(Long idUsuarioCreador) {
        Object[] res = repo.obtenerDistribucionCalificacionesPorUsuario(idUsuarioCreador);
        Object[] fila = (res != null && res.length > 0 && res[0] instanceof Object[]) ? (Object[]) res[0] : res;

        List<Long> distribucion = new java.util.ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Object valor = (fila != null && fila.length > i) ? fila[i] : null;
            distribucion.add(valor != null ? ((Number) valor).longValue() : 0L);
        }
        return distribucion;
    }

    private EvaluacionesEntity convertirAEntity(EvaluacionesDTO dto, TicketEntity ticket) {
        EvaluacionesEntity objEntity = new EvaluacionesEntity();
        objEntity.setCalificacion(dto.getCalificacion());
        objEntity.setComentario(dto.getComentario());
        objEntity.setTicket(ticket);
        return objEntity;
    }

    private EvaluacionesDTO convertirADTO(EvaluacionesEntity entity) {
        EvaluacionesDTO dto = new EvaluacionesDTO();
        dto.setId(entity.getIdEvaluacion());
        dto.setCalificacion(entity.getCalificacion());
        dto.setComentario(entity.getComentario());

        if (entity.getTicket() != null) {
            dto.setIdTicket(entity.getTicket().getIdTicket());
            dto.setCodigoTicket(entity.getTicket().getCodigo());
            dto.setAsuntoTicket(entity.getTicket().getAsunto());
            bitacoraRepo.findFirstByIdTicketAndNuevoEstadoOrderByFechaHoraDesc(entity.getTicket().getIdTicket(), "Cerrado")
                    .ifPresent(bitacora -> dto.setFechaEvaluacion(bitacora.getFechaHora()));

            if (entity.getTicket().getTecnicoAsignado() != null) {
                dto.setNombreTecnico(entity.getTicket().getTecnicoAsignado().getNombreUsuario());
            } else {
                dto.setNombreTecnico("Sin técnico asignado");
            }
        }
        return dto;
    }
}