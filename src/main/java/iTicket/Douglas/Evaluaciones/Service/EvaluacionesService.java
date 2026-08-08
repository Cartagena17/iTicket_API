package iTicket.Douglas.Evaluaciones.Service;

import iTicket.Douglas.Evaluaciones.DTO.EvaluacionesDTO;
import iTicket.Douglas.Evaluaciones.Entity.EvaluacionesEntity;
import iTicket.Douglas.Evaluaciones.Repository.EvaluacionesRepository;
import iTicket.Douglas.Tickets.DTO.TicketEstadoDTO;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Tickets.Service.TicketService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            // 0. Un ticket solo puede tener una única evaluación
            if (repo.findByTicket_IdTicket(dto.getIdTicket()).isPresent()) {
                throw new RuntimeException("El Ticket con ID " + dto.getIdTicket() + " ya cuenta con una evaluación.");
            }

            // 1.Buscamos el ticket en la BD. Si no existe, lanzamos un error.
            TicketEntity ticketExistente = ticketRepository.findById(dto.getIdTicket())
                    .orElseThrow(() -> new RuntimeException("El Ticket con ID " + dto.getIdTicket() + " no existe."));

            // 2. Convertimos a Entity pasándole el Ticket real encontrado
            EvaluacionesEntity entity = convertirAEntity(dto, ticketExistente);

            // 3. Guardamos la evaluación
            EvaluacionesEntity entitySave = repo.save(entity);

            TicketEstadoDTO dtoT = new TicketEstadoDTO();
            dtoT.setEstado("Cerrado");
            ticketService.actualizarEstado(dto.getIdTicket(), dtoT);
            // 4. Retornamos la respuesta mapeada a DTO
            return convertirADTO(entitySave);

        } catch (Exception e) {
            log.error("Error al registrar la evaluación: " + e.getMessage());
            throw new RuntimeException("Error al registrar la evaluación: " + e.getMessage());
        }
    }

    public List<EvaluacionesDTO> obtenerTodas() {
        try {
            return repo.findAll().stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener las evaluaciones: " + e.getMessage());
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

            // Validar cambio de ticket
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
            log.error("Error al actualizar la evaluación: " + e.getMessage());
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

    // Método helper para convertir DTO a Entity
    private EvaluacionesEntity convertirAEntity(EvaluacionesDTO dto, TicketEntity ticket) {
        EvaluacionesEntity objEntity = new EvaluacionesEntity();
        objEntity.setCalificacion(dto.getCalificacion());
        objEntity.setComentario(dto.getComentario());
        objEntity.setTicket(ticket); // Le asignamos el ticket REAL de la BD
        return objEntity;
    }

    // Método helper para convertir Entity a DTO
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