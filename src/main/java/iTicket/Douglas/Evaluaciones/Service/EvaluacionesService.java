package iTicket.Douglas.Evaluaciones.Service;

import iTicket.Douglas.Evaluaciones.DTO.EvaluacionesDTO;
import iTicket.Douglas.Evaluaciones.DTO.MetricasDTO;
import iTicket.Douglas.Evaluaciones.Entity.EvaluacionesEntity;
import iTicket.Douglas.Evaluaciones.Repository.EvaluacionesRepository;
import iTicket.Douglas.Tickets.DTO.TicketEstadoDTO;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Tickets.Service.TicketService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EvaluacionesService {

    private final EvaluacionesRepository repo;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    @Transactional
    public EvaluacionesDTO nuevaEvaluacion(@Valid EvaluacionesDTO dto) {
        try {
            if (repo.findByTicket_IdTicket(dto.getIdTicket()).isPresent()) {
                throw new RuntimeException("El Ticket con ID " + dto.getIdTicket() + " ya cuenta con una evaluación.");
            }

            TicketEntity ticketExistente = ticketRepository.findById(dto.getIdTicket())
                    .orElseThrow(() -> new RuntimeException("El Ticket con ID " + dto.getIdTicket() + " no existe."));

            if (!"Resuelto".equalsIgnoreCase(ticketExistente.getEstado())) {
                throw new RuntimeException("Accion DENEGADA: Solo los tickets en estado 'Resuelto' pueden ser evaluados. El ticket actualmente esta: " + ticketExistente.getEstado());
            }

            EvaluacionesEntity entity = convertirAEntity(dto, ticketExistente);
            EvaluacionesEntity entitySave = repo.save(entity);

            TicketEstadoDTO dtoT = new TicketEstadoDTO();
            dtoT.setEstado("Cerrado");
            ticketService.actualizarEstado(dto.getIdTicket(), dtoT);

            return convertirADTO(entitySave);

        } catch (Exception e) {
            log.error("Error al registrar la evaluación: {}", e.getMessage());
            throw new RuntimeException("Error al registrar la evaluación: " + e.getMessage());
        }
    }

    public List<EvaluacionesDTO> obtenerTodas() {
        try {
            return repo.findAll().stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener las evaluaciones: {}", e.getMessage());
            throw new RuntimeException("Error al obtener las evaluaciones");
        }
    }

    public EvaluacionesDTO buscarPorId(Long id) {
        Optional<EvaluacionesEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public EvaluacionesDTO buscarPorTicket(Long idTicket) {
        Optional<EvaluacionesEntity> entidadOpcional = repo.findByTicket_IdTicket(idTicket);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    @Transactional
    public EvaluacionesDTO actualizarEvaluacion(Long id, @Valid EvaluacionesDTO dto) {
        try {
            EvaluacionesEntity entity = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("La evaluación con ID " + id + " no existe."));

            if (dto.getIdTicket() != null && !dto.getIdTicket().equals(entity.getTicket().getIdTicket())) {
                if (repo.findByTicket_IdTicket(dto.getIdTicket()).isPresent()) {
                    throw new RuntimeException("El nuevo Ticket con ID " + dto.getIdTicket() + " ya cuenta con una evaluación.");
                }
                TicketEntity ticketExistente = ticketRepository.findById(dto.getIdTicket())
                        .orElseThrow(() -> new RuntimeException("El Ticket con ID " + dto.getIdTicket() + " no existe."));
                entity.setTicket(ticketExistente);
            }

            entity.setCalificacion(dto.getCalificacion());
            entity.setComentario(dto.getComentario());

            EvaluacionesEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al actualizar la evaluación: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar la evaluación: " + e.getMessage());
        }
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

    public Page<EvaluacionesDTO> obtenerEvaluacionesPaginadas(String busqueda, Double calificacion, LocalDate fecha, Pageable pageable) {
        try {
            String filtroBusqueda = (busqueda != null && !busqueda.trim().isEmpty()) ? busqueda.trim() : null;
            Double filtroCalificacion = (calificacion != null && calificacion > 0) ? calificacion : null;

            LocalDateTime fechaInicio = (fecha != null) ? fecha.atStartOfDay() : null;
            LocalDateTime fechaFin = (fecha != null) ? fecha.atTime(LocalTime.MAX) : null;

            Page<EvaluacionesEntity> paginaEntities = repo.buscarPorFiltrosPaginado(filtroBusqueda, filtroCalificacion, fechaInicio, fechaFin, pageable);
            return paginaEntities.map(this::convertirADTO);

        } catch (Exception e) {
            log.error("Error al obtener las evaluaciones paginadas: {}", e.getMessage());
            throw new RuntimeException("Error al consultar evaluaciones paginadas: " + e.getMessage());
        }
    }

    public MetricasDTO obtenerMetricas(String busqueda, Double califiacion, LocalDate fecha) {
        String filtroBusqueda = (busqueda != null && !busqueda.trim().isEmpty() ? busqueda.trim() : null);
        Double filtroCalificacion = (califiacion != null && califiacion > 0) ? califiacion : null;

        LocalDateTime fechaInicio = (fecha != null) ? fecha.atStartOfDay() : null;
        LocalDateTime fechaFin = (fecha != null) ? fecha.atTime(LocalTime.MAX) : null;

        Object[] res = repo.obtenerMetricasRaw(filtroBusqueda, filtroCalificacion, fechaInicio, fechaFin);

        if (res == null || res.length == 0 || res[0] == null) {
            return new MetricasDTO(0L, 0.0, 0L, 0L);
        }

        // Manejo dinámico seguro para arreglos directos (Object[]) o anidados (Object[][])
        Object[] fila = (res[0] instanceof Object[]) ? (Object[]) res[0] : res;

        Long total = (fila.length > 0 && fila[0] != null) ? ((Number) fila[0]).longValue() : 0L;
        Double promedioRaw = (fila.length > 1 && fila[1] != null) ? ((Number) fila[1]).doubleValue() : 0.0;
        Double promedio = Math.round(promedioRaw * 10.0) / 10.0;
        Long positivas = (fila.length > 2 && fila[2] != null) ? ((Number) fila[2]).longValue() : 0L;
        Long negativas = (fila.length > 3 && fila[3] != null) ? ((Number) fila[3]).longValue() : 0L;

        return new MetricasDTO(total, promedio, positivas, negativas);
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
            dto.setFechaEvaluacion(entity.getTicket().getFechaCreacion());

            if (entity.getTicket().getTecnicoAsignado() != null) {
                dto.setNombreTecnico(entity.getTicket().getTecnicoAsignado().getNombreUsuario());
            } else {
                dto.setNombreTecnico("Sin técnico asignado");
            }
        }
        return dto;
    }
}