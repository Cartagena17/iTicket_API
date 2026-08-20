package iTicket.Douglas.DetalleGeneral.Service;

import iTicket.Douglas.DetalleGeneral.DTO.DetalleGDTO;
import iTicket.Douglas.DetalleGeneral.Entity.DetalleGEntity;
import iTicket.Douglas.DetalleGeneral.Repository.DetalleGRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetalleGService {

    private final DetalleGRepository repo;
    private final TicketRepository ticketRepo;

    private DetalleGEntity convertirAEntity(@Valid DetalleGDTO dto) {
        DetalleGEntity entity = new DetalleGEntity();
        entity.setDescripcionUbicacion(dto.getDescripcionUbicacion());
        entity.setTicket(buscarTicket(dto.getTicket()));
        return entity;
    }

    private DetalleGDTO convertirADTO(@Valid DetalleGEntity entity) {
        DetalleGDTO dto = new DetalleGDTO();
        dto.setIdDetalleGeneral(entity.getIdDetalleGeneral());
        dto.setDescripcionUbicacion(entity.getDescripcionUbicacion());
        dto.setTicket(entity.getTicket().getIdTicket());
        dto.setAsunto(entity.getTicket().getAsunto());
        return dto;
    }

    @Transactional
    public DetalleGDTO nuevoDetalleG(@Valid DetalleGDTO dto) {
        DetalleGEntity entity = convertirAEntity(dto);
        DetalleGEntity entitySave = repo.save(entity);
        log.info("Nuevo detalle general registrado: " + entitySave.getIdDetalleGeneral());
        return convertirADTO(entitySave);
    }

    public List<DetalleGDTO> obtenerDetallesG() {
        List<DetalleGEntity> datos = repo.findAll();
        return datos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Transactional
    public DetalleGDTO actualizarDetalleG(Long id, @Valid DetalleGDTO dto) {
        DetalleGEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un detalle general con id " + id));

        entidad.setDescripcionUbicacion(dto.getDescripcionUbicacion());
        entidad.setTicket(buscarTicket(dto.getTicket()));
        DetalleGEntity datosGuardados = repo.save(entidad);
        log.info("Detalle general con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarDetalleG(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public DetalleGDTO obtenerDetallesIdTicket(Long idTicket) {
        DetalleGEntity entidad = repo.findByTicket_IdTicket(idTicket)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún detalle general para el ticket: " + idTicket));
        return convertirADTO(entidad);
    }

    private TicketEntity buscarTicket(Long id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún ticket con id: " + id));
    }

    @Transactional
    public DetalleGDTO actualizarDetalle(@Valid DetalleGDTO dto) {
        TicketEntity ticket = buscarTicket(dto.getTicket());
        Optional<DetalleGEntity> existente = repo.findByTicket(ticket);

        DetalleGEntity entity = existente.orElseGet(DetalleGEntity::new);
        entity.setDescripcionUbicacion(dto.getDescripcionUbicacion());
        entity.setTicket(ticket);

        DetalleGEntity entitySave = repo.save(entity);
        log.info("Detalle general actualizado/creado para ticket: " + ticket.getIdTicket());
        return convertirADTO(entitySave);
    }
}