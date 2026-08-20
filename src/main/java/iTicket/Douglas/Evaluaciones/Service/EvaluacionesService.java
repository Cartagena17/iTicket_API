package iTicket.Douglas.Evaluaciones.Service;

import iTicket.Douglas.Evaluaciones.DTO.EvaluacionesDTO;
import iTicket.Douglas.Evaluaciones.Entity.EvaluacionesEntity;
import iTicket.Douglas.Evaluaciones.Repository.EvaluacionesRepository;
import iTicket.Douglas.Exception.RecursoDuplicadoException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Tickets.DTO.TicketEstadoDTO;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Tickets.Service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public EvaluacionesDTO nuevaEvaluacion(@Valid EvaluacionesDTO dto) {
        if (repo.findByTicket_IdTicket(dto.getIdTicket()).isPresent()) {
            throw new RecursoDuplicadoException("El ticket con ID " + dto.getIdTicket() + " ya cuenta con una evaluación.");
        }

        TicketEntity ticketExistente = ticketRepository.findById(dto.getIdTicket())
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con ID " + dto.getIdTicket() + " no existe."));

        EvaluacionesEntity entity = convertirAEntity(dto, ticketExistente);
        EvaluacionesEntity entitySave = repo.save(entity);

        TicketEstadoDTO dtoT = new TicketEstadoDTO();
        dtoT.setEstado("Cerrado");
        ticketService.actualizarEstado(dto.getIdTicket(), dtoT);

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
        }

        return dto;
    }
}